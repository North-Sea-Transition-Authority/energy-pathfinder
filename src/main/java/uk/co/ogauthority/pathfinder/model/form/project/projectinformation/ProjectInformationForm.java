package uk.co.ogauthority.pathfinder.model.form.project.projectinformation;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.PartialValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring.LengthRestrictedString;

public class ProjectInformationForm {

  @NotNull(message = "Select a field stage", groups = FullValidation.class)
  private FieldStage fieldStage;

  @LengthRestrictedString(messagePrefix = "The project title", groups = {FullValidation.class, PartialValidation.class})
  @NotEmpty(message = "Enter a project title", groups = FullValidation.class)
  private String projectTitle;

  @NotEmpty(message = "Provide a summary of the project", groups = FullValidation.class)
  private String projectSummary;

  @Valid
  private ContactDetailForm contactDetail;

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

  public ContactDetailForm getContactDetail() {
    return contactDetail;
  }

  public void setContactDetail(ContactDetailForm contactDetail) {
    this.contactDetail = contactDetail;
  }
}
