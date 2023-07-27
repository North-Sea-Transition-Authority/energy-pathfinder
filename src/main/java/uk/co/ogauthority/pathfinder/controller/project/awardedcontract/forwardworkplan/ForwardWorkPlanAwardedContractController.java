package uk.co.ogauthority.pathfinder.controller.project.awardedcontract.forwardworkplan;

import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.annotation.AllowProjectContributorAccess;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.AwardContractController;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractForm;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@AllowProjectContributorAccess
@ProjectTypeCheck(types = ProjectType.FORWARD_WORK_PLAN)
@RequestMapping("/project/{projectId}/work-plan-awarded-contracts")
public class ForwardWorkPlanAwardedContractController extends AwardContractController {

  private final ForwardWorkPlanAwardedContractService awardedContractService;

  public ForwardWorkPlanAwardedContractController(BreadcrumbService breadcrumbService,
                                                  ControllerHelperService controllerHelperService,
                                                  ProjectSectionItemOwnershipService projectSectionItemOwnershipService,
                                                  ForwardWorkPlanAwardedContractService awardedContractService) {
    super(breadcrumbService, controllerHelperService, projectSectionItemOwnershipService);
    this.awardedContractService = awardedContractService;
  }

  @GetMapping("/awarded-contract")
  public ModelAndView addAwardedContract(@PathVariable("projectId") Integer projectId,
                                         ProjectContext projectContext) {
    var form = new ForwardWorkPlanAwardedContractForm();
    var preSelectedContractFunctionMap = awardedContractService.getPreSelectedContractFunction(form);
    return getAwardedContractModelAndView(projectId, form, preSelectedContractFunctionMap, projectContext.getProjectDetails());
  }

  @GetMapping("/awarded-contract/{awardedContractId}/edit")
  public ModelAndView getAwardedContract(@PathVariable("projectId") Integer projectId,
                                         @PathVariable("awardedContractId") Integer awardedProjectId,
                                         ProjectContext projectContext) {
    var projectDetails = projectContext.getProjectDetails();
    var awardedContract = awardedContractService.getAwardedContract(
        awardedProjectId,
        projectDetails);
    checkIfUserHasAccessAwardedContract(awardedContract);
    var form = awardedContractService.getForm(awardedContract);
    var preSelectedContractFunctionMap = awardedContractService.getPreSelectedContractFunction(form);
    return getAwardedContractModelAndView(projectId, form, preSelectedContractFunctionMap, projectDetails);
  }

  @PostMapping("/awarded-contract")
  public ModelAndView saveAwardedContract(@PathVariable("projectId") Integer projectId,
                                          @Valid @ModelAttribute("form") ForwardWorkPlanAwardedContractForm form,
                                          BindingResult bindingResult,
                                          ValidationType validationType,
                                          ProjectContext projectContext) {
    var preSelectedContractFunctionMap = awardedContractService.getPreSelectedContractFunction(form);
    var projectDetails = projectContext.getProjectDetails();
    bindingResult = awardedContractService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getAwardedContractModelAndView(projectId, form, preSelectedContractFunctionMap, projectDetails),
        form,
        () -> {
          var contract = awardedContractService.createAwardedContract(
              projectDetails,
              form,
              projectContext.getUserAccount()
          );
          AuditService.audit(
              AuditEvent.AWARDED_CONTRACT_UPDATED,
              String.format(
                  AuditEvent.AWARDED_CONTRACT_UPDATED.getMessage(),
                  contract.getId(),
                  projectDetails.getId()
              )
          );
          return getForwardWorkPlanAwardedContractSummaryRedirect(projectId);
        }
    );
  }

  @PostMapping("/awarded-contract/{awardedContractId}/edit")
  public ModelAndView saveAwardedContract(@PathVariable("projectId") Integer projectId,
                                          @PathVariable("awardedContractId") Integer awardedContractId,
                                          @Valid @ModelAttribute("form") ForwardWorkPlanAwardedContractForm form,
                                          BindingResult bindingResult,
                                          ValidationType validationType,
                                          ProjectContext projectContext) {
    var preSelectedContractFunctionMap = awardedContractService.getPreSelectedContractFunction(form);
    var projectDetails = projectContext.getProjectDetails();
    var awardedContract = awardedContractService.getAwardedContract(
        awardedContractId,
        projectDetails
    );
    checkIfUserHasAccessAwardedContract(awardedContract);
    bindingResult = awardedContractService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getAwardedContractModelAndView(projectId, form, preSelectedContractFunctionMap, projectDetails),
        form,
        () -> {
          var contract = awardedContractService.updateAwardedContract(awardedContractId,
              projectDetails, form);
          AuditService.audit(
              AuditEvent.AWARDED_CONTRACT_UPDATED,
              String.format(
                  AuditEvent.AWARDED_CONTRACT_UPDATED.getMessage(),
                  contract.getId(),
                  projectDetails.getId()
              )
          );

          return getForwardWorkPlanAwardedContractSummaryRedirect(projectId);
        }
    );
  }

}
