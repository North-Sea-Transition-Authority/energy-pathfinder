package uk.co.ogauthority.pathfinder.model.view.awardedcontract.forwardworkplan;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractController;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractRemovalController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContract;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.AwardedContractViewUtilCommon;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

public class ForwardWorkPlanAwardedContractViewUtil {

  private ForwardWorkPlanAwardedContractViewUtil() {
    throw new IllegalStateException("ForwardWorkPlanAwardedContractViewUtil is a utility class and should not be instantiated");
  }

  public static class ForwardWorkPlanAwardedContractViewBuilder {

    private final ForwardWorkPlanAwardedContract awardedContract;
    private final int displayOrder;
    private final PortalOrganisationGroup addedByPortalOrganisationGroup;
    private boolean isValid = true;
    private boolean includeSummaryLinks = false;

    public ForwardWorkPlanAwardedContractViewBuilder(ForwardWorkPlanAwardedContract awardedContract,
                                                     Integer displayOrder,
                                                     PortalOrganisationGroup addedByPortalOrganisationGroup) {
      this.awardedContract = awardedContract;
      this.displayOrder = displayOrder;
      this.addedByPortalOrganisationGroup = addedByPortalOrganisationGroup;
    }

    public ForwardWorkPlanAwardedContractViewBuilder isValid(boolean isValid) {
      this.isValid = isValid;
      return this;
    }

    public ForwardWorkPlanAwardedContractViewBuilder includeSummaryLinks(boolean includeSummaryLinks) {
      this.includeSummaryLinks = includeSummaryLinks;
      return this;
    }

    public ForwardWorkPlanAwardedContractView build() {
      return createAwardedContractView(
          this.awardedContract,
          this.displayOrder,
          this.isValid,
          this.includeSummaryLinks,
          this.addedByPortalOrganisationGroup
      );
    }

    private static ForwardWorkPlanAwardedContractView createAwardedContractView(
        ForwardWorkPlanAwardedContract awardedContract,
        int displayOrder,
        boolean isValid,
        boolean includeSummaryLinks,
        PortalOrganisationGroup addedByPortalOrganisationGroup) {

      var projectId = awardedContract.getProjectDetail().getProject().getId();
      var editUrl = getEditLink(projectId, awardedContract.getId());
      var deleteUrl = getDeleteLink(projectId, awardedContract.getId(), displayOrder);

      return (ForwardWorkPlanAwardedContractView) AwardedContractViewUtilCommon.populateView(
          new ForwardWorkPlanAwardedContractView(),
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
        ReverseRouter.route(on(ForwardWorkPlanAwardedContractController.class).getAwardedContract(
            projectId,
            awardedContractId,
            null
        ))
    );
  }

  public static SummaryLink getDeleteLink(Integer projectId, Integer awardedContractId, Integer displayOrder) {
    return new SummaryLink(
        SummaryLinkText.DELETE.getDisplayName(),
        ReverseRouter.route(on(ForwardWorkPlanAwardedContractRemovalController.class).removeAwardedContract(
            projectId,
            awardedContractId,
            displayOrder,
            null
        ))
    );
  }

}
