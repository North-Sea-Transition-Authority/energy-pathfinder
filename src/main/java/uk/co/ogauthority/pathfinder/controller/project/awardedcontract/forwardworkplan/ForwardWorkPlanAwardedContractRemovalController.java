package uk.co.ogauthority.pathfinder.controller.project.awardedcontract.forwardworkplan;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.annotation.AllowProjectContributorAccess;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.AwardContractController;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSummaryService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@AllowProjectContributorAccess
@ProjectTypeCheck(types = ProjectType.FORWARD_WORK_PLAN)
@RequestMapping("/project/{projectId}/work-plan-awarded-contracts/awarded-contract/{awardedContractId}/remove/{displayOrder}")
public class ForwardWorkPlanAwardedContractRemovalController extends AwardContractController {

  private final ForwardWorkPlanAwardedContractService awardedContractService;
  private final ForwardWorkPlanAwardedContractSummaryService awardedContractSummaryService;

  public ForwardWorkPlanAwardedContractRemovalController(BreadcrumbService breadcrumbService,
                                                         ControllerHelperService controllerHelperService,
                                                         ProjectSectionItemOwnershipService projectSectionItemOwnershipService,
                                                         ForwardWorkPlanAwardedContractService awardedContractService,
                                                         ForwardWorkPlanAwardedContractSummaryService awardedContractSummaryService
                                                         ) {
    super(breadcrumbService, controllerHelperService, projectSectionItemOwnershipService);
    this.awardedContractService = awardedContractService;
    this.awardedContractSummaryService = awardedContractSummaryService;
  }

  @GetMapping()
  public ModelAndView removeAwardedContractConfirmation(@PathVariable("projectId") Integer projectId,
                                                        @PathVariable("awardedContractId") Integer awardedProjectId,
                                                        @PathVariable("displayOrder") Integer displayOrder,
                                                        ProjectContext projectContext) {
    var awardedContract = awardedContractService.getAwardedContract(
        awardedProjectId,
        projectContext.getProjectDetails()
    );
    checkIfUserHasAccessAwardedContract(awardedContract);
    var awardedContractView = awardedContractSummaryService.getAwardedContractView(
        awardedProjectId,
        projectContext.getProjectDetails(),
        displayOrder
    );
    return removeAwardedContractModelAndView(projectId, awardedContractView);
  }

  @PostMapping()
  public ModelAndView removeAwardedContract(@PathVariable("projectId") Integer projectId,
                                            @PathVariable("awardedContractId") Integer awardedContractId,
                                            @PathVariable("displayOrder") Integer displayOrder,
                                            ProjectContext projectContext) {
    var projectDetail = projectContext.getProjectDetails();
    var awardedContract = awardedContractService.getAwardedContract(
        awardedContractId,
        projectDetail
    );
    checkIfUserHasAccessAwardedContract(awardedContract);
    awardedContractService.deleteAwardedContract(awardedContract);
    AuditService.audit(
        AuditEvent.AWARDED_CONTRACT_REMOVED,
        String.format(
            AuditEvent.AWARDED_CONTRACT_REMOVED.getMessage(),
            awardedContractId,
            projectDetail.getId()
        )
    );

    if (awardedContractService.hasAwardedContracts(projectDetail)) {
      return getForwardWorkPlanAwardedContractSummaryRedirect(projectId);
    } else {
      return getForwardWorkPlanAwardedContractSetupRedirect(projectId);
    }
  }


  private ModelAndView removeAwardedContractModelAndView(Integer projectId, ForwardWorkPlanAwardedContractView awardedContractView) {
    var modelAndView = new ModelAndView("project/awardedcontract/removeAwardedContract")
        .addObject("awardedContractView", awardedContractView)
        .addObject("cancelUrl", getAwardedContractSummaryUrl(projectId));

    breadcrumbService.fromInfrastructureAwardedContracts(projectId, modelAndView, REMOVE_PAGE_NAME);

    return modelAndView;
  }

  private String getAwardedContractSummaryUrl(Integer projectId) {
    return ReverseRouter.route(on(ForwardWorkPlanAwardedContractSummaryController.class)
        .viewAwardedContracts(projectId, null));
  }

  private ModelAndView getForwardWorkPlanAwardedContractSetupRedirect(Integer projectId) {
    return ReverseRouter.redirect(on(ForwardWorkPlanAwardedContractSetupController.class)
        .getAwardedContractSetup(projectId, null, null));
  }
}
