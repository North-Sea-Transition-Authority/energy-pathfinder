package uk.co.ogauthority.pathfinder.model.view.collaborationopportunity;

import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.util.summary.SummaryItem;

public class CollaborationOpportunityView implements SummaryItem {

  private Integer displayOrder;

  private Integer id;

  private Integer projectId;

  private String function;

  private String descriptionOfWork;

  private String estimatedServiceDate;

  private String contactName;

  private String phoneNumber;

  private String jobTitle;

  private String emailAddress;

  private SummaryLink editLink;

  private SummaryLink deleteLink;

  private Boolean isValid;

  public CollaborationOpportunityView(Integer displayOrder,
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

  public String getFunction() {
    return function;
  }

  public void setFunction(String function) {
    this.function = function;
  }

  public String getDescriptionOfWork() {
    return descriptionOfWork;
  }

  public void setDescriptionOfWork(String descriptionOfWork) {
    this.descriptionOfWork = descriptionOfWork;
  }

  public String getEstimatedServiceDate() {
    return estimatedServiceDate;
  }

  public void setEstimatedServiceDate(String estimatedServiceDate) {
    this.estimatedServiceDate = estimatedServiceDate;
  }

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
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

  public void setIsValid(Boolean valid) {
    isValid = valid;
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
