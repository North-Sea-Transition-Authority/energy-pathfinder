package uk.co.ogauthority.pathfinder.service.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.project.submission.SubmitProjectController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.view.submission.ProjectSubmissionSummaryView;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSummaryView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.service.email.RegulatorEmailService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.cleanup.ProjectCleanUpService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.project.submission.ProjectSubmissionSummaryViewService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSummaryViewService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorUpdateRequestService;
import uk.co.ogauthority.pathfinder.testutil.ProjectSubmissionSummaryTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@RunWith(MockitoJUnitRunner.class)
public class SubmitProjectServiceTest {

  @Mock
  private ProjectDetailsRepository projectDetailsRepository;

  @Mock
  private ProjectCleanUpService projectCleanUpService;

  @Mock
  private ProjectSummaryViewService projectSummaryViewService;

  @Mock
  private ProjectSubmissionSummaryViewService projectSubmissionSummaryViewService;

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private AwardedContractService awardedContractService;

  @Mock
  private RegulatorEmailService regulatorEmailService;

  @Mock
  private RegulatorUpdateRequestService regulatorUpdateRequestService;

  private SubmitProjectService submitProjectService;

  private final static ProjectDetail PROJECT_DETAIL = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    submitProjectService = new SubmitProjectService(
        projectDetailsRepository,
        projectCleanUpService,
        projectSummaryViewService,
        projectSubmissionSummaryViewService,
        List.of(projectInformationService, awardedContractService),
        regulatorEmailService,
        regulatorUpdateRequestService);
    when(projectDetailsRepository.save(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void submitProject() {
    var projectDetail = PROJECT_DETAIL;
    var authenticatedUserAccount = UserTestingUtil.getAuthenticatedUserAccount();

    submitProjectService.submitProject(projectDetail, authenticatedUserAccount);

    assertThat(projectDetail.getStatus()).isEqualTo(ProjectStatus.QA);
    assertThat(projectDetail.getSubmittedByWua()).isEqualTo(authenticatedUserAccount.getWuaId());
    assertThat(projectDetail.getSubmittedInstant()).isNotNull();

    verify(projectDetailsRepository, times(1)).save(projectDetail);
    verify(projectCleanUpService, times(1)).removeProjectSectionDataIfNotRelevant(projectDetail);
  }

  @Test
  public void submitProject_UpdateEmailNotSentIfVersionOne() {
    var projectDetail = PROJECT_DETAIL;
    var authenticatedUserAccount = UserTestingUtil.getAuthenticatedUserAccount();

    submitProjectService.submitProject(projectDetail, authenticatedUserAccount);

    verify(regulatorEmailService, times(0)).sendUpdateSubmitConfirmationEmail(projectDetail);
  }

  @Test
  public void submitProject_UpdateEmailSentIfNotVersionOne() {
    var projectDetail = PROJECT_DETAIL;
    projectDetail.setVersion(2);
    var authenticatedUserAccount = UserTestingUtil.getAuthenticatedUserAccount();

    submitProjectService.submitProject(projectDetail, authenticatedUserAccount);

    verify(regulatorEmailService, times(1)).sendUpdateSubmitConfirmationEmail(projectDetail);
  }

  @Test
  public void isProjectValid_whenValid_thenTrue() {

    final var projectDetail = PROJECT_DETAIL;

    when(projectInformationService.canShowInTaskList(projectDetail)).thenReturn(true);
    when(awardedContractService.canShowInTaskList(projectDetail)).thenReturn(true);

    when(projectInformationService.isComplete(projectDetail)).thenReturn(true);
    when(awardedContractService.isComplete(projectDetail)).thenReturn(true);

    assertThat(submitProjectService.isProjectValid(projectDetail)).isTrue();
  }

  @Test
  public void isProjectValid_whenInvalid_thenFalse() {

    final var projectDetail = PROJECT_DETAIL;

    when(projectInformationService.canShowInTaskList(projectDetail)).thenReturn(true);
    when(awardedContractService.canShowInTaskList(projectDetail)).thenReturn(true);

    when(projectInformationService.isComplete(projectDetail)).thenReturn(true);
    when(awardedContractService.isComplete(projectDetail)).thenReturn(false);

    assertThat(submitProjectService.isProjectValid(projectDetail)).isFalse();
  }

  @Test
  public void isProjectValid_whenSectionNotShownOnTaskList_thenNotPartOfCheck() {

    final var projectDetail = PROJECT_DETAIL;

    when(projectInformationService.canShowInTaskList(projectDetail)).thenReturn(true);
    when(awardedContractService.canShowInTaskList(projectDetail)).thenReturn(false);

    when(projectInformationService.isComplete(projectDetail)).thenReturn(true);

    assertThat(submitProjectService.isProjectValid(projectDetail)).isTrue();
  }

  @Test
  public void getProjectSubmitSummaryModelAndView_assertCorrectProperties_whenFirstVersionAndValidProject() {

    final var projectDetail = PROJECT_DETAIL;
    projectDetail.setVersion(1);

    final var projectSummaryView = new ProjectSummaryView("test", List.of());
    when(projectSummaryViewService.getProjectSummaryView(projectDetail)).thenReturn(projectSummaryView);

    final var modelAndView = submitProjectService.getProjectSubmitSummaryModelAndView(projectDetail, true);

    assertProjectSubmitSummaryModelAndView(projectDetail, modelAndView, false, true, projectSummaryView);
  }

  @Test
  public void getProjectSubmitSummaryModelAndView_assertCorrectProperties_whenFirstVersionAndInvalidProject() {

    final var projectDetail = PROJECT_DETAIL;
    projectDetail.setVersion(1);

    final var projectSummaryView = new ProjectSummaryView("test", List.of());
    when(projectSummaryViewService.getProjectSummaryView(projectDetail)).thenReturn(projectSummaryView);

    final var modelAndView = submitProjectService.getProjectSubmitSummaryModelAndView(projectDetail, false);

    assertProjectSubmitSummaryModelAndView(projectDetail, modelAndView, false, false, projectSummaryView);
  }

  @Test
  public void getProjectSubmitSummaryModelAndView_assertCorrectProperties_whenUpdateAndValidProject() {

    final var projectDetail = PROJECT_DETAIL;
    projectDetail.setVersion(2);

    final var projectSummaryView = new ProjectSummaryView("test", List.of());
    when(projectSummaryViewService.getProjectSummaryView(projectDetail)).thenReturn(projectSummaryView);

    final var modelAndView = submitProjectService.getProjectSubmitSummaryModelAndView(projectDetail, true);
    assertProjectSubmitSummaryModelAndView(projectDetail, modelAndView, true, true, projectSummaryView);
  }

  @Test
  public void getProjectSubmitSummaryModelAndView_assertCorrectProperties_whenUpdateAndInvalidProject() {

    final var projectDetail = PROJECT_DETAIL;
    projectDetail.setVersion(2);

    final var projectSummaryView = new ProjectSummaryView("test", List.of());
    when(projectSummaryViewService.getProjectSummaryView(projectDetail)).thenReturn(projectSummaryView);

    final var modelAndView = submitProjectService.getProjectSubmitSummaryModelAndView(projectDetail, false);
    assertProjectSubmitSummaryModelAndView(projectDetail, modelAndView, true, false, projectSummaryView);
  }

  private void assertProjectSubmitSummaryModelAndView(ProjectDetail projectDetail,
                                                      ModelAndView modelAndView,
                                                      boolean isUpdate,
                                                      boolean isProjectValid,
                                                      ProjectSummaryView projectSummaryView) {

    final var projectId = projectDetail.getProject().getId();

    assertThat(modelAndView.getModelMap()).containsExactly(
        entry("isUpdate", isUpdate),
        entry("isProjectValid", isProjectValid),
        entry("projectSummaryView", projectSummaryView),
        entry("submitProjectUrl",
            ReverseRouter.route(on(SubmitProjectController.class).submitProject(projectId, null, Optional.empty()))
        ),
        entry("updateRequestReason", regulatorUpdateRequestService.getUpdateRequestReason(
            projectDetail.getProject(),
            projectDetail.getVersion())),
        entry("taskListUrl", ControllerUtils.getBackToTaskListUrl(projectId)),
        entry(
            ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR,
            ProjectService.getProjectTypeDisplayName(projectDetail)
        ),
        entry(
            ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR,
            ProjectService.getProjectTypeDisplayNameLowercase(projectDetail)
        )
    );
  }

  @Test
  public void getProjectSubmitConfirmationModelAndView_whenFirstVersion() {
    var projectDetail = ProjectUtil.getProjectDetails();
    projectDetail.setVersion(1);

    var projectSubmissionSummaryView = ProjectSubmissionSummaryTestUtil.getProjectSubmissionSummaryView();

    when(projectSubmissionSummaryViewService.getProjectSubmissionSummaryView(projectDetail)).thenReturn(projectSubmissionSummaryView);

    var modelAndView = submitProjectService.getProjectSubmitConfirmationModelAndView(projectDetail);

    assertProjectSubmitConfirmationModelAndView(
        modelAndView,
        false,
        projectSubmissionSummaryView,
        projectDetail
    );
  }

  @Test
  public void getProjectSubmitConfirmationModelAndView_whenUpdate() {
    var projectDetail = ProjectUtil.getProjectDetails();
    projectDetail.setVersion(2);

    var projectSubmissionSummaryView = ProjectSubmissionSummaryTestUtil.getProjectSubmissionSummaryView();

    when(projectSubmissionSummaryViewService.getProjectSubmissionSummaryView(projectDetail)).thenReturn(projectSubmissionSummaryView);

    var modelAndView = submitProjectService.getProjectSubmitConfirmationModelAndView(projectDetail);

    assertProjectSubmitConfirmationModelAndView(
        modelAndView,
        true,
        projectSubmissionSummaryView,
        projectDetail
    );
  }

  private void assertProjectSubmitConfirmationModelAndView(ModelAndView modelAndView,
                                                           boolean isUpdate,
                                                           ProjectSubmissionSummaryView projectSubmissionSummaryView,
                                                           ProjectDetail projectDetail) {
    assertThat(modelAndView.getViewName()).isEqualTo(SubmitProjectService.PROJECT_SUBMIT_CONFIRMATION_TEMPLATE_PATH);
    assertThat(modelAndView.getModelMap()).containsExactly(
        entry("isUpdate", isUpdate),
        entry("projectSubmissionSummaryView", projectSubmissionSummaryView),
        entry("workAreaUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null, null))),
        entry("feedbackUrl", ControllerUtils.getFeedbackUrl(projectDetail.getId())),
        entry(
            ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR,
            ProjectService.getProjectTypeDisplayName(projectDetail)
        ),
        entry(
            ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR,
            ProjectService.getProjectTypeDisplayNameLowercase(projectDetail)
        )
    );
  }
}
