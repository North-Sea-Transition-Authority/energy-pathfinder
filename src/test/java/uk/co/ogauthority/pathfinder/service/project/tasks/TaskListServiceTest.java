package uk.co.ogauthority.pathfinder.service.project.tasks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.controller.project.CancelDraftProjectVersionController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.tasks.TaskListGroup;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.service.project.cancellation.CancelDraftProjectVersionService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TaskListTestUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class TaskListServiceTest {

  @Mock
  private TaskListGroupsService taskListGroupsService;

  @Mock
  private ServiceProperties serviceProperties;

  @Mock
  private CancelDraftProjectVersionService cancelDraftProjectVersionService;

  @Mock
  private WebUserAccountService webUserAccountService;

  private static final String SERVICE_NAME = "Service name";

  private final Set<UserToProjectRelationship> relationships = Set.of(UserToProjectRelationship.OPERATOR);
  private final WebUserAccount webUserAccount = UserTestingUtil.getWebUserAccount();

  private TaskListService taskListService;

  private ProjectDetail projectDetail;

  @Before
  public void setup() throws Exception {
    taskListService = new TaskListService(
        taskListGroupsService,
        serviceProperties,
        cancelDraftProjectVersionService,
        webUserAccountService);
    projectDetail = ProjectUtil.getProjectDetails();

    when(serviceProperties.getServiceName()).thenReturn(SERVICE_NAME);

    when(cancelDraftProjectVersionService.isCancellable(projectDetail)).thenReturn(false);

    when(webUserAccountService.getWebUserAccount(projectDetail.getCreatedByWua())).thenReturn(Optional.of(webUserAccount));
  }

  @Test
  public void getTaskListModelAndView_whenFirstVersion() {
    projectDetail.setVersion(1);

    var groups = List.of(
        TaskListTestUtil.getTaskListGroup(),
        TaskListTestUtil.getTaskListGroup()
    );

    when(taskListGroupsService.getTaskListGroups(projectDetail, relationships)).thenReturn(groups);

    var modelAndView = taskListService.getTaskListModelAndView(projectDetail, relationships);
    assertTaskListModelAndView(modelAndView, false, groups, projectDetail);
  }

  @Test
  public void getTaskListModelAndView_whenUpdate() {
    projectDetail.setVersion(2);

    var groups = List.of(
        TaskListTestUtil.getTaskListGroup(),
        TaskListTestUtil.getTaskListGroup()
    );

    when(taskListGroupsService.getTaskListGroups(projectDetail, relationships)).thenReturn(groups);

    var modelAndView = taskListService.getTaskListModelAndView(projectDetail, relationships);
    assertTaskListModelAndView(modelAndView, true, groups, projectDetail);
  }

  @Test
  public void getTaskListModelAndView_whenInfrastructureProject() {
    projectDetail.setProjectType(ProjectType.INFRASTRUCTURE);

    var modelAndView = taskListService.getTaskListModelAndView(projectDetail, relationships);
    assertTaskListModelAndView(modelAndView, false, List.of(), projectDetail);
  }

  @Test
  public void getTaskListModelAndView_whenForwardWorkPlanProject() {

    projectDetail.setProjectType(ProjectType.FORWARD_WORK_PLAN);

    var modelAndView = taskListService.getTaskListModelAndView(projectDetail, relationships);
    assertTaskListModelAndView(modelAndView, false, List.of(), projectDetail);
  }

  private void assertTaskListModelAndView(ModelAndView modelAndView,
                                          boolean isUpdate,
                                          List<TaskListGroup> groups,
                                          ProjectDetail projectDetail) {
    var ownerEmail = webUserAccount.getEmailAddress();
    assertThat(modelAndView.getViewName()).isEqualTo(TaskListService.TASK_LIST_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("isUpdate", isUpdate),
        entry("hasTaskListGroups", !groups.isEmpty()),
        entry("groups", groups),
        entry("cancelDraftUrl", ReverseRouter.route(on(CancelDraftProjectVersionController.class)
            .getCancelDraft(projectDetail.getProject().getId(), null, null))),
        entry("taskListPageHeading", getExpectedTaskListHeading(projectDetail)),
        entry("isCancellable", false),
        entry("canDisplayEmail",  !ownerEmail.isBlank()),
        entry("ownerEmail", ownerEmail),
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

  private String getExpectedTaskListHeading(ProjectDetail projectDetail) {
    return ProjectService.isInfrastructureProject(projectDetail)
        ? String.format("%s %s", SERVICE_NAME, ProjectService.getProjectTypeDisplayNameLowercase(projectDetail))
        : ProjectService.getProjectTypeDisplayName(projectDetail);
  }
}
