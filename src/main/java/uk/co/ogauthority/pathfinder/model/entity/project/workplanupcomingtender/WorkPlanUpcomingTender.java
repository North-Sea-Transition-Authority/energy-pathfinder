package uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailCapture;

@Entity
@Table(name = "work_plan_upcoming_tenders")
public class WorkPlanUpcomingTender
    extends ProjectDetailEntity implements ContactDetailCapture {

  @Enumerated
  private Function departmentType;

  private String manualDepartmentType;

  @Lob
  @Column(name = "description_of_work", columnDefinition = "CLOB")
  private String descriptionOfWork;

  private LocalDate estimatedTenderDate;

  private String contactName;

  private String phoneNumber;

  private String jobTitle;

  private String emailAddress;

  public WorkPlanUpcomingTender() {
  }

  public WorkPlanUpcomingTender(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  public Function getDepartmentType() {
    return departmentType;
  }

  public void setDepartmentType(Function departmentType) {
    this.departmentType = departmentType;
  }

  public String getManualDepartmentType() {
    return manualDepartmentType;
  }

  public void setManualDepartmentType(String manualDepartmentType) {
    this.manualDepartmentType = manualDepartmentType;
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
