package uk.co.ogauthority.pathfinder.controller.project.awardedcontract;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.ProjectFormPageController;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.AllowProjectContributorAccess;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.controller.rest.ContractFunctionRestController;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.AwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractForm;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.AwardedContractView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.OrganisationGroupIdWrapper;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractSummaryService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@AllowProjectContributorAccess
@ProjectTypeCheck(types = ProjectType.INFRASTRUCTURE)
@RequestMapping("/project/{projectId}/awarded-contracts")
public class AwardedContractController extends ProjectFormPageController {

  public static final String PAGE_NAME = "Awarded contracts";
  public static final String PAGE_NAME_SINGULAR = "Awarded contract";
  public static final String REMOVE_PAGE_NAME = "Remove awarded contract";

  private final AwardedContractService awardedContractService;
  private final AwardedContractSummaryService awardedContractSummaryService;
  private final ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  @Autowired
  public AwardedContractController(BreadcrumbService breadcrumbService,
                                   AwardedContractService awardedContractService,
                                   ControllerHelperService controllerHelperService,
                                   AwardedContractSummaryService awardedContractSummaryService,
                                   ProjectSectionItemOwnershipService projectSectionItemOwnershipService) {
    super(breadcrumbService, controllerHelperService);
    this.awardedContractService = awardedContractService;
    this.awardedContractSummaryService = awardedContractSummaryService;
    this.projectSectionItemOwnershipService = projectSectionItemOwnershipService;
  }

  @GetMapping
  public ModelAndView viewAwardedContracts(@PathVariable("projectId") Integer projectId,
                                           ProjectContext projectContext) {
    var awardedContractViews = awardedContractSummaryService.getAwardedContractViews(
        projectContext.getProjectDetails()
    );
    return getAwardedContractsSummaryModelAndView(projectId, awardedContractViews, ValidationResult.NOT_VALIDATED);
  }

  @GetMapping("/awarded-contract")
  public ModelAndView addAwardedContract(@PathVariable("projectId") Integer projectId,
                                         ProjectContext projectContext) {
    return getAwardedContractModelAndView(projectId, new AwardedContractForm());
  }

  @GetMapping("/awarded-contract/{awardedContractId}/edit")
  public ModelAndView getAwardedContract(@PathVariable("projectId") Integer projectId,
                                         @PathVariable("awardedContractId") Integer awardedProjectId,
                                         ProjectContext projectContext) {
    var awardedContract = awardedContractService.getAwardedContract(
        awardedProjectId,
        projectContext.getProjectDetails());
    checkIfUserHasAccessAwardedContract(awardedContract);
    var form = awardedContractService.getForm(awardedProjectId, projectContext.getProjectDetails());
    return getAwardedContractModelAndView(projectId, form);
  }

  @GetMapping("/awarded-contract/{awardedContractId}/remove/{displayOrder}")
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

  @PostMapping("/awarded-contract")
  public ModelAndView saveAwardedContract(@PathVariable("projectId") Integer projectId,
                                          @Valid @ModelAttribute("form") AwardedContractForm form,
                                          BindingResult bindingResult,
                                          ValidationType validationType,
                                          ProjectContext projectContext) {
    bindingResult = awardedContractService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getAwardedContractModelAndView(projectId, form),
        form,
        () -> {
          var contract = awardedContractService.createAwardedContract(
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
          return getAwardedContractSummaryRedirect(projectId);
        }
    );
  }

  @PostMapping("/awarded-contract/{awardedContractId}/edit")
  public ModelAndView saveAwardedContract(@PathVariable("projectId") Integer projectId,
                                          @PathVariable("awardedContractId") Integer awardedContractId,
                                          @Valid @ModelAttribute("form") AwardedContractForm form,
                                          BindingResult bindingResult,
                                          ValidationType validationType,
                                          ProjectContext projectContext) {
    var awardedContract = awardedContractService.getAwardedContract(
        awardedContractId,
        projectContext.getProjectDetails()
    );
    checkIfUserHasAccessAwardedContract(awardedContract);
    bindingResult = awardedContractService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getAwardedContractModelAndView(projectId, form),
        form,
        () -> {
          var contract = awardedContractService.updateAwardedContract(awardedContractId,
              projectContext.getProjectDetails(), form);
          AuditService.audit(
              AuditEvent.AWARDED_CONTRACT_UPDATED,
              String.format(
                  AuditEvent.AWARDED_CONTRACT_UPDATED.getMessage(),
                  contract.getId(),
                  projectContext.getProjectDetails().getId()
              )
          );

          return getAwardedContractSummaryRedirect(projectId);
        }
    );
  }

  @PostMapping("/awarded-contract/{awardedContractId}/remove/{displayOrder}")
  public ModelAndView removeAwardedContract(@PathVariable("projectId") Integer projectId,
                                            @PathVariable("awardedContractId") Integer awardedContractId,
                                            @PathVariable("displayOrder") Integer displayOrder,
                                            ProjectContext projectContext) {
    var awardedContract = awardedContractService.getAwardedContract(
        awardedContractId,
        projectContext.getProjectDetails()
    );
    checkIfUserHasAccessAwardedContract(awardedContract);
    awardedContractService.deleteAwardedContract(awardedContract);
    AuditService.audit(
        AuditEvent.AWARDED_CONTRACT_REMOVED,
        String.format(
            AuditEvent.AWARDED_CONTRACT_REMOVED.getMessage(),
            awardedContractId,
            projectContext.getProjectDetails().getId()
        )
    );
    return getAwardedContractSummaryRedirect(projectId);
  }

  @PostMapping
  public ModelAndView saveAwardedContractSummary(@PathVariable("projectId") Integer projectId,
                                                 ProjectContext projectContext) {

    var awardedContractViews = awardedContractSummaryService.getValidatedAwardedContractViews(
        projectContext.getProjectDetails()
    );

    var validationResult = awardedContractSummaryService.validateViews(awardedContractViews);

    return validationResult.equals(ValidationResult.VALID)
        ? ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null))
        : getAwardedContractsSummaryModelAndView(projectId, awardedContractViews, validationResult);
  }

  private ModelAndView getAwardedContractsSummaryModelAndView(Integer projectId,
                                                              List<AwardedContractView> awardedContractViews,
                                                              ValidationResult validationResult) {
    var modelAndView = new ModelAndView("project/awardedcontract/awardedContractFormSummary")
        .addObject("pageTitle", PAGE_NAME)
        .addObject("awardedContractViews", awardedContractViews)
        .addObject("addAwardedContractUrl",
            ReverseRouter.route(on(AwardedContractController.class).addAwardedContract(projectId, null))
        )
        .addObject("backToTaskListUrl",
            ControllerUtils.getBackToTaskListUrl(projectId)
        )
        .addObject("projectSetupUrl", ControllerUtils.getProjectSetupUrl(projectId))
        .addObject("errorList",
            validationResult.equals(ValidationResult.INVALID)
                ? awardedContractSummaryService.getAwardedContractViewErrors(awardedContractViews)
                : null
        );
    breadcrumbService.fromTaskList(projectId, modelAndView, PAGE_NAME);
    return modelAndView;
  }

  private ModelAndView getAwardedContractModelAndView(Integer projectId, AwardedContractForm form) {

    var preSelectedContractFunctionMap = awardedContractService.getPreSelectedContractFunction(form);

    var modelAndView = new ModelAndView("project/awardedcontract/awardedContract")
        .addObject("form", form)
        .addObject("contractBands", ContractBand.getAllAsMap())
        .addObject("contractFunctionRestUrl", getContractFunctionSearchUrl())
        .addObject("preSelectedContractFunctionMap", preSelectedContractFunctionMap);

    breadcrumbService.fromAwardedContracts(projectId, modelAndView, PAGE_NAME_SINGULAR);

    return modelAndView;
  }

  private ModelAndView removeAwardedContractModelAndView(Integer projectId, AwardedContractView awardedContractView) {
    var modelAndView = new ModelAndView("project/awardedcontract/removeAwardedContract")
        .addObject("awardedContractView", awardedContractView)
        .addObject("cancelUrl", getAwardedContractSummaryUrl(projectId));

    breadcrumbService.fromAwardedContracts(projectId, modelAndView, REMOVE_PAGE_NAME);

    return modelAndView;
  }

  private ModelAndView getAwardedContractSummaryRedirect(Integer projectId) {
    return ReverseRouter.redirect(on(AwardedContractController.class).viewAwardedContracts(projectId, null));
  }

  private String getContractFunctionSearchUrl() {
    return SearchSelectorService.route(
        on(ContractFunctionRestController.class).searchContractFunctions(null)
    );
  }

  private String getAwardedContractSummaryUrl(Integer projectId) {
    return ReverseRouter.route(on(AwardedContractController.class).viewAwardedContracts(projectId, null));
  }

  private void checkIfUserHasAccessAwardedContract(AwardedContract awardedContract) {
    if (!projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        awardedContract.getProjectDetail(),
        new OrganisationGroupIdWrapper(awardedContract.getAddedByOrganisationGroup())
    )) {
      throw new AccessDeniedException(
          String.format(
              "User does not have access to the awarded contract with id: %d",
              awardedContract.getId())
      );
    }
  }
}
