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
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationForm;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.repository.project.projectinformation.ProjectInformationRepository;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectInformationServiceTest {

  @Mock
  private ProjectInformationRepository projectInformationRepository;

  @Mock
  private SpringValidatorAdapter validator;

  private ProjectInformationService projectInformationService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  private ProjectInformation projectInformation;

  @Before
  public void setUp() throws Exception {
    projectInformationService = new ProjectInformationService(
        projectInformationRepository,
        validator
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
  }

  @Test
  public void createOrUpdate_existingDetail() {
    when(projectInformationRepository.findByProjectDetail(details))
        .thenReturn(Optional.of(ProjectInformationUtil.getProjectInformation_withCompleteDetails(details)));
    projectInformation = projectInformationService.createOrUpdate(details, ProjectInformationUtil.getCompleteForm());

    assertThat(projectInformation.getProjectDetail()).isEqualTo(details);
    assertThat(projectInformation.getFieldStage()).isEqualTo(ProjectInformationUtil.FIELD_STAGE);
    assertThat(projectInformation.getProjectTitle()).isEqualTo(ProjectInformationUtil.PROJECT_TITLE);
    assertThat(projectInformation.getProjectSummary()).isEqualTo(ProjectInformationUtil.PROJECT_SUMMARY);
  }

  @Test
  public void getForm_noExistingDetail() {
    when(projectInformationRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    ProjectInformationForm form = projectInformationService.getForm(details);

    assertThat(form.getFieldStage()).isNull();
    assertThat(form.getProjectTitle()).isNull();
    assertThat(form.getProjectSummary()).isNull();
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
  }


  @Test
  public void validate_partial() {
    projectInformationService.validate(
        new ProjectInformationForm(),
        null,
        ValidationType.PARTIAL
    );
    verify(validator, times(0)).validate(any(), any(), any());
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

    verify(validator, times(1)).validate(form, bindingResult, FullValidation.class);
  }
}
