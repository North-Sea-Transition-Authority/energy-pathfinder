package uk.co.ogauthority.pathfinder.service.navigation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.AwardedContractController;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.CollaborationOpportunitiesController;
import uk.co.ogauthority.pathfinder.controller.project.decommissionedwell.DecommissionedWellController;
import uk.co.ogauthority.pathfinder.controller.project.platformsfpsos.PlatformsFpsosController;
import uk.co.ogauthority.pathfinder.controller.project.subseainfrastructure.SubseaInfrastructureController;
import uk.co.ogauthority.pathfinder.controller.project.upcomingtender.UpcomingTendersController;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

@Service
public class BreadcrumbService {

  public void fromUpcomingTenders(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, upcomingTenders(projectId), thisPage);
  }

  private Map<String, String> upcomingTenders(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(UpcomingTendersController.class).viewTenders(projectId, null));
    map.put(route, UpcomingTendersController.PAGE_NAME);
    return map;
  }

  public void fromCollaborationOpportunities(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, collaborationOpportunities(projectId), thisPage);
  }

  private Map<String, String> collaborationOpportunities(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(CollaborationOpportunitiesController.class).viewCollaborationOpportunities(projectId, null));
    map.put(route, CollaborationOpportunitiesController.PAGE_NAME);
    return map;
  }

  public void fromAwardedContracts(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, awardedContracts(projectId), thisPage);
  }

  private Map<String, String> awardedContracts(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(AwardedContractController.class).viewAwardedContracts(projectId, null));
    map.put(route, AwardedContractController.PAGE_NAME);
    return map;
  }

  public void fromDecommissionedWells(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, decommissionedWells(projectId), thisPage);
  }

  private Map<String, String> decommissionedWells(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(DecommissionedWellController.class).viewWellsToBeDecommissioned(
        projectId,
        null
    ));
    map.put(route, DecommissionedWellController.SUMMARY_PAGE_NAME);
    return map;
  }

  public void fromPlatformsFpsos(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, platformsFpsos(projectId), thisPage);
  }

  private Map<String, String> platformsFpsos(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(PlatformsFpsosController.class).viewPlatformFpso(
        projectId,
        null
    ));
    map.put(route, PlatformsFpsosController.SUMMARY_PAGE_NAME);
    return map;
  }

  public void fromSubseaInfrastructure(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, subseaInfrastructure(projectId), thisPage);
  }

  private Map<String, String> subseaInfrastructure(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(SubseaInfrastructureController.class).getSubseaStructures(projectId, null));
    map.put(route, SubseaInfrastructureController.SUMMARY_PAGE_NAME);
    return map;
  }

  public void fromWorkArea(ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, workArea(), thisPage);
  }

  private Map<String, String> workArea() {
    Map<String, String> breadcrumbs = new LinkedHashMap<>();
    breadcrumbs.put(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), "Work area");
    return breadcrumbs;
  }

  public void fromTaskList(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, taskList(projectId), thisPage);
  }

  private Map<String, String> taskList(Integer projectId) {
    var map = workArea();
    String route = ReverseRouter.route(on(TaskListController.class).viewTaskList(projectId, null));
    map.put(route, "Task list");
    return map;
  }

  private void addAttrs(ModelAndView modelAndView, Map<String, String> breadcrumbs, String currentPage) {
    modelAndView.addObject("crumbList", breadcrumbs);
    modelAndView.addObject("currentPage", currentPage);
  }
}
