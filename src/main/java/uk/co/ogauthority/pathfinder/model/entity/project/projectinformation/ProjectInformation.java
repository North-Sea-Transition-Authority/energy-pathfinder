package uk.co.ogauthority.pathfinder.model.entity.project.projectinformation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStageSubCategory;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailCapture;

@Entity
@Table(name = "project_information")
public class ProjectInformation extends ProjectDetailEntity implements ContactDetailCapture {

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
  private FieldStageSubCategory fieldStageSubCategory;

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

  public FieldStageSubCategory getFieldStageSubCategory() {
    return fieldStageSubCategory;
  }

  public void setFieldStageSubCategory(
      FieldStageSubCategory fieldStageSubCategory) {
    this.fieldStageSubCategory = fieldStageSubCategory;
  }
}
