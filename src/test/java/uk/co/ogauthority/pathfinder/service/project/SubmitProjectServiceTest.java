package uk.co.ogauthority.pathfinder.service.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.project.submission.SubmitProjectController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSummaryView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSummaryViewService;
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
  private ProjectInformationService projectInformationService;

  @Mock
  private AwardedContractService awardedContractService;

  private SubmitProjectService submitProjectService;

  private final static ProjectDetail PROJECT_DETAIL = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    submitProjectService = new SubmitProjectService(
        projectDetailsRepository,
        projectCleanUpService,
        projectSummaryViewService,
        List.of(projectInformationService, awardedContractService)
    );
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
  public void getProjectSubmitSummaryModelAndView_assertCorrectProperties() {

    final var projectDetail = PROJECT_DETAIL;
    final var projectId = projectDetail.getProject().getId();

    final var projectSummaryView = new ProjectSummaryView("test", List.of());
    when(projectSummaryViewService.getProjectSummaryView(projectDetail)).thenReturn(projectSummaryView);

    final var modelAndView = submitProjectService.getProjectSubmitSummaryModelAndView(projectDetail);

    assertThat(modelAndView.getModelMap()).containsExactly(
        entry("projectSummaryView", projectSummaryView),
        entry("submitProjectUrl",
            ReverseRouter.route(on(SubmitProjectController.class).submitProject(projectId, null))
        ),
        entry("taskListUrl", ControllerUtils.getBackToTaskListUrl(projectId))
    );
  }

  @Test
  public void getProjectSubmitSummaryModelAndViewWithSubmissionError_assertCorrectProperties() {

    final var projectDetail = PROJECT_DETAIL;
    final var projectId = projectDetail.getProject().getId();

    final var projectSummaryView = new ProjectSummaryView("test", List.of());
    when(projectSummaryViewService.getProjectSummaryView(projectDetail)).thenReturn(projectSummaryView);

    final var modelAndView = submitProjectService.getProjectSubmitSummaryModelAndViewWithSubmissionError(projectDetail);

    assertThat(modelAndView.getModelMap()).containsExactly(
        entry("projectSummaryView", projectSummaryView),
        entry("submitProjectUrl",
            ReverseRouter.route(on(SubmitProjectController.class).submitProject(projectId, null))
        ),
        entry("taskListUrl", ControllerUtils.getBackToTaskListUrl(projectId)),
        entry("errorMessage", SubmitProjectService.INVALID_PROJECT_ERROR_MESSAGE)
    );
  }
}
