package uk.co.ogauthority.pathfinder.model.view.awardedcontract;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.AwardedContractController;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.AwardedContract;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.contactdetail.ContactDetailViewUtil;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class AwardedContractViewUtil {

  private AwardedContractViewUtil() {
    throw new IllegalStateException("AwardedContractViewUtil is a util class and should not be instantiated");
  }

  public static AwardedContractView from(AwardedContract awardedContract, Integer displayOrder) {
    return from(awardedContract, displayOrder, true);
  }

  public static AwardedContractView from(AwardedContract awardedContract, Integer displayOrder, boolean isValid) {
    var awardedContractView = new AwardedContractView();
    awardedContractView.setDisplayOrder(displayOrder);
    awardedContractView.setId(awardedContract.getId());

    var projectId = awardedContract.getProjectDetail().getProject().getId();
    awardedContractView.setProjectId(projectId);
    awardedContractView.setContractorName(awardedContract.getContractorName());

    var contractFunction = (awardedContract.getContractFunction() != null)
        ? awardedContract.getContractFunction().getDisplayName()
        : awardedContract.getManualContractFunction();
    awardedContractView.setContractFunction(contractFunction);

    awardedContractView.setDescriptionOfWork(awardedContract.getDescriptionOfWork());
    awardedContractView.setDateAwarded(DateUtil.formatDate(awardedContract.getDateAwarded()));

    var contractBand = (awardedContract.getContractBand() != null)
        ? awardedContract.getContractBand().getDisplayName()
        : null;
    awardedContractView.setContractBand(contractBand);

    var contactDetailView = ContactDetailViewUtil.from(awardedContract);
    awardedContractView.setContactDetailView(contactDetailView);

    var summaryLinks = new ArrayList<SummaryLink>();
    summaryLinks.add(getEditLink(projectId, awardedContract.getId()));
    summaryLinks.add(getDeleteLink(projectId, awardedContract.getId(), displayOrder));

    awardedContractView.setSummaryLinks(summaryLinks);

    awardedContractView.setValid(isValid);

    return awardedContractView;
  }

  public static SummaryLink getEditLink(Integer projectId, Integer awardedContractId) {
    return new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        ReverseRouter.route(on(AwardedContractController.class).getAwardedContract(
            projectId,
            awardedContractId,
            null
        ))
    );
  }

  public static SummaryLink getDeleteLink(Integer projectId, Integer awardedContractId, Integer displayOrder) {
    return new SummaryLink(
        SummaryLinkText.DELETE.getDisplayName(),
        ReverseRouter.route(on(AwardedContractController.class).removeAwardedContract(
            projectId,
            awardedContractId,
            displayOrder,
            null
        ))
    );
  }
}
