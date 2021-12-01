package uk.co.ogauthority.pathfinder.service.project.start.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.project.start.infrastructure.InfrastructureProjectStartController;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanStartModelServiceTest {

  private ForwardWorkPlanStartModelService forwardWorkPlanStartModelService;

  @Before
  public void setup() {
    forwardWorkPlanStartModelService = new ForwardWorkPlanStartModelService();
  }

  @Test
  public void getStartPageModelAndView_assertModelProperties() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    final var resultingModelAndView = forwardWorkPlanStartModelService.getStartPageModelAndView(projectDetail);

    assertThat(resultingModelAndView.getViewName()).isEqualTo(ForwardWorkPlanStartModelService.TEMPLATE_PATH);
    assertThat(resultingModelAndView.getModelMap()).containsExactly(
        entry(
            "infrastructureProjectTypeLowercaseDisplayName",
            ProjectType.INFRASTRUCTURE.getLowercaseDisplayName()
        ),
        entry(
            "forwardWorkPlanProjectTypeLowercaseDisplayName",
            ProjectType.FORWARD_WORK_PLAN.getLowercaseDisplayName()
        ),
        entry(
            "taskListUrl",
            ControllerUtils.getBackToTaskListUrl(projectDetail.getProject().getId())
        ),
        entry(
            "startInfrastructureProjectUrl",
            ReverseRouter.route(on(InfrastructureProjectStartController.class).startPage(null))
        )
    );
  }

}