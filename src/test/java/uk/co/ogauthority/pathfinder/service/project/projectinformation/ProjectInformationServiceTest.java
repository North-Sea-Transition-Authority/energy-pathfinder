package uk.co.ogauthority.pathfinder.service.project.projectinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationForm;
import uk.co.ogauthority.pathfinder.repository.project.projectinformation.ProjectInformationRepository;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectInformationServiceTest {

  @Mock
  private ProjectInformationRepository projectInformationRepository;

  @Mock
  private ValidationService validationService;

  private ProjectInformationService projectInformationService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  private ProjectInformation projectInformation;

  @Before
  public void setUp() {
    projectInformationService = new ProjectInformationService(
        projectInformationRepository,
        validationService
    );

    when(projectInformationRepository.save(any(ProjectInformation.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createOrUpdate_newDetail() {
    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    projectInformation = projectInformationService.createOrUpdate(details, ProjectInformationUtil.getCompleteForm());

    assertThat(projectInformation.getProjectDetail()).isEqualTo(details);
    assertThat(projectInformation.getFieldStage()).isEqualTo(ProjectInformationUtil.FIELD_STAGE);
    assertThat(projectInformation.getProjectTitle()).isEqualTo(ProjectInformationUtil.PROJECT_TITLE);
    assertThat(projectInformation.getProjectSummary()).isEqualTo(ProjectInformationUtil.PROJECT_SUMMARY);
    assertThat(projectInformation.getContactName()).isEqualTo(ProjectInformationUtil.CONTACT_NAME);
    assertThat(projectInformation.getPhoneNumber()).isEqualTo(ProjectInformationUtil.PHONE_NUMBER);
    assertThat(projectInformation.getJobTitle()).isEqualTo(ProjectInformationUtil.JOB_TITLE);
    assertThat(projectInformation.getEmailAddress()).isEqualTo(ProjectInformationUtil.EMAIL);
  }

  @Test
  public void createOrUpdate_existingDetail() {
    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(ProjectInformationUtil.getProjectInformation_withCompleteDetails(details)));
    var form = ProjectInformationUtil.getCompleteForm();
    form.setName("New name");
    projectInformation = projectInformationService.createOrUpdate(details, form);

    assertThat(projectInformation.getProjectDetail()).isEqualTo(details);
    assertThat(projectInformation.getFieldStage()).isEqualTo(ProjectInformationUtil.FIELD_STAGE);
    assertThat(projectInformation.getProjectTitle()).isEqualTo(ProjectInformationUtil.PROJECT_TITLE);
    assertThat(projectInformation.getProjectSummary()).isEqualTo(ProjectInformationUtil.PROJECT_SUMMARY);
    assertThat(projectInformation.getContactName()).isEqualTo("New name");
    assertThat(projectInformation.getPhoneNumber()).isEqualTo(ProjectInformationUtil.PHONE_NUMBER);
    assertThat(projectInformation.getJobTitle()).isEqualTo(ProjectInformationUtil.JOB_TITLE);
    assertThat(projectInformation.getEmailAddress()).isEqualTo(ProjectInformationUtil.EMAIL);
  }

  @Test
  public void getForm_noExistingDetail() {
    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    ProjectInformationForm form = projectInformationService.getForm(details);

    assertThat(form.getFieldStage()).isNull();
    assertThat(form.getProjectTitle()).isNull();
    assertThat(form.getProjectSummary()).isNull();
    assertThat(form.getName()).isNull();
    assertThat(form.getPhoneNumber()).isNull();
    assertThat(form.getJobTitle()).isNull();
    assertThat(form.getEmailAddress()).isNull();
  }

  @Test
  public void getForm_existingDetail() {
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(details);
    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(projectInformation));
    ProjectInformationForm form = projectInformationService.getForm(details);

    assertThat(form.getFieldStage()).isEqualTo(projectInformation.getFieldStage());
    assertThat(form.getProjectTitle()).isEqualTo(projectInformation.getProjectTitle());
    assertThat(form.getProjectSummary()).isEqualTo(projectInformation.getProjectSummary());
    assertThat(form.getName()).isEqualTo(projectInformation.getContactName());
    assertThat(form.getPhoneNumber()).isEqualTo(projectInformation.getPhoneNumber());
    assertThat(form.getJobTitle()).isEqualTo(projectInformation.getJobTitle());
    assertThat(form.getEmailAddress()).isEqualTo(projectInformation.getEmailAddress());
  }


  @Test
  public void validate_partial() {
    var form = new ProjectInformationForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    projectInformationService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void validate_full() {
    var form = ProjectInformationUtil.getCompleteForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    projectInformationService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }
}
