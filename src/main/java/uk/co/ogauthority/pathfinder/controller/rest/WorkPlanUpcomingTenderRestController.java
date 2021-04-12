package uk.co.ogauthority.pathfinder.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.WorkPlanUpcomingTenderService;

@RestController
@RequestMapping("/api/work-plan-upcoming-tender")
public class WorkPlanUpcomingTenderRestController {

  private final WorkPlanUpcomingTenderService workPlanUpcomingTenderService;

  @Autowired
  public WorkPlanUpcomingTenderRestController(WorkPlanUpcomingTenderService workPlanUpcomingTenderService) {
    this.workPlanUpcomingTenderService = workPlanUpcomingTenderService;
  }

  @GetMapping("/department")
  public RestSearchResult searchTenderDepartments(@Nullable @RequestParam("term") String searchTerm) {
    return new RestSearchResult(workPlanUpcomingTenderService.findDepartmentTenderLikeWithManualEntry(searchTerm));
  }
}
