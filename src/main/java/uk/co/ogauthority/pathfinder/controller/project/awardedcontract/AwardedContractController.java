package uk.co.ogauthority.pathfinder.controller.project.awardedcontract;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

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
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.rest.ContractFunctionRestController;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/awarded-contracts")
public class AwardedContractController {

  public static final String PAGE_NAME = "Awarded contracts";
  public static final String PAGE_NAME_SINGULAR = "Awarded contract";

  private final BreadcrumbService breadcrumbService;
  private final AwardedContractService awardedContractService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public AwardedContractController(BreadcrumbService breadcrumbService,
                                   AwardedContractService awardedContractService,
                                   ControllerHelperService controllerHelperService) {
    this.breadcrumbService = breadcrumbService;
    this.awardedContractService = awardedContractService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView viewAwardedContracts(@PathVariable("projectId") Integer projectId,
                                          ProjectContext projectContext) {
    return getAwardedContractsSummaryModelAndView(projectId, projectContext);
  }

  @GetMapping("/awarded-contract")
  public ModelAndView addAwardedContract(@PathVariable("projectId") Integer projectId,
                                         ProjectContext projectContext) {
    return getAwardedContractModelAndView(projectId, new AwardedContractForm());
  }

  @GetMapping("/awarded-contract/{awardedContractId}")
  public ModelAndView getAwardedContract(@PathVariable("projectId") Integer projectId,
                                         @PathVariable("awardedContractId") Integer awardedProjectId,
                                         ProjectContext projectContext) {
    var form = awardedContractService.getForm(awardedProjectId, projectContext.getProjectDetails());
    return getAwardedContractModelAndView(projectId, form);
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
          awardedContractService.createAwardedContract(projectContext.getProjectDetails(), form);
          return getAwardedContractSummaryRedirect(projectId, projectContext);
        }
    );
  }

  @PostMapping("/awarded-contract/{awardedContractId}")
  public ModelAndView saveAwardedContract(@PathVariable("projectId") Integer projectId,
                                          @PathVariable("awardedContractId") Integer awardedProjectId,
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
          awardedContractService.updateAwardedContract(awardedProjectId, projectContext.getProjectDetails(), form);
          return getAwardedContractSummaryRedirect(projectId, projectContext);
        }
    );
  }

  private ModelAndView getAwardedContractsSummaryModelAndView(Integer projectId, ProjectContext projectContext) {
    var modelAndView = new ModelAndView("project/awardedcontract/awardedContractSummary")
        .addObject("addAwardedContractUrl",
            ReverseRouter.route(on(AwardedContractController.class).addAwardedContract(projectId, projectContext))
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

  private ModelAndView getAwardedContractSummaryRedirect(Integer projectId, ProjectContext projectContext) {
    return ReverseRouter.redirect(on(AwardedContractController.class).viewAwardedContracts(projectId, projectContext));
  }

  private String getContractFunctionSearchUrl() {
    return SearchSelectorService.route(
        on(ContractFunctionRestController.class).searchContractFunctions(null)
    );
  }
}
