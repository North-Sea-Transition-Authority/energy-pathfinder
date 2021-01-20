package uk.co.ogauthority.pathfinder.model.view.upcomingtender;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

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

  public static UpcomingTenderView createUpComingTenderView(UpcomingTender upcomingTender,
                                                            Integer displayOrder,
                                                            List<UploadedFileView> uploadedFileViews) {
    var projectId = upcomingTender.getProjectDetail().getProject().getId();
    var tender = new UpcomingTenderView(
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

    tender.setTenderFunction(tenderFunction);
    tender.setDescriptionOfWork(upcomingTender.getDescriptionOfWork());
    tender.setEstimatedTenderDate(DateUtil.formatDate(upcomingTender.getEstimatedTenderDate()));
    tender.setContractBand(
        upcomingTender.getContractBand() != null
            ? upcomingTender.getContractBand().getDisplayName()
            : ""
    );

    tender.setContactName(upcomingTender.getName());
    tender.setContactPhoneNumber(upcomingTender.getPhoneNumber());
    tender.setContactEmailAddress(upcomingTender.getEmailAddress());
    tender.setContactJobTitle(upcomingTender.getJobTitle());

    var editLink = new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        ReverseRouter.route(on(UpcomingTendersController.class).editUpcomingTender(
            projectId,
            upcomingTender.getId(),
            null
        ))
    );

    var removeLink = new SummaryLink(
        SummaryLinkText.DELETE.getDisplayName(),
        ReverseRouter.route(on(UpcomingTendersController.class).removeUpcomingTenderConfirm(
            projectId,
            upcomingTender.getId(),
            displayOrder,
            null
        ))
    );

    tender.setSummaryLinks(List.of(editLink, removeLink));

    tender.setUploadedFileViews(uploadedFileViews);

    return tender;
  }

  public static UpcomingTenderView createUpComingTenderView(
      UpcomingTender upcomingTender,
      Integer displayOrder,
      List<UploadedFileView> uploadedFileViews,
      Boolean isValid) {
    var view = createUpComingTenderView(upcomingTender, displayOrder, uploadedFileViews);
    view.setIsValid(isValid);
    return view;
  }
}
