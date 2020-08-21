package uk.co.ogauthority.pathfinder.model.entity.project.projectinformation;

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

@Entity
@Table(name = "project_information")
public class ProjectInformation {

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

  public ProjectInformation() {
  }

  public ProjectInformation(ProjectDetail projectDetail,
                            FieldStage fieldStage,
                            String projectTitle,
                            String projectSummary,
                            String contactName,
                            String phoneNumber,
                            String jobTitle,
                            String emailAddress) {
    this.projectDetail = projectDetail;
    this.fieldStage = fieldStage;
    this.projectTitle = projectTitle;
    this.projectSummary = projectSummary;
    this.contactName = contactName;
    this.phoneNumber = phoneNumber;
    this.jobTitle = jobTitle;
    this.emailAddress = emailAddress;
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
}
