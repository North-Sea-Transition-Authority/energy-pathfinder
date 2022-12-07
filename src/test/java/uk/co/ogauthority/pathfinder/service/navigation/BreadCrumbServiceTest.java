package uk.co.ogauthority.pathfinder.service.navigation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan.ForwardWorkPlanCollaborationOpportunityController;
import uk.co.ogauthority.pathfinder.controller.project.commissionedwell.CommissionedWellController;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityModelService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class BreadCrumbServiceTest {

  private BreadcrumbService breadcrumbService;

  @Before
  public void setUp() {
    breadcrumbService = new BreadcrumbService();
  }

  @Test
  public void fromWorkArea() {

    final var modelAndView = new ModelAndView();
    final var currentPageName = "current page name";

    breadcrumbService.fromWorkArea(modelAndView, currentPageName);

    final var expectedBreadCrumbMap = new LinkedHashMap<String, String>();
    expectedBreadCrumbMap.put(
        ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null, null)),
        BreadcrumbService.WORK_AREA_CRUMB_PROMPT
    );

    assertModelAndViewProperties(
        modelAndView,
        expectedBreadCrumbMap,
        currentPageName
    );
  }

  @Test
  public void fromWorkPlanUpcomingTenders() {

    final var modelAndView = new ModelAndView();
    final var currentPageName = "current page";
    final var projectId = 1;

    breadcrumbService.fromWorkPlanUpcomingTenders(projectId, modelAndView, currentPageName);

    final var expectedBreadCrumbMap = new LinkedHashMap<String, String>();
    expectedBreadCrumbMap.put(
        ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null, null)),
        BreadcrumbService.WORK_AREA_CRUMB_PROMPT
    );
    expectedBreadCrumbMap.put(
        ReverseRouter.route(on(TaskListController.class).viewTaskList(projectId, null)),
        BreadcrumbService.TASK_LIST_CRUMB_PROMPT
    );
    expectedBreadCrumbMap.put(
        ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class).viewUpcomingTenders(projectId, null)),
        ForwardWorkPlanUpcomingTenderController.PAGE_NAME
    );

    assertModelAndViewProperties(
        modelAndView,
        expectedBreadCrumbMap,
        currentPageName
    );
  }

  @Test
  public void fromManageProject_assertModelProperties() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var modelAndView = new ModelAndView();
    final var currentPageName = "current page name";

    breadcrumbService.fromManageProject(projectDetail, modelAndView, currentPageName);

    final var expectedBreadCrumbMap = new LinkedHashMap<String, String>();
    expectedBreadCrumbMap.put(
        ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null, null)),
        BreadcrumbService.WORK_AREA_CRUMB_PROMPT
    );
    expectedBreadCrumbMap.put(
        ReverseRouter.route(on(ManageProjectController.class).getProject(
            projectDetail.getProject().getId(),
            null,
            null,
            null
        )),
        String.format(
            "%s %s",
            BreadcrumbService.MANAGE_CRUMB_PROMPT_PREFIX,
            projectDetail.getProjectType().getLowercaseDisplayName()
        )
    );

    assertModelAndViewProperties(
        modelAndView,
        expectedBreadCrumbMap,
        currentPageName
    );
  }

  @Test
  public void fromWorkPlanCollaborations() {

    final var modelAndView = new ModelAndView();
    final var currentPageName = "current page";
    final var projectId = 1;

    breadcrumbService.fromWorkPlanCollaborations(projectId, modelAndView, currentPageName);

    final var expectedBreadCrumbMap = new LinkedHashMap<String, String>();
    expectedBreadCrumbMap.put(
        ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null, null)),
        BreadcrumbService.WORK_AREA_CRUMB_PROMPT
    );
    expectedBreadCrumbMap.put(
        ReverseRouter.route(on(TaskListController.class).viewTaskList(projectId, null)),
        BreadcrumbService.TASK_LIST_CRUMB_PROMPT
    );
    expectedBreadCrumbMap.put(
        ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class)
            .viewCollaborationOpportunities(projectId, null)),
        ForwardWorkPlanCollaborationOpportunityModelService.PAGE_NAME
    );

    assertModelAndViewProperties(
        modelAndView,
        expectedBreadCrumbMap,
        currentPageName
    );
  }

  @Test
  public void fromCommissionedWells() {

    final var modelAndView = new ModelAndView();
    final var currentPageName = "current page";
    final var projectId = 1;

    breadcrumbService.fromCommissionedWells(projectId, modelAndView, currentPageName);

    final var expectedBreadCrumbMap = new LinkedHashMap<String, String>();
    expectedBreadCrumbMap.put(
        ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null, null)),
        BreadcrumbService.WORK_AREA_CRUMB_PROMPT
    );
    expectedBreadCrumbMap.put(
        ReverseRouter.route(on(TaskListController.class).viewTaskList(projectId, null)),
        BreadcrumbService.TASK_LIST_CRUMB_PROMPT
    );
    expectedBreadCrumbMap.put(
        ReverseRouter.route(on(CommissionedWellController.class).viewWellsToCommission(projectId, null)),
        CommissionedWellController.TASK_LIST_NAME
    );

    assertModelAndViewProperties(
        modelAndView,
        expectedBreadCrumbMap,
        currentPageName
    );
  }

  private void assertModelAndViewProperties(ModelAndView modelAndView,
                                            Map<String, String> expectedBreadCrumbMap,
                                            String currentPageName) {
    assertThat(modelAndView.getModel()).containsExactly(
        entry(BreadcrumbService.BREADCRUMB_MAP_MODEL_ATTR_NAME, expectedBreadCrumbMap),
        entry(BreadcrumbService.CURRENT_PAGE_MODEL_ATTR_NAME, currentPageName)
    );
  }
}
