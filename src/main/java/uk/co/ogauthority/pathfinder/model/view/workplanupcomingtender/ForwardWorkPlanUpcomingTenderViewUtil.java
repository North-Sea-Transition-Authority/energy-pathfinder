package uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderConversionController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.duration.DurationPeriod;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class ForwardWorkPlanUpcomingTenderViewUtil {

  private ForwardWorkPlanUpcomingTenderViewUtil() {
    throw new IllegalStateException(
        "ForwardWorkPlanUpcomingTenderViewUtil is a utility class and should not be instantiated"
    );
  }

  public static class ForwardWorkPlanUpcomingTenderViewBuilder {

    private final ForwardWorkPlanUpcomingTender forwardWorkPlanUpcomingTender;
    private final int displayOrder;
    private final PortalOrganisationGroup addedByPortalOrganisationGroup;

    private boolean isValid = true;
    private boolean includeSummaryLinks = false;

    public ForwardWorkPlanUpcomingTenderViewBuilder(ForwardWorkPlanUpcomingTender forwardWorkPlanUpcomingTender,
                                                    int displayOrder,
                                                    PortalOrganisationGroup addedByPortalOrganisationGroup) {
      this.forwardWorkPlanUpcomingTender = forwardWorkPlanUpcomingTender;
      this.displayOrder = displayOrder;
      this.addedByPortalOrganisationGroup = addedByPortalOrganisationGroup;
    }

    public ForwardWorkPlanUpcomingTenderViewBuilder isValid(boolean isValid) {
      this.isValid = isValid;
      return this;
    }

    public ForwardWorkPlanUpcomingTenderViewBuilder includeSummaryLinks(boolean includeSummaryLinks) {
      this.includeSummaryLinks = includeSummaryLinks;
      return this;
    }

    public ForwardWorkPlanUpcomingTenderView build() {
      return createUpcomingTenderView(
          this.forwardWorkPlanUpcomingTender,
          this.displayOrder,
          this.isValid,
          this.includeSummaryLinks,
          this.addedByPortalOrganisationGroup
      );
    }

    private static ForwardWorkPlanUpcomingTenderView createUpcomingTenderView(
        ForwardWorkPlanUpcomingTender forwardWorkPlanUpcomingTender,
        Integer displayOrder,
        boolean isValid,
        boolean includeSummaryLinks,
        PortalOrganisationGroup addedByPortalOrganisationGroup
    ) {
      var projectId = forwardWorkPlanUpcomingTender.getProjectDetail().getProject().getId();
      var workPlanUpcomingTenderView = new ForwardWorkPlanUpcomingTenderView(
          displayOrder,
          forwardWorkPlanUpcomingTender.getId(),
          projectId
      );

      var tenderFunction = new StringWithTag();

      if (forwardWorkPlanUpcomingTender.getDepartmentType() != null) {
        tenderFunction = new StringWithTag(forwardWorkPlanUpcomingTender.getDepartmentType().getDisplayName(),
            Tag.NONE);
      } else if (forwardWorkPlanUpcomingTender.getManualDepartmentType() != null) {
        tenderFunction = new StringWithTag(forwardWorkPlanUpcomingTender.getManualDepartmentType(), Tag.NOT_FROM_LIST);
      }

      workPlanUpcomingTenderView.setTenderDepartment(tenderFunction);
      workPlanUpcomingTenderView.setDescriptionOfWork(forwardWorkPlanUpcomingTender.getDescriptionOfWork());
      workPlanUpcomingTenderView.setEstimatedTenderStartDate(DateUtil.getDateFromQuarterYear(
          forwardWorkPlanUpcomingTender.getEstimatedTenderDateQuarter(),
          forwardWorkPlanUpcomingTender.getEstimatedTenderDateYear()));
      workPlanUpcomingTenderView.setContractBand(
          forwardWorkPlanUpcomingTender.getContractBand() != null
              ? forwardWorkPlanUpcomingTender.getContractBand().getDisplayName()
              : ""
      );

      workPlanUpcomingTenderView.setContactName(forwardWorkPlanUpcomingTender.getContactName());
      workPlanUpcomingTenderView.setContactPhoneNumber(forwardWorkPlanUpcomingTender.getPhoneNumber());
      workPlanUpcomingTenderView.setContactEmailAddress(forwardWorkPlanUpcomingTender.getEmailAddress());
      workPlanUpcomingTenderView.setContactJobTitle(forwardWorkPlanUpcomingTender.getJobTitle());

      var contractLength = getContractLength(
          forwardWorkPlanUpcomingTender.getContractTermDurationPeriod(),
          forwardWorkPlanUpcomingTender.getContractTermDuration()
      );

      workPlanUpcomingTenderView.setContractLength(contractLength);

      List<SummaryLink> summaryLinks = new ArrayList<>();
      if (includeSummaryLinks) {
        var editLink = new SummaryLink(
            SummaryLinkText.EDIT.getDisplayName(),
            ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class).editUpcomingTender(
                projectId,
                forwardWorkPlanUpcomingTender.getId(),
                null
            ))
        );
        summaryLinks.add(editLink);

        var convertLink = new SummaryLink(
            SummaryLinkText.CONVERT_TO_AWARDED.getDisplayName(),
            ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderConversionController.class).convertUpcomingTenderConfirm(
                projectId,
                forwardWorkPlanUpcomingTender.getId(),
                displayOrder,
                null
            ))
        );
        summaryLinks.add(convertLink);

        var removeLink = new SummaryLink(
            SummaryLinkText.DELETE.getDisplayName(),
            ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class).removeUpcomingTenderConfirm(
                projectId,
                forwardWorkPlanUpcomingTender.getId(),
                displayOrder,
                null
            ))
        );
        summaryLinks.add(removeLink);
      }

      workPlanUpcomingTenderView.setSummaryLinks(summaryLinks);
      workPlanUpcomingTenderView.setIsValid(isValid);
      workPlanUpcomingTenderView.setAddedByPortalOrganisationGroup(
          resolvePortalOrganisationGroupName(addedByPortalOrganisationGroup)
      );

      return workPlanUpcomingTenderView;
    }

    private static String resolvePortalOrganisationGroupName(PortalOrganisationGroup portalOrganisationGroup) {
      if (StringUtils.isBlank(portalOrganisationGroup.getName())) {
        return "Unknown organisation";
      }
      return portalOrganisationGroup.getName();
    }

    private static String getContractLength(DurationPeriod contractTermDurationPeriod,
                                            Integer contractTermDuration) {

      var contractLength = "";

      if (contractTermDuration != null && contractTermDurationPeriod != null) {
        contractLength = String.format(
            "%s %s",
            contractTermDuration,
            (contractTermDuration == 1)
                ? contractTermDurationPeriod.getDisplayNameSingular().toLowerCase()
                : contractTermDurationPeriod.getDisplayNamePlural().toLowerCase()
        );
      }

      return contractLength;
    }
  }
}
