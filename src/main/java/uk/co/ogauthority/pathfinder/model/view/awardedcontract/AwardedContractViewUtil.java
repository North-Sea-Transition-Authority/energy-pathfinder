package uk.co.ogauthority.pathfinder.model.view.awardedcontract;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractController;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractRemovalController;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.infrastructure.InfrastructureAwardedContractController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure.AwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class AwardedContractViewUtil {

  private static final Class<InfrastructureAwardedContractController> INFRASTRUCTURE_CONTROLLER = InfrastructureAwardedContractController.class;
  private static final Class<ForwardWorkPlanAwardedContractController> FORWARD_WORK_PLAN_CONTROLLER = ForwardWorkPlanAwardedContractController.class;

  private AwardedContractViewUtil() {
    throw new IllegalStateException("AwardedContractViewUtil is a util class and should not be instantiated");
  }

  public static SummaryLink getEditLink(Integer projectId, Integer awardedContractId, ProjectType projectType) {
    if (ProjectType.INFRASTRUCTURE.equals(projectType)) {
      return new SummaryLink(
          SummaryLinkText.EDIT.getDisplayName(),
          ReverseRouter.route(on(INFRASTRUCTURE_CONTROLLER).getAwardedContract(
              projectId,
              awardedContractId,
              null
          ))
      );
    }
    return new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        ReverseRouter.route(on(FORWARD_WORK_PLAN_CONTROLLER).getAwardedContract(
            projectId,
            awardedContractId,
            null
        ))
    );
  }

  public static SummaryLink getDeleteLink(Integer projectId, Integer awardedContractId, Integer displayOrder, ProjectType projectType) {
    if (ProjectType.INFRASTRUCTURE.equals(projectType)) {
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

  public static class AwardedContractViewBuilder {

    private final AwardedContract awardedContract;
    private final int displayOrder;
    private final PortalOrganisationGroup addedByPortalOrganisationGroup;
    private boolean isValid = true;
    private boolean includeSummaryLinks = false;

    public AwardedContractViewBuilder(AwardedContract awardedContract,
                                      Integer displayOrder,
                                      PortalOrganisationGroup addedByPortalOrganisationGroup) {
      this.awardedContract = awardedContract;
      this.displayOrder = displayOrder;
      this.addedByPortalOrganisationGroup = addedByPortalOrganisationGroup;
    }

    public AwardedContractViewBuilder isValid(boolean isValid) {
      this.isValid = isValid;
      return this;
    }

    public AwardedContractViewBuilder includeSummaryLinks(boolean includeSummaryLinks) {
      this.includeSummaryLinks = includeSummaryLinks;
      return this;
    }

    public AwardedContractView build() {
      return createAwardedContractView(
          this.awardedContract,
          this.displayOrder,
          this.isValid,
          this.includeSummaryLinks,
          this.addedByPortalOrganisationGroup
      );
    }

    private static AwardedContractView createAwardedContractView(AwardedContract awardedContract,
                                                                 int displayOrder,
                                                                 boolean isValid,
                                                                 boolean includeSummaryLinks,
                                                                 PortalOrganisationGroup addedByPortalOrganisationGroup) {
      var awardedContractView = new AwardedContractView();
      awardedContractView.setDisplayOrder(displayOrder);
      awardedContractView.setId(awardedContract.getId());

      var projectId = awardedContract.getProjectDetail().getProject().getId();
      awardedContractView.setProjectId(projectId);
      awardedContractView.setContractorName(awardedContract.getContractorName());

      var projectType = awardedContract.getProjectDetail().getProjectType();

      var contractFunction = (awardedContract.getContractFunction() != null)
          ? new StringWithTag(awardedContract.getContractFunction().getDisplayName(), Tag.NONE)
          : new StringWithTag(awardedContract.getManualContractFunction(), Tag.NOT_FROM_LIST);
      awardedContractView.setContractFunction(contractFunction);

      awardedContractView.setDescriptionOfWork(awardedContract.getDescriptionOfWork());
      awardedContractView.setDateAwarded(DateUtil.formatDate(awardedContract.getDateAwarded()));

      var contractBand = (awardedContract.getContractBand() != null)
          ? awardedContract.getContractBand().getDisplayName()
          : null;
      awardedContractView.setContractBand(contractBand);

      awardedContractView.setContactName(awardedContract.getContactName());
      awardedContractView.setContactPhoneNumber(awardedContract.getPhoneNumber());
      awardedContractView.setContactJobTitle(awardedContract.getJobTitle());
      awardedContractView.setContactEmailAddress(awardedContract.getEmailAddress());

      var summaryLinks = new ArrayList<SummaryLink>();
      if (includeSummaryLinks) {
        summaryLinks.add(getEditLink(projectId, awardedContract.getId(), projectType));
        summaryLinks.add(getDeleteLink(projectId, awardedContract.getId(), displayOrder, projectType));
      }

      awardedContractView.setSummaryLinks(summaryLinks);
      awardedContractView.setAddedByPortalOrganisationGroup(resolvePortalOrganisationName(addedByPortalOrganisationGroup));
      awardedContractView.setIsValid(isValid);

      return awardedContractView;
    }

    private static String resolvePortalOrganisationName(PortalOrganisationGroup portalOrganisationGroup) {
      if (StringUtils.isBlank(portalOrganisationGroup.getName())) {
        return "Unknown organisation";
      }
      return portalOrganisationGroup.getName();
    }
  }
}
