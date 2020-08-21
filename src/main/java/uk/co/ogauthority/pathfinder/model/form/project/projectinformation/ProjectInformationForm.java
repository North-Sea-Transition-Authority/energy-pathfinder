package uk.co.ogauthority.pathfinder.model.form.project.projectinformation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.PartialValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.email.ValidEmail;
import uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring.LengthRestrictedString;
import uk.co.ogauthority.pathfinder.model.form.validation.phonenumber.ValidPhoneNumber;

public class ProjectInformationForm {

  @NotNull(message = "Select a field stage", groups = FullValidation.class)
  private FieldStage fieldStage;

  @LengthRestrictedString(messagePrefix = "The project title", groups = {FullValidation.class, PartialValidation.class})
  @NotEmpty(message = "Enter a project title", groups = FullValidation.class)
  private String projectTitle;

  @NotEmpty(message = "Provide a summary of the project", groups = FullValidation.class)
  private String projectSummary;

  @LengthRestrictedString(messagePrefix = "The contact name", groups = {FullValidation.class, PartialValidation.class})
  @NotEmpty(message = "Enter a contact name", groups = FullValidation.class)
  private String name;

  @NotEmpty(message = "Enter a telephone number", groups = FullValidation.class)
  @ValidPhoneNumber(messagePrefix = "The contact phone number", groups = {FullValidation.class, PartialValidation.class})
  private String phoneNumber;

  @LengthRestrictedString(messagePrefix = "The contact job title", groups = {FullValidation.class, PartialValidation.class})
  @NotEmpty(message = "Enter a job title", groups = FullValidation.class)
  private String jobTitle;

  @NotEmpty(message = "Enter an email address", groups = FullValidation.class)
  @ValidEmail(messagePrefix = "The contact email", groups = {FullValidation.class, PartialValidation.class})
  private String emailAddress;

  public ProjectInformationForm() {
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
