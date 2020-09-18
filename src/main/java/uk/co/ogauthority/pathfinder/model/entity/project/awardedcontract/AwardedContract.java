package uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract;

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
@Table(name = "awarded_contracts")
public class AwardedContract implements ContactDetailCapture {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "project_details_id")
  private ProjectDetail projectDetail;

  private String contractorName;

  @Enumerated(EnumType.STRING)
  private Function contractFunction;

  private String manualContractFunction;

  @Lob
  @Column(name = "description_of_work", columnDefinition = "CLOB")
  private String descriptionOfWork;

  private LocalDate dateAwarded;

  @Enumerated(EnumType.STRING)
  private ContractBand contractBand;

  private String contactName;

  private String phoneNumber;

  private String jobTitle;

  private String emailAddress;

  public AwardedContract() {}

  public AwardedContract(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  public Integer getId() {
    return id;
  }

  public ProjectDetail getProjectDetail() {
    return projectDetail;
  }

  public void setProjectDetail(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  public String getContractorName() {
    return contractorName;
  }

  public void setContractorName(String contractorName) {
    this.contractorName = contractorName;
  }

  public Function getContractFunction() {
    return contractFunction;
  }

  public void setContractFunction(Function contractFunction) {
    this.contractFunction = contractFunction;
  }

  public String getManualContractFunction() {
    return manualContractFunction;
  }

  public void setManualContractFunction(String manualContractFunction) {
    this.manualContractFunction = manualContractFunction;
  }

  public String getDescriptionOfWork() {
    return descriptionOfWork;
  }

  public void setDescriptionOfWork(String descriptionOfWork) {
    this.descriptionOfWork = descriptionOfWork;
  }

  public LocalDate getDateAwarded() {
    return dateAwarded;
  }

  public void setDateAwarded(LocalDate dateAwarded) {
    this.dateAwarded = dateAwarded;
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

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  @Override
  public String getName() {
    return getContactName();
  }

  @Override
  public String getPhoneNumber() {
    return phoneNumber;
  }

  @Override
  public String getJobTitle() {
    return jobTitle;
  }

  @Override
  public String getEmailAddress() {
    return emailAddress;
  }
}
