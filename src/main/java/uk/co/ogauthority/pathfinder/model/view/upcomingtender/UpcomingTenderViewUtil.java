package uk.co.ogauthority.pathfinder.model.view.upcomingtender;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import uk.co.ogauthority.pathfinder.controller.project.upcomingtender.UpcomingTendersController;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class UpcomingTenderViewUtil {

  private UpcomingTenderViewUtil() {
    throw new IllegalStateException("UpcomingTenderViewFactory is a utility class and should not be instantiated");
  }

  public static class UpcomingTenderViewBuilder {

    private final UpcomingTender upcomingTender;
    private final int displayOrder;
    private final List<UploadedFileView> uploadedFileViews;
    private boolean isValid = true;
    private boolean includeSummaryLinks = false;

    public UpcomingTenderViewBuilder(UpcomingTender upcomingTender,
                                     int displayOrder,
                                     List<UploadedFileView> uploadedFileViews) {
      this.upcomingTender = upcomingTender;
      this.displayOrder = displayOrder;
      this.uploadedFileViews = uploadedFileViews;
    }

    public UpcomingTenderViewBuilder isValid(boolean isValid) {
      this.isValid = isValid;
      return this;
    }

    public UpcomingTenderViewBuilder includeSummaryLinks(boolean includeSummaryLinks) {
      this.includeSummaryLinks = includeSummaryLinks;
      return this;
    }

    public UpcomingTenderView build() {
      return createUpComingTenderView(
          this.upcomingTender,
          this.displayOrder,
          this.uploadedFileViews,
          this.isValid,
          this.includeSummaryLinks
      );
    }

    private static UpcomingTenderView createUpComingTenderView(
        UpcomingTender upcomingTender,
        Integer displayOrder,
        List<UploadedFileView> uploadedFileViews,
        boolean isValid,
        boolean includeSummaryLinks
    ) {
      var projectId = upcomingTender.getProjectDetail().getProject().getId();
      var upcomingTenderView = new UpcomingTenderView(
          displayOrder,
          upcomingTender.getId(),
          projectId
      );

      var tenderFunction = new StringWithTag();

      if (upcomingTender.getTenderFunction() != null) {
        tenderFunction = new StringWithTag(upcomingTender.getTenderFunction().getDisplayName(), Tag.NONE);
      } else if (upcomingTender.getManualTenderFunction() != null) {
        tenderFunction = new StringWithTag(upcomingTender.getManualTenderFunction(), Tag.NOT_FROM_LIST);
      }

      upcomingTenderView.setTenderFunction(tenderFunction);
      upcomingTenderView.setDescriptionOfWork(upcomingTender.getDescriptionOfWork());
      upcomingTenderView.setEstimatedTenderDate(DateUtil.formatDate(upcomingTender.getEstimatedTenderDate()));
      upcomingTenderView.setContractBand(
          upcomingTender.getContractBand() != null
              ? upcomingTender.getContractBand().getDisplayName()
              : ""
      );

      upcomingTenderView.setContactName(upcomingTender.getName());
      upcomingTenderView.setContactPhoneNumber(upcomingTender.getPhoneNumber());
      upcomingTenderView.setContactEmailAddress(upcomingTender.getEmailAddress());
      upcomingTenderView.setContactJobTitle(upcomingTender.getJobTitle());

      var links = new ArrayList<SummaryLink>();
      if (includeSummaryLinks) {
        var editLink = new SummaryLink(
            SummaryLinkText.EDIT.getDisplayName(),
            ReverseRouter.route(on(UpcomingTendersController.class).editUpcomingTender(
                projectId,
                upcomingTender.getId(),
                null
            ))
        );
        links.add(editLink);

        var removeLink = new SummaryLink(
            SummaryLinkText.DELETE.getDisplayName(),
            ReverseRouter.route(on(UpcomingTendersController.class).removeUpcomingTenderConfirm(
                projectId,
                upcomingTender.getId(),
                displayOrder,
                null
            ))
        );
        links.add(removeLink);
      }

      upcomingTenderView.setSummaryLinks(links);
      upcomingTenderView.setUploadedFileViews(uploadedFileViews);
      upcomingTenderView.setIsValid(isValid);

      return upcomingTenderView;
    }
  }
}
