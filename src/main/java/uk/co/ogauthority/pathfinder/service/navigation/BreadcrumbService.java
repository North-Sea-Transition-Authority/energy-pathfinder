package uk.co.ogauthority.pathfinder.service.navigation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.communication.CommunicationController;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.AwardedContractController;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.CollaborationOpportunitiesController;
import uk.co.ogauthority.pathfinder.controller.project.decommissionedpipeline.DecommissionedPipelineController;
import uk.co.ogauthority.pathfinder.controller.project.integratedrig.IntegratedRigController;
import uk.co.ogauthority.pathfinder.controller.project.platformsfpsos.PlatformsFpsosController;
import uk.co.ogauthority.pathfinder.controller.project.plugabandonmentschedule.PlugAbandonmentScheduleController;
import uk.co.ogauthority.pathfinder.controller.project.subseainfrastructure.SubseaInfrastructureController;
import uk.co.ogauthority.pathfinder.controller.project.upcomingtender.UpcomingTendersController;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationModelService;

@Service
public class BreadcrumbService {

  public void fromUpcomingTenders(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, upcomingTenders(projectId), thisPage);
  }

  private Map<String, String> upcomingTenders(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(UpcomingTendersController.class).viewUpcomingTenders(projectId, null));
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

  public void fromPlugAbandonmentSchedule(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, plugAbandonmentSchedule(projectId), thisPage);
  }

  private Map<String, String> plugAbandonmentSchedule(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(PlugAbandonmentScheduleController.class).viewPlugAbandonmentSchedules(
        projectId,
        null
    ));
    map.put(route, PlugAbandonmentScheduleController.TASK_LIST_NAME);
    return map;
  }

  public void fromPlatformsFpsos(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, platformsFpsos(projectId), thisPage);
  }

  private Map<String, String> platformsFpsos(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(PlatformsFpsosController.class).viewPlatformsFpsos(
        projectId,
        null
    ));
    map.put(route, PlatformsFpsosController.TASK_LIST_NAME);
    return map;
  }

  public void fromSubseaInfrastructure(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, subseaInfrastructure(projectId), thisPage);
  }

  private Map<String, String> subseaInfrastructure(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(SubseaInfrastructureController.class).viewSubseaStructures(projectId, null));
    map.put(route, SubseaInfrastructureController.TASK_LIST_NAME);
    return map;
  }

  public void fromIntegratedRig(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, integratedRig(projectId), thisPage);
  }

  private Map<String, String> integratedRig(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(IntegratedRigController.class).viewIntegratedRigs(projectId, null));
    map.put(route, IntegratedRigController.TASK_LIST_NAME);
    return map;
  }

  public void fromDecommissionedPipelines(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, decommissionedPipeline(projectId), thisPage);
  }

  private Map<String, String> decommissionedPipeline(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(DecommissionedPipelineController.class).viewPipelines(projectId, null));
    map.put(route, DecommissionedPipelineController.TASK_LIST_NAME);
    return map;
  }

  public void fromWorkArea(ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, workArea(), thisPage);
  }

  private Map<String, String> workArea() {
    Map<String, String> breadcrumbs = new LinkedHashMap<>();
    breadcrumbs.put(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null, null)), "Work area");
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

  public void fromManageProject(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, manageProject(projectId), thisPage);
  }

  private Map<String, String> manageProject(Integer projectId) {
    var map = workArea();
    String route = ReverseRouter.route(on(ManageProjectController.class).getProject(projectId, null, null, null));
    map.put(route, "Manage project");
    return map;
  }

  public void fromCommunicationSummary(ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, communicationSummary(), thisPage);
  }

  private Map<String, String> communicationSummary() {
    Map<String, String> breadcrumbs = workArea();
    breadcrumbs.put(
        ReverseRouter.route(on(CommunicationController.class).getCommunicationSummary(null)),
        CommunicationModelService.COMMUNICATION_SUMMARY_PAGE_TITLE);
    return breadcrumbs;
  }

  public void fromCommunicationEmailContent(Integer communicationId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, emailContent(communicationId), thisPage);
  }

  private Map<String, String> emailContent(Integer communicationId) {
    Map<String, String> breadcrumbs = communicationSummary();
    breadcrumbs.put(
        ReverseRouter.route(on(CommunicationController.class).getCommunicationContent(
            communicationId,
            null,
            null
        )),
        CommunicationModelService.COMMUNICATION_CONTENT_PAGE_TITLE);
    return breadcrumbs;
  }

  public void fromCommunicationOrganisationGroupSelect(Integer communicationId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, organisationGroupSelect(communicationId), thisPage);
  }

  private Map<String, String> organisationGroupSelect(Integer communicationId) {
    Map<String, String> breadcrumbs = emailContent(communicationId);
    breadcrumbs.put(
        ReverseRouter.route(on(CommunicationController.class).getOperatorSelectForCommunication(
            communicationId,
            null,
            null
        )),
        CommunicationModelService.OPERATOR_BREADCRUMB_TITLE
    );
    return breadcrumbs;
  }

  private void addAttrs(ModelAndView modelAndView, Map<String, String> breadcrumbs, String currentPage) {
    modelAndView.addObject("crumbList", breadcrumbs);
    modelAndView.addObject("currentPage", currentPage);
  }
}
