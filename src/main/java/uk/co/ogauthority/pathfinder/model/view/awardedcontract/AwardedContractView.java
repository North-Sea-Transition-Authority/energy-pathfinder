package uk.co.ogauthority.pathfinder.model.view.awardedcontract;

import java.util.List;
import uk.co.ogauthority.pathfinder.model.view.ProjectSummaryItem;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.contactdetail.ContactDetailView;

public class AwardedContractView extends ProjectSummaryItem {

  private String contractorName;

  private String contractFunction;

  private String descriptionOfWork;

  private String dateAwarded;

  private String contractBand;

  private ContactDetailView contactDetailView;

  List<SummaryLink> summaryLinks;

  public String getContractorName() {
    return contractorName;
  }

  public void setContractorName(String contractorName) {
    this.contractorName = contractorName;
  }

  public String getContractFunction() {
    return contractFunction;
  }

  public void setContractFunction(String contractFunction) {
    this.contractFunction = contractFunction;
  }

  public String getDescriptionOfWork() {
    return descriptionOfWork;
  }

  public void setDescriptionOfWork(String descriptionOfWork) {
    this.descriptionOfWork = descriptionOfWork;
  }

  public String getDateAwarded() {
    return dateAwarded;
  }

  public void setDateAwarded(String dateAwarded) {
    this.dateAwarded = dateAwarded;
  }

  public String getContractBand() {
    return contractBand;
  }

  public void setContractBand(String contractBand) {
    this.contractBand = contractBand;
  }

  public ContactDetailView getContactDetailView() {
    return contactDetailView;
  }

  public void setContactDetailView(ContactDetailView contactDetailView) {
    this.contactDetailView = contactDetailView;
  }

  public List<SummaryLink> getSummaryLinks() {
    return summaryLinks;
  }

  public void setSummaryLinks(List<SummaryLink> summaryLinks) {
    this.summaryLinks = summaryLinks;
  }

}
