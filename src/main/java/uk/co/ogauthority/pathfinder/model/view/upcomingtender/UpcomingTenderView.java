package uk.co.ogauthority.pathfinder.model.view.upcomingtender;

import uk.co.ogauthority.pathfinder.model.view.contactdetail.ContactDetailView;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.util.summary.SummaryItem;

public class UpcomingTenderView {

public class UpcomingTenderView implements SummaryItem {

  private Integer displayOrder;

  private Integer id;

  private Integer projectId;

  private String tenderFunction;

  private String descriptionOfWork;

  private String estimatedTenderDate;

  private String contractBand;

  private ContactDetailView contactDetailView;

  private SummaryLink editLink;

  private SummaryLink deleteLink;

  private Boolean isValid;


  public UpcomingTenderView(
      Integer displayOrder,
      Integer id,
      Integer projectId
  ) {
    this.displayOrder = displayOrder;
    this.id = id;
    this.projectId = projectId;
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

  public String getTenderFunction() {
    return tenderFunction;
  }

  public void setTenderFunction(String tenderFunction) {
    this.tenderFunction = tenderFunction;
  }

  public String getDescriptionOfWork() {
    return descriptionOfWork;
  }

  public void setDescriptionOfWork(String descriptionOfWork) {
    this.descriptionOfWork = descriptionOfWork;
  }

  public String getEstimatedTenderDate() {
    return estimatedTenderDate;
  }

  public void setEstimatedTenderDate(String estimatedTenderDate) {
    this.estimatedTenderDate = estimatedTenderDate;
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

  public void setIsValid(Boolean isValid) {
    this.isValid = isValid;
  }

  public SummaryLink getEditLink() {
    return editLink;
  }

  public void setEditLink(SummaryLink editLink) {
    this.editLink = editLink;
  }

  public SummaryLink getDeleteLink() {
    return deleteLink;
  }

  public void setDeleteLink(SummaryLink deleteLink) {
    this.deleteLink = deleteLink;
  }

  @Override
  public Integer getDisplayOrder() {
    return displayOrder;
  }

  @Override
  public Boolean isValid() {
    return isValid;
  }
}
