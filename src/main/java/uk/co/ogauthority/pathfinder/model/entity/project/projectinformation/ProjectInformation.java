package uk.co.ogauthority.pathfinder.model.entity.project.projectinformation;

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
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailCapture;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.Quarter;

@Entity
@Table(name = "project_information")
public class ProjectInformation implements ContactDetailCapture {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "project_details_id")
  private ProjectDetail projectDetail;

  @Enumerated(EnumType.STRING)
  private FieldStage fieldStage;

  private String projectTitle;

  @Lob
  @Column(name = "project_summary", columnDefinition = "CLOB")
  private String projectSummary;

  private String contactName;

  private String phoneNumber;

  private String jobTitle;

  private String emailAddress;

  @Enumerated(EnumType.STRING)
  private Quarter firstProductionDateQuarter;

  private Integer firstProductionDateYear;

  @Enumerated(EnumType.STRING)
  private Quarter decomWorkStartDateQuarter;

  private Integer decomWorkStartDateYear;

  private LocalDate productionCessationDate;

  public Integer getId() {
    return id;
  }

  public ProjectDetail getProjectDetail() {
    return projectDetail;
  }

  public void setProjectDetail(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  public FieldStage getFieldStage() {
    return fieldStage;
  }

  public void setFieldStage(FieldStage fieldStage) {
    this.fieldStage = fieldStage;
  }

  public String getProjectTitle() {
    return projectTitle;
  }

  public void setProjectTitle(String projectTitle) {
    this.projectTitle = projectTitle;
  }

  public String getProjectSummary() {
    return projectSummary;
  }

  public void setProjectSummary(String projectSummary) {
    this.projectSummary = projectSummary;
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


  public Quarter getFirstProductionDateQuarter() {
    return firstProductionDateQuarter;
  }

  public void setFirstProductionDateQuarter(Quarter firstProductionDateQuarter) {
    this.firstProductionDateQuarter = firstProductionDateQuarter;
  }

  public Integer getFirstProductionDateYear() {
    return firstProductionDateYear;
  }

  public void setFirstProductionDateYear(Integer firstProductionDateYear) {
    this.firstProductionDateYear = firstProductionDateYear;
  }

  public Quarter getDecomWorkStartDateQuarter() {
    return decomWorkStartDateQuarter;
  }

  public void setDecomWorkStartDateQuarter(Quarter decomWorkStartDateQuarter) {
    this.decomWorkStartDateQuarter = decomWorkStartDateQuarter;
  }

  public Integer getDecomWorkStartDateYear() {
    return decomWorkStartDateYear;
  }

  public void setDecomWorkStartDateYear(Integer decomWorkStartDateYear) {
    this.decomWorkStartDateYear = decomWorkStartDateYear;
  }

  public LocalDate getProductionCessationDate() {
    return productionCessationDate;
  }

  public void setProductionCessationDate(LocalDate productionCessationDate) {
    this.productionCessationDate = productionCessationDate;
  }
}
