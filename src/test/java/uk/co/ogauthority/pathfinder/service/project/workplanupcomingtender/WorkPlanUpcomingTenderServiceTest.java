package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import static org.assertj.core.api.Assertions.assertThat;
import static java.util.Map.entry;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.WorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.WorkPlanUpcomingTenderFormValidator;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.project.workplanupcomingtender.WorkPlanUpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class WorkPlanUpcomingTenderServiceTest {

  @Mock
  private BreadcrumbService breadcrumbService;

  @Mock
  private FunctionService functionService;

  @Mock
  private ValidationService validationService;

  @Mock
  private WorkPlanUpcomingTenderFormValidator workPlanUpcomingTenderFormValidator;

  @Mock
  private WorkPlanUpcomingTenderRepository workPlanUpcomingTenderRepository;

  @Mock
  private SearchSelectorService searchSelectorService;

  private WorkPlanUpcomingTenderService workPlanUpcomingTenderService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

  @Before
  public void setup() {
    workPlanUpcomingTenderService = new WorkPlanUpcomingTenderService(
        breadcrumbService,
        functionService,
        validationService,
        workPlanUpcomingTenderFormValidator,
        workPlanUpcomingTenderRepository,
        searchSelectorService
    );
  }

  @Test
  public void getUpcomingTendersModelAndView() {
    var projectId = projectDetail.getProject().getId();

    var modelAndView = workPlanUpcomingTenderService.getUpcomingTendersModelAndView(projectId);

    assertThat(modelAndView.getViewName()).isEqualTo(WorkPlanUpcomingTenderService.TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("pageName", WorkPlanUpcomingTenderController.PAGE_NAME),
        entry("addUpcomingTenderUrl", ReverseRouter.route(on(WorkPlanUpcomingTenderController.class).addUpcomingTender(projectId, null))
        )
    );

    verify(breadcrumbService, times(1)).fromTaskList(projectId, modelAndView, WorkPlanUpcomingTenderController.PAGE_NAME);
  }

  @Test
  public void isComplete_whenInvalid_thenFalse() {

    var isComplete = workPlanUpcomingTenderService.isComplete(projectDetail);

    assertThat(isComplete).isFalse();
  }

  @Test
  public void canShowInTaskList_whenNotForwardWorkPlan_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);

    assertThat(workPlanUpcomingTenderService.canShowInTaskList(projectDetail)).isFalse();
  }

  @Test
  public void canShowInTaskList_whenForwardWorkPlan_thenTrue() {
    assertThat(workPlanUpcomingTenderService.canShowInTaskList(projectDetail)).isTrue();
  }
}