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
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormCommon;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractForm;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractServiceCommon;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractSummaryService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@AllowProjectContributorAccess
@ProjectTypeCheck(types = ProjectType.FORWARD_WORK_PLAN)
@RequestMapping("/project/{projectId}/work-plan-awarded-contracts")
public class ForwardWorkPlanAwardedContractController extends AwardContractController {

  public ForwardWorkPlanAwardedContractController(BreadcrumbService breadcrumbService,
                                                  ControllerHelperService controllerHelperService,
                                                  ProjectSectionItemOwnershipService projectSectionItemOwnershipService,
                                                  AwardedContractServiceCommon awardedContractServiceCommon,
                                                  AwardedContractSummaryService awardedContractSummaryService) {
    super(breadcrumbService, controllerHelperService, projectSectionItemOwnershipService,
        awardedContractServiceCommon, awardedContractSummaryService);
  }

  @GetMapping("/awarded-contract")
  public ModelAndView addAwardedContract(@PathVariable("projectId") Integer projectId) {
    return getAwardedContractModelAndView(projectId, new ForwardWorkPlanAwardedContractForm());
  }

  @GetMapping("/awarded-contract/{awardedContractId}/edit")
  public ModelAndView getAwardedContract(@PathVariable("projectId") Integer projectId,
                                         @PathVariable("awardedContractId") Integer awardedProjectId,
                                         ProjectContext projectContext) {
    var awardedContract = awardedContractServiceCommon.getAwardedContract(
        awardedProjectId,
        projectContext.getProjectDetails());
    checkIfUserHasAccessAwardedContract(awardedContract);
    var form = awardedContractServiceCommon.getForm(awardedProjectId, projectContext.getProjectDetails());
    return getAwardedContractModelAndView(projectId, form);
  }

  @PostMapping("/awarded-contract")
  public ModelAndView saveAwardedContract(@PathVariable("projectId") Integer projectId,
                                          @Valid @ModelAttribute("form") ForwardWorkPlanAwardedContractForm form,
                                          BindingResult bindingResult,
                                          ValidationType validationType,
                                          ProjectContext projectContext) {
    bindingResult = awardedContractServiceCommon.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getAwardedContractModelAndView(projectId, form),
        form,
        () -> {
          var contract = awardedContractServiceCommon.createAwardedContract(
              projectContext.getProjectDetails(),
              form,
              projectContext.getUserAccount()
          );
          AuditService.audit(
              AuditEvent.AWARDED_CONTRACT_UPDATED,
              String.format(
                  AuditEvent.AWARDED_CONTRACT_UPDATED.getMessage(),
                  contract.getId(),
                  projectContext.getProjectDetails().getId()
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
    var awardedContract = awardedContractServiceCommon.getAwardedContract(
        awardedContractId,
        projectContext.getProjectDetails()
    );
    checkIfUserHasAccessAwardedContract(awardedContract);
    bindingResult = awardedContractServiceCommon.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getAwardedContractModelAndView(projectId, form),
        form,
        () -> {
          var contract = awardedContractServiceCommon.updateAwardedContract(awardedContractId,
              projectContext.getProjectDetails(), form);
          AuditService.audit(
              AuditEvent.AWARDED_CONTRACT_UPDATED,
              String.format(
                  AuditEvent.AWARDED_CONTRACT_UPDATED.getMessage(),
                  contract.getId(),
                  projectContext.getProjectDetails().getId()
              )
          );

          return getForwardWorkPlanAwardedContractSummaryRedirect(projectId);
        }
    );
  }

  private ModelAndView getAwardedContractModelAndView(Integer projectId, AwardedContractFormCommon form) {

    var preSelectedContractFunctionMap = awardedContractServiceCommon.getPreSelectedContractFunction(form);

    var modelAndView = new ModelAndView("project/awardedcontract/awardedContract")
        .addObject("form", form)
        .addObject("contractBands", ContractBand.getAllAsMap(ProjectType.FORWARD_WORK_PLAN))
        .addObject("contractFunctionRestUrl", getContractFunctionSearchUrl())
        .addObject("preSelectedContractFunctionMap", preSelectedContractFunctionMap);

    breadcrumbService.fromForwardWorkPlanAwardedContracts(projectId, modelAndView, PAGE_NAME_SINGULAR);

    return modelAndView;
  }

}
