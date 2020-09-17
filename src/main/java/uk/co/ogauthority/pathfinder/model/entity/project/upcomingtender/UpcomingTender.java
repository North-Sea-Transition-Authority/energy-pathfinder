package uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailCapture;

@Entity
@Table(name = "upcoming_tenders")
public class UpcomingTender implements ContactDetailCapture {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "project_details_id")
  private ProjectDetail projectDetail;

  @Enumerated(EnumType.STRING)
  private Function tenderFunction;

  private String manualTenderFunction;

  @Lob
  @Column(name = "description_of_work", columnDefinition = "CLOB")
  private String descriptionOfWork;

  private LocalDate estimatedTenderDate;

  @Enumerated(EnumType.STRING)
  private ContractBand contractBand;

  private String contactName;

  private String phoneNumber;

  private String jobTitle;

  private String emailAddress;

  public UpcomingTender() {
  }

  public Integer getId() {
    return id;
  }

  public UpcomingTender(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  public ProjectDetail getProjectDetail() {
    return projectDetail;
  }

  public void setProjectDetail(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  public Function getTenderFunction() {
    return tenderFunction;
  }

  public void setTenderFunction(Function tenderFunction) {
    this.tenderFunction = tenderFunction;
  }

  public String getManualTenderFunction() {
    return manualTenderFunction;
  }

  public void setManualTenderFunction(String manualTenderFunction) {
    this.manualTenderFunction = manualTenderFunction;
  }

  public String getDescriptionOfWork() {
    return descriptionOfWork;
  }

  public void setDescriptionOfWork(String descriptionOfWork) {
    this.descriptionOfWork = descriptionOfWork;
  }

  public LocalDate getEstimatedTenderDate() {
    return estimatedTenderDate;
  }

  public void setEstimatedTenderDate(LocalDate estimatedTenderDate) {
    this.estimatedTenderDate = estimatedTenderDate;
  }

  public ContractBand getContractBand() {
    return contractBand;
  }

  public void setContractBand(ContractBand contractBand) {
    this.contractBand = contractBand;
  }

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  @Override
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @Override
  public String getJobTitle() {
    return jobTitle;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  @Override
  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  @Override
  public String getName() {
    return getContactName();
  }
}
