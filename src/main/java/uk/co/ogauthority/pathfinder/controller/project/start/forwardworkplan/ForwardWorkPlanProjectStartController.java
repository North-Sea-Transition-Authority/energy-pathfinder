package uk.co.ogauthority.pathfinder.controller.project.start.forwardworkplan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.start.forwardworkplan.ForwardWorkPlanStartModelService;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@ProjectTypeCheck(types = ProjectType.FORWARD_WORK_PLAN)
@RequestMapping("/forward-work-plan/{projectId}/start")
public class ForwardWorkPlanProjectStartController {

  private final ForwardWorkPlanStartModelService forwardWorkPlanStartModelService;

  @Autowired
  public ForwardWorkPlanProjectStartController(ForwardWorkPlanStartModelService forwardWorkPlanStartModelService) {
    this.forwardWorkPlanStartModelService = forwardWorkPlanStartModelService;
  }

  @GetMapping
  public ModelAndView startPage(@PathVariable("projectId") Integer projectId,
                                ProjectContext projectContext,
                                AuthenticatedUserAccount user) {
    return forwardWorkPlanStartModelService.getStartPageModelAndView(projectContext.getProjectDetails());
  }
}
