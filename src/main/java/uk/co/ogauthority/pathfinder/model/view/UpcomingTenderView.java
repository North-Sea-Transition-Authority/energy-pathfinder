package uk.co.ogauthority.pathfinder.model.view;

public class UpcomingTenderView {

  private Integer displayOrder;

  private Integer id;

  private Integer projectId;

  private String tenderFunction;

  private String descriptionOfWork;

  private String estimatedTenderDate;

  private String contractBand;

  private String contactName;

  private String phoneNumber;

  private String jobTitle;

  private String emailAddress;

  //TODO add action links
  //Own class for actionLinks edit/delete?

  private Boolean isValid;


  public UpcomingTenderView(
      Integer displayOrder,
      Integer id,
      Integer projectId,
      String tenderFunction,
      String descriptionOfWork,
      String estimatedTenderDate,
      String contractBand,
      String contactName,
      String phoneNumber,
      String jobTitle,
      String emailAddress
  ) {
    this.displayOrder = displayOrder;
    this.id = id;
    this.projectId = projectId;
    this.tenderFunction = tenderFunction;
    this.descriptionOfWork = descriptionOfWork;
    this.estimatedTenderDate = estimatedTenderDate;
    this.contractBand = contractBand;
    this.contactName = contactName;
    this.phoneNumber = phoneNumber;
    this.jobTitle = jobTitle;
    this.emailAddress = emailAddress;
  }

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

  public Boolean isValid() {
    return isValid;
  }

  public void setIsValid(Boolean isValid) {
    this.isValid = isValid;
  }
}
