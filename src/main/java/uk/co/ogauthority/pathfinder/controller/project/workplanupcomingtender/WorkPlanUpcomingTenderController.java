package uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.WorkPlanUpcomingTenderService;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@ProjectTypeCheck(types = ProjectType.FORWARD_WORK_PLAN)
@RequestMapping("/project/{projectId}/work-plan-upcoming-tenders")
public class WorkPlanUpcomingTenderController {

  public static final String PAGE_NAME = "Upcoming tenders";

  private final WorkPlanUpcomingTenderService workPlanUpcomingTenderService;

  public WorkPlanUpcomingTenderController(
      WorkPlanUpcomingTenderService workPlanUpcomingTenderService) {

    this.workPlanUpcomingTenderService = workPlanUpcomingTenderService;
  }

  @GetMapping
  public ModelAndView viewUpcomingTenders(@PathVariable("projectId") Integer projectId,
                                          ProjectContext projectContext) {
    return workPlanUpcomingTenderService.getUpcomingTendersModelAndView(projectId);
  }
}
