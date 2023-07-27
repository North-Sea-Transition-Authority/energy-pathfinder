package uk.co.ogauthority.pathfinder.model.view.awardedcontract.infrastructure;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.infrastructure.InfrastructureAwardedContractController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure.InfrastructureAwardedContract;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.AwardedContractViewUtilCommon;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

public class InfrastructureAwardedContractViewUtil {

  private static final Class<InfrastructureAwardedContractController> INFRASTRUCTURE_CONTROLLER = InfrastructureAwardedContractController.class;

  private InfrastructureAwardedContractViewUtil() {
    throw new IllegalStateException("InfrastructureAwardedContractViewUtil is a utility class and should not be instantiated");
  }

  public static class InfrastructureAwardedContractViewBuilder {

    private final InfrastructureAwardedContract awardedContract;
    private final int displayOrder;
    private final PortalOrganisationGroup addedByPortalOrganisationGroup;
    private boolean isValid = true;
    private boolean includeSummaryLinks = false;

    public InfrastructureAwardedContractViewBuilder(InfrastructureAwardedContract awardedContract,
                                                    Integer displayOrder,
                                                    PortalOrganisationGroup addedByPortalOrganisationGroup) {
      this.awardedContract = awardedContract;
      this.displayOrder = displayOrder;
      this.addedByPortalOrganisationGroup = addedByPortalOrganisationGroup;
    }

    public InfrastructureAwardedContractViewBuilder isValid(boolean isValid) {
      this.isValid = isValid;
      return this;
    }

    public InfrastructureAwardedContractViewBuilder includeSummaryLinks(boolean includeSummaryLinks) {
      this.includeSummaryLinks = includeSummaryLinks;
      return this;
    }

    public InfrastructureAwardedContractView build() {
      return createAwardedContractView(
          this.awardedContract,
          this.displayOrder,
          this.isValid,
          this.includeSummaryLinks,
          this.addedByPortalOrganisationGroup
      );
    }

    private static InfrastructureAwardedContractView createAwardedContractView(InfrastructureAwardedContract awardedContract,
                                                                               int displayOrder,
                                                                               boolean isValid,
                                                                               boolean includeSummaryLinks,
                                                                               PortalOrganisationGroup addedByPortalOrganisationGroup) {

      var projectId = awardedContract.getProjectDetail().getProject().getId();
      var editUrl = getEditLink(projectId, awardedContract.getId());
      var deleteUrl = getDeleteLink(projectId, awardedContract.getId(), displayOrder);

      return (InfrastructureAwardedContractView) AwardedContractViewUtilCommon.populateView(
          new InfrastructureAwardedContractView(),
          awardedContract,
          includeSummaryLinks,
          displayOrder,
          editUrl,
          deleteUrl,
          addedByPortalOrganisationGroup,
          isValid
      );
    }
  }

  public static SummaryLink getEditLink(Integer projectId, Integer awardedContractId) {
    return new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        ReverseRouter.route(on(INFRASTRUCTURE_CONTROLLER).getAwardedContract(
            projectId,
            awardedContractId,
            null
        ))
    );
  }

  public static SummaryLink getDeleteLink(Integer projectId, Integer awardedContractId, Integer displayOrder) {
    return new SummaryLink(
        SummaryLinkText.DELETE.getDisplayName(),
        ReverseRouter.route(on(INFRASTRUCTURE_CONTROLLER).removeAwardedContract(
            projectId,
            awardedContractId,
            displayOrder,
            null
        ))
    );
  }
}
