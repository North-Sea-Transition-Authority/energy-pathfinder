package uk.co.ogauthority.pathfinder.service.project.start.forwardworkplan;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.start.infrastructure.InfrastructureProjectStartController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@Service
public class ForwardWorkPlanStartModelService {

  protected static final String TEMPLATE_PATH = "project/start/forwardworkplan/forwardWorkPlanStartPage";

  public ModelAndView getStartPageModelAndView(ProjectDetail projectDetail) {

    final var projectId = projectDetail.getProject().getId();

    return new ModelAndView(TEMPLATE_PATH)
        .addObject(
            "infrastructureProjectTypeLowercaseDisplayName",
            ProjectType.INFRASTRUCTURE.getLowercaseDisplayName()
        )
        .addObject(
            "forwardWorkPlanProjectTypeLowercaseDisplayName",
            ProjectType.FORWARD_WORK_PLAN.getLowercaseDisplayName()
        )
        .addObject("taskListUrl", ControllerUtils.getBackToTaskListUrl(projectId))
        .addObject(
            "startInfrastructureProjectUrl",
            ReverseRouter.route(on(InfrastructureProjectStartController.class).startPage(null))
        );
  }
}
