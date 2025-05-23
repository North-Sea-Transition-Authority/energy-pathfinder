package uk.co.ogauthority.pathfinder.service.navigation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.communication.CommunicationSummaryController;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.AwardContractController;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSummaryController;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.infrastructure.InfrastructureAwardedContractController;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan.ForwardWorkPlanCollaborationOpportunityController;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.infrastructure.InfrastructureCollaborationOpportunitiesController;
import uk.co.ogauthority.pathfinder.controller.project.commissionedwell.CommissionedWellController;
import uk.co.ogauthority.pathfinder.controller.project.decommissionedpipeline.DecommissionedPipelineController;
import uk.co.ogauthority.pathfinder.controller.project.integratedrig.IntegratedRigController;
import uk.co.ogauthority.pathfinder.controller.project.platformsfpsos.PlatformsFpsosController;
import uk.co.ogauthority.pathfinder.controller.project.plugabandonmentschedule.PlugAbandonmentScheduleController;
import uk.co.ogauthority.pathfinder.controller.project.subseainfrastructure.SubseaInfrastructureController;
import uk.co.ogauthority.pathfinder.controller.project.upcomingtender.UpcomingTendersController;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationModelService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityModelService;

@Service
public class BreadcrumbService {

  protected static final String WORK_AREA_CRUMB_PROMPT = "Work area";
  protected static final String TASK_LIST_CRUMB_PROMPT = "Task list";
  protected static final String MANAGE_CRUMB_PROMPT_PREFIX = "Manage";
  protected static final String BREADCRUMB_MAP_MODEL_ATTR_NAME = "crumbList";
  protected static final String CURRENT_PAGE_MODEL_ATTR_NAME = "currentPage";

  public void fromUpcomingTenders(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, upcomingTenders(projectId), thisPage);
  }

  private Map<String, String> upcomingTenders(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(UpcomingTendersController.class).viewUpcomingTenders(projectId, null));
    map.put(route, UpcomingTendersController.PAGE_NAME);
    return map;
  }

  public void fromInfrastructureCollaborationOpportunities(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, infrastructureCollaborationOpportunities(projectId), thisPage);
  }

  private Map<String, String> infrastructureCollaborationOpportunities(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(InfrastructureCollaborationOpportunitiesController.class)
        .viewCollaborationOpportunities(projectId, null)
    );
    map.put(route, InfrastructureCollaborationOpportunitiesController.PAGE_NAME);
    return map;
  }

  public void fromInfrastructureAwardedContracts(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, infrastructureAwardedContracts(projectId), thisPage);
  }

  private Map<String, String> infrastructureAwardedContracts(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(InfrastructureAwardedContractController.class).viewAwardedContracts(projectId, null));
    map.put(route, AwardContractController.PAGE_NAME);
    return map;
  }

  public void fromForwardWorkPlanAwardedContracts(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, forwardWorkPlanAwardedContracts(projectId), thisPage);
  }

  private Map<String, String> forwardWorkPlanAwardedContracts(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(ForwardWorkPlanAwardedContractSummaryController.class).viewAwardedContracts(projectId, null));
    map.put(route, AwardContractController.PAGE_NAME);
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
    breadcrumbs.put(
        ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null, null)),
        WORK_AREA_CRUMB_PROMPT
    );
    return breadcrumbs;
  }

  public void fromTaskList(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, taskList(projectId), thisPage);
  }

  private Map<String, String> taskList(Integer projectId) {
    var map = workArea();
    String route = ReverseRouter.route(on(TaskListController.class).viewTaskList(projectId, null));
    map.put(route, TASK_LIST_CRUMB_PROMPT);
    return map;
  }

  public void fromManageProject(ProjectDetail projectDetail, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, manageProject(projectDetail), thisPage);
  }

  private Map<String, String> manageProject(ProjectDetail projectDetail) {
    final var map = workArea();
    final var route = ReverseRouter.route(on(ManageProjectController.class).getProject(
        projectDetail.getProject().getId(),
        null,
        null,
        null
    ));
    map.put(
        route,
        String.format("%s %s", MANAGE_CRUMB_PROMPT_PREFIX, projectDetail.getProjectType().getLowercaseDisplayName())
    );
    return map;
  }

  public void fromCommunicationsSummary(ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, communicationsSummary(), thisPage);
  }

  private Map<String, String> communicationsSummary() {
    Map<String, String> breadcrumbs = workArea();
    breadcrumbs.put(
        ReverseRouter.route(on(CommunicationSummaryController.class).getCommunicationsSummary(null)),
        CommunicationModelService.COMMUNICATIONS_SUMMARY_PAGE_TITLE);
    return breadcrumbs;
  }

  private void addAttrs(ModelAndView modelAndView, Map<String, String> breadcrumbs, String currentPage) {
    modelAndView.addObject(BREADCRUMB_MAP_MODEL_ATTR_NAME, breadcrumbs);
    modelAndView.addObject(CURRENT_PAGE_MODEL_ATTR_NAME, currentPage);
  }

  public void fromWorkPlanUpcomingTenders(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, workPlanUpcomingTenders(projectId), thisPage);
  }

  private Map<String, String> workPlanUpcomingTenders(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class).viewUpcomingTenders(projectId, null));
    map.put(route, ForwardWorkPlanUpcomingTenderController.PAGE_NAME);
    return map;
  }

  public void fromWorkPlanCollaborations(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, workPlanCollaborations(projectId), thisPage);
  }

  private Map<String, String> workPlanCollaborations(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class)
        .viewCollaborationOpportunities(projectId, null));
    map.put(route, ForwardWorkPlanCollaborationOpportunityModelService.PAGE_NAME);
    return map;
  }

  public void fromCommissionedWells(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, commissionedWells(projectId), thisPage);
  }

  private Map<String, String> commissionedWells(Integer projectId) {
    var map = taskList(projectId);
    String route = ReverseRouter.route(on(CommissionedWellController.class).viewWellsToCommission(
        projectId,
        null
    ));
    map.put(route, CommissionedWellController.TASK_LIST_NAME);
    return map;
  }
}
