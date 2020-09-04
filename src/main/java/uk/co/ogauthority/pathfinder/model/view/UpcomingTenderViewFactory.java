package uk.co.ogauthority.pathfinder.model.view;

import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class UpcomingTenderViewFactory {

  public static UpcomingTenderView createUpComingTenderView(UpcomingTender upcomingTender, Integer displayOrder) {
    //TODO replace with getters / setters to avoid code smell
    return new UpcomingTenderView(
        displayOrder,
        upcomingTender.getId(),
        upcomingTender.getProjectDetail().getProject().getId(),
        upcomingTender.getTenderFunction() != null
            ? upcomingTender.getTenderFunction().getDisplayName()
            : SearchSelectorService.removePrefix(upcomingTender.getManualTenderFunction()),
        upcomingTender.getDescriptionOfWork(),
        DateUtil.formatDate(upcomingTender.getEstimatedTenderDate()),
        upcomingTender.getContractBand() != null
            ? upcomingTender.getContractBand().getDisplayName()
            : "",
        upcomingTender.getContactName(),
        upcomingTender.getPhoneNumber(),
        upcomingTender.getJobTitle(),
        upcomingTender.getEmailAddress()
    );
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
