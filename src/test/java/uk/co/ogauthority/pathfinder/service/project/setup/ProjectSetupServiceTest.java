package uk.co.ogauthority.pathfinder.service.project.setup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.tasks.ProjectTaskListSetup;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions.TaskListSectionQuestion;
import uk.co.ogauthority.pathfinder.model.form.project.setup.ProjectSetupForm;
import uk.co.ogauthority.pathfinder.model.form.project.setup.ProjectSetupFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.tasks.ProjectTaskListSetupRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectFormSectionServiceTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectTaskListSetupTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TaskListTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectSetupServiceTest {

  @Mock
  private ProjectTaskListSetupRepository projectTaskListSetupRepository;

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private ProjectSetupFormValidator projectSetupFormValidator;

  @Mock
  private ValidationService validationService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private ProjectSetupService projectSetupService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  private final ProjectTaskListSetup setup = ProjectTaskListSetupTestUtil.getProjectTaskListSetupWithFieldStageIndependentSectionsAnswered(details);

  private final ProjectTaskListSetup decomSetup = ProjectTaskListSetupTestUtil.getProjectSetupWithDecommissioningSectionsAnswered(details);

  @Before
  public void setUp() {
    projectSetupService = new ProjectSetupService(
        projectTaskListSetupRepository,
        projectInformationService,
        projectSetupFormValidator,
        validationService,
        entityDuplicationService
    );

    when(projectTaskListSetupRepository.save(any(ProjectTaskListSetup.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void getSectionQuestionsForProjectDetail_whenFieldStageSelected_thenReturnOnlySectionsSupportingFieldStage() {

    var selectedFieldStage = FieldStage.DEVELOPMENT;

    var expectedSectionQuestions = Arrays.stream(TaskListSectionQuestion.values())
        .filter(sectionQuestion -> sectionQuestion.getApplicableFieldStages().contains(selectedFieldStage))
        .collect(Collectors.toList());

    when(projectInformationService.getFieldStage(details)).thenReturn(Optional.of(selectedFieldStage));

    var resultingSectionQuestions = projectSetupService.getSectionQuestionsForProjectDetail(details);

    assertThat(resultingSectionQuestions).containsExactlyInAnyOrderElementsOf(expectedSectionQuestions);
  }

  @Test
  public void getSectionQuestionsForProjectDetail_whenNoFieldStageSelected_thenReturnOnlySectionsSupportingFieldStage() {

    var expectedSectionQuestions = Arrays.stream(TaskListSectionQuestion.values())
        .filter(sectionQuestion -> sectionQuestion.getApplicableFieldStages().containsAll(Set.of(FieldStage.values())))
        .collect(Collectors.toList());

    when(projectInformationService.getFieldStage(details)).thenReturn(Optional.empty());

    var resultingSectionQuestions = projectSetupService.getSectionQuestionsForProjectDetail(details);

    assertThat(resultingSectionQuestions).containsExactlyInAnyOrderElementsOf(expectedSectionQuestions);
  }

  @Test
  public void getProjectSetupModelAndView() {

    var form = new ProjectSetupForm();

    var modelAndView = projectSetupService.getProjectSetupModelAndView(details, form);

    assertThat(modelAndView.getViewName()).isEqualTo(ProjectSetupService.MODEL_AND_VIEW_PATH);

    var modelMap = modelAndView.getModel();

    assertThat(modelMap).containsOnlyKeys("sections", "form");
    assertThat(modelMap.get("sections")).isNotNull();
  }

  @Test
  public void createOrUpdateProjectTaskListSetup() {
    when(projectTaskListSetupRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    var form = ProjectTaskListSetupTestUtil.getProjectSetupFormWithFieldStageIndependentSectionsAnswered();
    var projectTaskListSetup = projectSetupService.createOrUpdateProjectTaskListSetup(details, form);
    assertThat(projectTaskListSetup.getTaskListSections()).containsExactlyInAnyOrderElementsOf(ProjectTaskListSetupTestUtil.FIELD_STAGE_INDEPENDENT_SECTIONS);
    assertThat(projectTaskListSetup.getTaskListAnswers()).containsExactlyInAnyOrderElementsOf(ProjectTaskListSetupTestUtil.FIELD_STAGE_INDEPENDENT_SETUP_ANSWERS);
    assertThat(projectTaskListSetup.getProjectDetail()).isEqualTo(details);
  }

  @Test
  public void createOrUpdateProjectTaskListSetup_decomRelated() {
    when(projectTaskListSetupRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    var form = ProjectTaskListSetupTestUtil.getProjectSetupFormWithDecommissioningSectionsAnswered();
    var projectTaskListSetup = projectSetupService.createOrUpdateProjectTaskListSetup(details, form);
    assertThat(projectTaskListSetup.getTaskListSections()).containsExactlyInAnyOrderElementsOf(ProjectTaskListSetupTestUtil.DECOMMISSIONING_FIELD_STAGE_SECTIONS);
    assertThat(projectTaskListSetup.getTaskListAnswers()).containsExactlyInAnyOrderElementsOf(ProjectTaskListSetupTestUtil.DECOMMISSIONING_FIELD_STAGE_SETUP_ANSWERS);
    assertThat(projectTaskListSetup.getProjectDetail()).isEqualTo(details);
  }

  @Test
  public void createOrUpdateProjectTaskListSetup_oldDetailsOverwritten() {
    when(projectTaskListSetupRepository.findByProjectDetail(details)).thenReturn(
        Optional.of(decomSetup)
    );
    var form = ProjectTaskListSetupTestUtil.getProjectSetupFormWithFieldStageIndependentSectionsAnswered();
    var projectTaskListSetup = projectSetupService.createOrUpdateProjectTaskListSetup(details, form);
    assertThat(projectTaskListSetup.getTaskListSections()).containsExactlyInAnyOrderElementsOf(ProjectTaskListSetupTestUtil.FIELD_STAGE_INDEPENDENT_SECTIONS);
    assertThat(projectTaskListSetup.getTaskListAnswers()).containsExactlyInAnyOrderElementsOf(ProjectTaskListSetupTestUtil.FIELD_STAGE_INDEPENDENT_SETUP_ANSWERS);
    assertThat(projectTaskListSetup.getProjectDetail()).isEqualTo(details);
  }

  @Test
  public void getForm_emptyWhenNoProjectTaskListSetupFound() {
    when(projectTaskListSetupRepository.findByProjectDetail(details)).thenReturn(Optional.empty());
    var form = projectSetupService.getForm(details);
    checkCommonFieldsMatch(new ProjectSetupForm(), form);
  }

  @Test
  public void getForm() {
    when(projectTaskListSetupRepository.findByProjectDetail(details)).thenReturn(
        Optional.of(setup)
      );
    var form = projectSetupService.getForm(details);
    checkCommonFieldsMatch(ProjectTaskListSetupTestUtil.getProjectSetupFormWithFieldStageIndependentSectionsAnswered(), form);
  }

  @Test
  public void getForm_decomRelated() {
    when(projectTaskListSetupRepository.findByProjectDetail(details)).thenReturn(
        Optional.of(decomSetup)
    );
    var form = projectSetupService.getForm(details);
    checkCommonFieldsMatch(ProjectTaskListSetupTestUtil.getProjectSetupFormWithDecommissioningSectionsAnswered(), form);
  }

  @Test
  public void getTaskListSectionQuestionsForForm() {
    var form = ProjectTaskListSetupTestUtil.getProjectSetupFormWithFieldStageIndependentSectionsAnswered();
    assertThat(projectSetupService.getTaskListSectionQuestionsFromForm(form)).containsExactlyInAnyOrderElementsOf(
        ProjectTaskListSetupTestUtil.FIELD_STAGE_INDEPENDENT_SECTIONS
    );
  }

  @Test
  public void getTaskListSectionQuestionsForForm_decomRelated() {
    var form = ProjectTaskListSetupTestUtil.getProjectSetupFormWithDecommissioningSectionsAnswered();
    assertThat(projectSetupService.getTaskListSectionQuestionsFromForm(form)).containsExactlyInAnyOrderElementsOf(
        ProjectTaskListSetupTestUtil.DECOMMISSIONING_FIELD_STAGE_SECTIONS
    );
  }

  @Test
  public void validate_whenPartial() {
    var form = new ProjectSetupForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    projectSetupService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL,
        details
    );

    verify(projectSetupFormValidator, times(1)).validate(any(), any(), any());
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void validate_whenFull() {
    var form = new ProjectSetupForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    projectSetupService.validate(
        form,
        bindingResult,
        ValidationType.FULL,
        details
    );

    verify(projectSetupFormValidator, times(1)).validate(any(), any(), any());
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void removeTaskListSetupSectionsNotApplicableToFieldStage_whenNoSetupSectionFound_thenNoDatabaseInteraction() {

    when(projectTaskListSetupRepository.findByProjectDetail(details)).thenReturn(Optional.empty());

    projectSetupService.removeTaskListSetupSectionsNotApplicableToFieldStage(details, FieldStage.DEVELOPMENT);

    verify(projectTaskListSetupRepository, never()).save(any());
  }

  @Test
  public void removeTaskListSetupSectionsNotApplicableToFieldStage_whenSectionsNotApplicableFound_thenVerifyCorrectSectionsAreRemoved() {

    var nonDecommissioningFieldStage = FieldStage.HYDROGEN;

    var decommissioningFieldStageSetupEntityToTest = createProjectTaskListSetupEntityWithFieldStageSection(FieldStage.DECOMMISSIONING);

    when(projectTaskListSetupRepository.findByProjectDetail(details)).thenReturn(Optional.of(decommissioningFieldStageSetupEntityToTest));

    projectSetupService.removeTaskListSetupSectionsNotApplicableToFieldStage(details, nonDecommissioningFieldStage);

    // the saved entity should be the same as this one e.g. with the decommissioning related sections removed
    var nonDecommissioningFieldStageSetupEntity = createProjectTaskListSetupEntityWithFieldStageSection(nonDecommissioningFieldStage);

    verify(projectTaskListSetupRepository, times(1)).save(nonDecommissioningFieldStageSetupEntity);
  }

  private ProjectTaskListSetup createProjectTaskListSetupEntityWithFieldStageSection(FieldStage fieldStage) {

    var taskListSectionsForFieldStage = Arrays.stream(TaskListSectionQuestion.values())
        .filter(sectionQuestion -> sectionQuestion.getApplicableFieldStages().contains(fieldStage))
        .collect(Collectors.toList());

    var taskListAnswers = taskListSectionsForFieldStage
        .stream()
        .map(TaskListSectionQuestion::getYesAnswer)
        .collect(Collectors.toList());

    var sectionsSetupEntity = new ProjectTaskListSetup(details);
    sectionsSetupEntity.setTaskListSections(taskListSectionsForFieldStage);
    sectionsSetupEntity.setTaskListAnswers(taskListAnswers);

    return sectionsSetupEntity;
  }

  @Test
  public void removeSectionData() {
    projectSetupService.removeSectionData(details);

    verify(projectTaskListSetupRepository, times(1)).deleteByProjectDetail(details);
  }

  @Test
  public void copySectionData_verifyDuplicationServiceInteraction() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    final var fromTaskListSetup = ProjectTaskListSetupTestUtil.getProjectSetupWithDecommissioningSectionsAnswered(fromProjectDetail);
    when(projectTaskListSetupRepository.findByProjectDetail(fromProjectDetail)).thenReturn(Optional.of(fromTaskListSetup));

    projectSetupService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, times(1)).duplicateEntityAndSetNewParent(
        fromTaskListSetup,
        toProjectDetail,
        ProjectTaskListSetup.class
    );
  }

  @Test
  public void canShowInTaskList_whenInfrastructureProject_thenTrue() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);
    assertThat(projectSetupService.canShowInTaskList(projectDetail, Set.of(UserToProjectRelationship.OPERATOR)))
        .isTrue();
  }

  @Test
  public void canShowInTaskList_whenNotInfrastructureProject_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
    assertThat(projectSetupService.canShowInTaskList(projectDetail, Set.of(UserToProjectRelationship.OPERATOR)))
        .isFalse();
  }

  @Test
  public void canShowInTaskList_whenNullProjectType_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails();
    projectDetail.setProjectType(null);
    assertThat(projectSetupService.canShowInTaskList(projectDetail, Set.of(UserToProjectRelationship.OPERATOR)))
        .isFalse();
  }

  @Test
  public void canShowInTaskList_userToProjectRelationshipSmokeTest() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);
    ProjectFormSectionServiceTestUtil.canShowInTaskList_userToProjectRelationshipSmokeTest(
        projectSetupService,
        projectDetail,
        Set.of(UserToProjectRelationship.OPERATOR)
    );
  }

  @Test
  public void taskValidAndSelectedForProjectDetail_whenValidAndNotSelected_thenFalse() {
    final var task = TaskListTestUtil.DEFAULT_PROJECT_TASK;
    final var projectTypeForTask = task.getRelatedProjectTypes().stream().findFirst().orElse(ProjectType.INFRASTRUCTURE);
    final var projectDetail = ProjectUtil.getProjectDetails(projectTypeForTask);

    when(projectTaskListSetupRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.empty());

    final var isValidAndSelected = projectSetupService.taskValidAndSelectedForProjectDetail(projectDetail, task);
    assertThat(isValidAndSelected).isFalse();
  }

  @Test
  public void isTaskValidForProjectDetail_whenInfrastructureProject_thenTrue() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);
    assertThat(projectSetupService.isTaskValidForProjectDetail(projectDetail)).isTrue();
  }

  @Test
  public void isTaskValidForProjectDetail_whenNotInfrastructureProject_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
    assertThat(projectSetupService.isTaskValidForProjectDetail(projectDetail)).isFalse();
  }

  @Test
  public void taskValidAndSelectedForProjectDetail_whenNotValidAndNotSelected_thenFalse() {
    final var task = TaskListTestUtil.DEFAULT_PROJECT_TASK;
    final var projectDetail = ProjectUtil.getProjectDetails();
    projectDetail.setProjectType(null);

    when(projectTaskListSetupRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.empty());

    final var isValidAndSelected = projectSetupService.taskValidAndSelectedForProjectDetail(projectDetail, task);
    assertThat(isValidAndSelected).isFalse();
  }

  @Test
  public void taskValidAndSelectedForProjectDetail_whenValidAndSelected_thenTrue() {
    final var task = TaskListTestUtil.DEFAULT_PROJECT_TASK;
    final var projectTypeForTask = task.getRelatedProjectTypes().stream().findFirst().orElse(ProjectType.INFRASTRUCTURE);
    final var projectDetail = ProjectUtil.getProjectDetails(projectTypeForTask);

    when(projectTaskListSetupRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.of(setup));

    final var isValidAndSelected = projectSetupService.taskValidAndSelectedForProjectDetail(projectDetail, task);
    assertThat(isValidAndSelected).isTrue();
  }

  @Test
  public void getSupportedProjectTypes_verifyInfrastructure() {
    assertThat(projectSetupService.getSupportedProjectTypes()).containsExactly(ProjectType.INFRASTRUCTURE);
  }

  @Test
  public void alwaysCopySectionData_verifyFalse() {
    assertThat(projectSetupService.alwaysCopySectionData(details)).isFalse();
  }

  @Test
  public void allowSectionDataCleanUp_verifyIsTrue() {
    final var allowSectionDateCleanUp = projectSetupService.allowSectionDataCleanUp(details);
    assertThat(allowSectionDateCleanUp).isTrue();
  }

  private void checkCommonFieldsMatch(ProjectSetupForm formToCheckAgainst, ProjectSetupForm resultingForm) {
    assertThat(formToCheckAgainst.getUpcomingTendersIncluded()).isEqualTo(resultingForm.getUpcomingTendersIncluded());
    assertThat(formToCheckAgainst.getAwardedContractsIncluded()).isEqualTo(resultingForm.getAwardedContractsIncluded());
    assertThat(formToCheckAgainst.getCollaborationOpportunitiesIncluded()).isEqualTo(resultingForm.getCollaborationOpportunitiesIncluded());
    assertThat(formToCheckAgainst.getWellsIncluded()).isEqualTo(resultingForm.getWellsIncluded());
    assertThat(formToCheckAgainst.getPlatformsFpsosIncluded()).isEqualTo(resultingForm.getPlatformsFpsosIncluded());
    assertThat(formToCheckAgainst.getSubseaInfrastructureIncluded()).isEqualTo(resultingForm.getSubseaInfrastructureIncluded());
    assertThat(formToCheckAgainst.getIntegratedRigsIncluded()).isEqualTo(resultingForm.getIntegratedRigsIncluded());
    assertThat(formToCheckAgainst.getPipelinesIncluded()).isEqualTo(resultingForm.getPipelinesIncluded());
  }
}
