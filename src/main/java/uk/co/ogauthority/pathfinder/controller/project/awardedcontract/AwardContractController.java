package uk.co.ogauthority.pathfinder.controller.project.awardedcontract;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Map;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.ProjectFormPageController;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSummaryController;
import uk.co.ogauthority.pathfinder.controller.rest.ContractFunctionRestController;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.AwardedContractCommon;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormCommon;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.OrganisationGroupIdWrapper;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

public abstract class AwardContractController extends ProjectFormPageController {

  public static final String PAGE_NAME = "Awarded contracts";
  public static final String PAGE_NAME_SINGULAR = "Awarded contract";
  public static final String REMOVE_PAGE_NAME = "Remove awarded contract";

  protected final ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  public AwardContractController(BreadcrumbService breadcrumbService,
                                 ControllerHelperService controllerHelperService,
                                 ProjectSectionItemOwnershipService projectSectionItemOwnershipService) {
    super(breadcrumbService, controllerHelperService);
    this.projectSectionItemOwnershipService = projectSectionItemOwnershipService;
  }

  protected String getContractFunctionSearchUrl() {
    return SearchSelectorService.route(
        on(ContractFunctionRestController.class).searchContractFunctions(null)
    );
  }

  protected <E extends AwardedContractCommon> void checkIfUserHasAccessAwardedContract(E awardedContract) {
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

  protected ModelAndView getForwardWorkPlanAwardedContractSummaryRedirect(Integer projectId) {
    return ReverseRouter.redirect(on(ForwardWorkPlanAwardedContractSummaryController.class)
        .viewAwardedContracts(projectId, null));
  }

  protected <E extends AwardedContractFormCommon> ModelAndView getAwardedContractModelAndView(
      Integer projectId,
      E form,
      Map<String, String> preSelectedContractFunctionMap,
      ProjectDetail projectDetail
  ) {
    var projectType = projectDetail.getProjectType();
    var modelAndView = new ModelAndView("project/awardedcontract/awardedContract")
        .addObject("form", form)
        .addObject("contractBands", ContractBand.getAllAsMap(projectType))
        .addObject("contractFunctionRestUrl", getContractFunctionSearchUrl())
        .addObject("preSelectedContractFunctionMap", preSelectedContractFunctionMap);

    breadcrumbService.fromInfrastructureAwardedContracts(projectId, modelAndView, PAGE_NAME_SINGULAR);

    return modelAndView;
  }

}
