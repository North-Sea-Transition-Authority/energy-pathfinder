package uk.co.ogauthority.pathfinder.model.view.awardedcontract;

import java.util.List;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.contactdetail.ContactDetailView;

public class AwardedContractView {

  private Integer displayOrder;

  private Integer id;

  private Integer projectId;

  private String contractorName;

  private String contractFunction;

  private String descriptionOfWork;

  private String dateAwarded;

  private String contractBand;

  private ContactDetailView contactDetailView;

  List<SummaryLink> summaryLinks;

  private boolean isValid;

  public Integer getDisplayOrder() {
    return displayOrder;
  }

  public void setDisplayOrder(Integer displayOrder) {
    this.displayOrder = displayOrder;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

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

  public boolean isValid() {
    return isValid;
  }

  public void setValid(boolean valid) {
    isValid = valid;
  }
}
