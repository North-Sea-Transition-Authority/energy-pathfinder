package uk.co.ogauthority.pathfinder.controller.project.awardedcontract;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import uk.co.ogauthority.pathfinder.controller.project.ProjectFormPageController;
import uk.co.ogauthority.pathfinder.controller.rest.ContractFunctionRestController;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure.AwardedContract;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.OrganisationGroupIdWrapper;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractServiceCommon;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractSummaryService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

public abstract class AwardContractController extends ProjectFormPageController {

  public static final String PAGE_NAME = "Awarded contracts";
  public static final String PAGE_NAME_SINGULAR = "Awarded contract";
  public static final String REMOVE_PAGE_NAME = "Remove awarded contract";

  protected final ProjectSectionItemOwnershipService projectSectionItemOwnershipService;
  protected final AwardedContractServiceCommon awardedContractServiceCommon;
  protected final AwardedContractSummaryService awardedContractSummaryService;

  public AwardContractController(BreadcrumbService breadcrumbService,
                                 ControllerHelperService controllerHelperService,
                                 ProjectSectionItemOwnershipService projectSectionItemOwnershipService,
                                 AwardedContractServiceCommon awardedContractServiceCommon,
                                 AwardedContractSummaryService awardedContractSummaryService) {
    super(breadcrumbService, controllerHelperService);
    this.projectSectionItemOwnershipService = projectSectionItemOwnershipService;
    this.awardedContractServiceCommon = awardedContractServiceCommon;
    this.awardedContractSummaryService = awardedContractSummaryService;
  }

  protected String getContractFunctionSearchUrl() {
    return SearchSelectorService.route(
        on(ContractFunctionRestController.class).searchContractFunctions(null)
    );
  }

  protected void checkIfUserHasAccessAwardedContract(AwardedContract awardedContract) {
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
