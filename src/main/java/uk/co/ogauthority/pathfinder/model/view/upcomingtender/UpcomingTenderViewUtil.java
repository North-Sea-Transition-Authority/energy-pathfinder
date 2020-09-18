package uk.co.ogauthority.pathfinder.model.view.upcomingtender;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import uk.co.ogauthority.pathfinder.controller.project.upcomingtender.UpcomingTendersController;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class UpcomingTenderViewUtil {

  public UpcomingTenderViewUtil() {
    throw new IllegalStateException("UpcomingTenderViewFactory is a utility class and should not be instantiated");
  }

  public static UpcomingTenderView createUpComingTenderView(UpcomingTender upcomingTender, Integer displayOrder) {
    var projectId = upcomingTender.getProjectDetail().getProject().getId();
    var tender = new UpcomingTenderView(
            displayOrder,
            upcomingTender.getId(),
            projectId
        );

    tender.setTenderFunction(
        upcomingTender.getTenderFunction() != null
          ? upcomingTender.getTenderFunction().getDisplayName()
          : upcomingTender.getManualTenderFunction()
    );
    tender.setDescriptionOfWork(upcomingTender.getDescriptionOfWork());
    tender.setEstimatedTenderDate(DateUtil.formatDate(upcomingTender.getEstimatedTenderDate()));
    tender.setContractBand(
        upcomingTender.getContractBand() != null
          ? upcomingTender.getContractBand().getDisplayName()
          : null
    );
    tender.setContactName(upcomingTender.getContactName());
    tender.setPhoneNumber(upcomingTender.getPhoneNumber());
    tender.setJobTitle(upcomingTender.getJobTitle());
    tender.setEmailAddress(upcomingTender.getEmailAddress());
    tender.setEditLink(
        new SummaryLink(
            SummaryLinkText.EDIT.getDisplayName(),
            ReverseRouter.route(on(UpcomingTendersController.class).editUpcomingTender(
                projectId,
                upcomingTender.getId(),
                null
            ))
        )
    );

    tender.setDeleteLink(
        new SummaryLink(
            SummaryLinkText.DELETE.getDisplayName(),
            ReverseRouter.route(on(UpcomingTendersController.class).deleteUpcomingTenderConfirm(
                projectId,
                upcomingTender.getId(),
                displayOrder,
                null
            ))
        )
    );

    return tender;
  }

  public static UpcomingTenderView createUpComingTenderView(
      UpcomingTender upcomingTender,
      Integer displayOrder,
      Boolean isValid) {
    var view = createUpComingTenderView(upcomingTender, displayOrder);
    view.setIsValid(isValid);
    return view;
  }
}
