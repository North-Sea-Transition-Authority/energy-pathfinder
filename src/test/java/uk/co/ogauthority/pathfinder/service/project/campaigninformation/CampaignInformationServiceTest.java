package uk.co.ogauthority.pathfinder.service.project.campaigninformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.PublishedProject;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignInformation;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignProject;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.project.campaigninformation.CampaignInformationForm;
import uk.co.ogauthority.pathfinder.model.form.project.campaigninformation.CampaignInformationFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.campaigninformation.CampaignInformationValidationHint;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.project.campaigninformation.CampaignInformationRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.CampaignInformationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class CampaignInformationServiceTest {

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private CampaignInformationRepository campaignInformationRepository;

  @Mock
  private ValidationService validationService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  @Mock
  private CampaignInformationFormValidator campaignInformationFormValidator;

  @Mock
  private CampaignProjectService campaignProjectService;

  private CampaignInformationService campaignInformationService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    campaignInformationService = new CampaignInformationService(
        projectSetupService,
        campaignInformationRepository,
        validationService,
        entityDuplicationService,
        campaignInformationFormValidator,
        campaignProjectService
    );
    when(campaignInformationRepository.save(any(CampaignInformation.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createOrUpdateCampaignInformation_whenNoDetailFound_assertPopulatedProperties() {
    when(campaignInformationRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.empty());

    var form = CampaignInformationTestUtil.createCampaignInformationForm();

    var campaignInformation = campaignInformationService.createOrUpdateCampaignInformation(
        form,
        projectDetail
    );

    assertThat(campaignInformation.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(campaignInformation.getScopeDescription()).isEqualTo(form.getScopeDescription());
    assertThat(campaignInformation.isPartOfCampaign()).isEqualTo(form.getIsPartOfCampaign());

    verifyCreateOrUpdateCampaignInformationInteractions(campaignInformation, form.getProjectsIncludedInCampaign());
  }

  @Test
  public void createOrUpdateCampaignInformation_whenDetailFound_assertPopulatedProperties() {
    var campaignInformation = CampaignInformationTestUtil.createCampaignInformation();
    when(campaignInformationRepository.findByProjectDetail(projectDetail))
        .thenReturn(Optional.of(campaignInformation));

    var form = new CampaignInformationForm();
    form.setScopeDescription("test");
    form.setIsPartOfCampaign(true);

    campaignInformation = campaignInformationService.createOrUpdateCampaignInformation(
        form,
        projectDetail
    );

    assertThat(campaignInformation.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(campaignInformation.getScopeDescription()).isEqualTo(form.getScopeDescription());
    assertThat(campaignInformation.isPartOfCampaign()).isEqualTo(form.getIsPartOfCampaign());

    verifyCreateOrUpdateCampaignInformationInteractions(campaignInformation, form.getProjectsIncludedInCampaign());
  }

  @Test
  public void getForm_whenNoExistingEntity_thenEmptyFormReturned() {

    when(campaignInformationRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.empty());

    CampaignInformationForm form = campaignInformationService.getForm(projectDetail);

    assertThat(form.getIsPartOfCampaign()).isNull();
    assertThat(form.getScopeDescription()).isNull();
    assertThat(form.getProjectsIncludedInCampaign()).isEmpty();
  }

  @Test
  public void getForm_whenExistingEntity_thenPopulatedFormReturned() {

    final var campaignInformation = CampaignInformationTestUtil.createCampaignInformation();
    when(campaignInformationRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.of(campaignInformation));

    final var publishedProject = new PublishedProject();
    publishedProject.setProjectId(100);

    final var campaignProject = new CampaignProject();
    campaignProject.setPublishedProject(publishedProject);

    final var campaignProjects = List.of(campaignProject);
    when(campaignProjectService.getCampaignProjects(projectDetail)).thenReturn(campaignProjects);

    CampaignInformationForm form = campaignInformationService.getForm(projectDetail);

    assertThat(form.getIsPartOfCampaign()).isEqualTo(campaignInformation.isPartOfCampaign());
    assertThat(form.getScopeDescription()).isEqualTo(campaignInformation.getScopeDescription());

    final var publishedProjectIds = campaignProjects
        .stream()
        .map(selectedCampaignProject -> selectedCampaignProject.getPublishedProject().getProjectId())
        .collect(Collectors.toList());

    assertThat(form.getProjectsIncludedInCampaign()).isEqualTo(publishedProjectIds);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getOrError_whenNotFound_thenException() {
    when(campaignInformationRepository.findByProjectDetail(projectDetail))
        .thenReturn(Optional.empty());

    campaignInformationService.getOrError(projectDetail);
  }

  @Test
  public void getOrError_whenFound_thenReturnCampaign() {
    var campaignInformation = CampaignInformationTestUtil.createCampaignInformation();
    when(campaignInformationRepository.findByProjectDetail(projectDetail))
        .thenReturn(Optional.of(campaignInformation));

    var result = campaignInformationService.getOrError(projectDetail);
    assertThat(result).isEqualTo(campaignInformation);
  }

  @Test
  public void canShowInTaskList_true() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(projectDetail, ProjectTask.CAMPAIGN_INFORMATION)).thenReturn(true);
    assertThat(campaignInformationService.canShowInTaskList(projectDetail)).isTrue();
  }

  @Test
  public void canShowInTaskList_false() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(projectDetail, ProjectTask.CAMPAIGN_INFORMATION)).thenReturn(false);
    assertThat(campaignInformationService.canShowInTaskList(projectDetail)).isFalse();
  }

  @Test
  public void isComplete_whenInvalid_thenFalse() {

    final var bindingResult = new BeanPropertyBindingResult(CampaignInformationForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(validationService.validate(any(), any(), any(ValidationType.class))).thenReturn(bindingResult);

    final var isComplete = campaignInformationService.isComplete(projectDetail);

    assertThat(isComplete).isFalse();
  }

  @Test
  public void isComplete_whenValid_thenTrue() {
    when(validationService.validate(any(), any(), any(ValidationType.class)))
        .thenReturn(ReverseRouter.emptyBindingResult());

    final var isComplete = campaignInformationService.isComplete(projectDetail);

    assertThat(isComplete).isTrue();
  }

  @Test
  public void copySectionData_verifyInteractions() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    final var fromCampaignInformation = CampaignInformationTestUtil.createCampaignInformation();
    fromCampaignInformation.setProjectDetail(fromProjectDetail);

    when(campaignInformationRepository.findByProjectDetail(fromProjectDetail))
        .thenReturn(Optional.of(fromCampaignInformation));

    final var toCampaignInformation = CampaignInformationTestUtil.createCampaignInformation();
    toCampaignInformation.setProjectDetail(toProjectDetail);

    when(entityDuplicationService.duplicateEntityAndSetNewParent(any(), any(), any()))
        .thenReturn(toCampaignInformation);

    campaignInformationService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, times(1)).duplicateEntityAndSetNewParent(
        fromCampaignInformation,
        toProjectDetail,
        CampaignInformation.class
    );

    verify(campaignProjectService, times(1)).copyCampaignProjectsToNewCampaign(
        fromCampaignInformation,
        toCampaignInformation
    );
  }

  @Test
  public void removeSectionData_verifyInteractions() {

    campaignInformationService.removeSectionData(projectDetail);

    verify(campaignInformationRepository, times(1)).deleteByProjectDetail(projectDetail);
    verify(campaignProjectService, times(1)).deleteAllCampaignProjects(projectDetail);
  }

  @Test
  public void validate_fullValidation_verifyInteractions() {
    callValidateMethodAndVerifyInteractions(ValidationType.FULL);
  }

  @Test
  public void validate_partialValidation_verifyInteractions() {
    callValidateMethodAndVerifyInteractions(ValidationType.PARTIAL);
  }

  private void callValidateMethodAndVerifyInteractions(ValidationType validationType) {

    final var form = new CampaignInformationForm();

    final var bindingResult = new BeanPropertyBindingResult(form, "form");

    campaignInformationService.validate(
        form,
        bindingResult,
        validationType,
        projectDetail
    );

    final var validationHint = new CampaignInformationValidationHint(validationType, projectDetail);

    verify(campaignInformationFormValidator, times(1)).validate(form, bindingResult, validationHint);
    verify(validationService, times(1)).validate(form, bindingResult, validationType);
  }

  private void verifyCreateOrUpdateCampaignInformationInteractions(CampaignInformation campaignInformation,
                                                                   List<Integer> campaignProjects) {
    verify(campaignInformationRepository, times(1)).save(campaignInformation);
    verify(campaignProjectService, times(1)).persistCampaignProjects(
        campaignInformation,
        campaignProjects
    );
  }
}