package uk.co.ogauthority.pathfinder.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderService;

@RestController
@RequestMapping("/api/work-plan-upcoming-tender")
public class WorkPlanUpcomingTenderRestController {

  private final ForwardWorkPlanUpcomingTenderService workPlanUpcomingTenderService;

  @Autowired
  public WorkPlanUpcomingTenderRestController(ForwardWorkPlanUpcomingTenderService workPlanUpcomingTenderService) {
    this.workPlanUpcomingTenderService = workPlanUpcomingTenderService;
  }

  @GetMapping("/departments")
  public RestSearchResult searchTenderDepartments(@Nullable @RequestParam("term") String searchTerm) {
    return new RestSearchResult(workPlanUpcomingTenderService.findDepartmentTenderLikeWithManualEntry(searchTerm));
  }
}
