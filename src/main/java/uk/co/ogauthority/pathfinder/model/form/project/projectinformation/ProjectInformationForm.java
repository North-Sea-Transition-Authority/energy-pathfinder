package uk.co.ogauthority.pathfinder.model.form.project.projectinformation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStageSubCategory;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.PartialValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring.LengthRestrictedString;

public class ProjectInformationForm {

  @LengthRestrictedString(messagePrefix = "The project title", groups = {FullValidation.class, PartialValidation.class})
  @NotEmpty(message = "Enter a project title", groups = FullValidation.class)
  private String projectTitle;

  @NotEmpty(message = "Provide a summary of the project", groups = FullValidation.class)
  private String projectSummary;

  @NotNull(message = "Select an energy project", groups = FullValidation.class)
  private FieldStage fieldStage;

  private QuarterYearInput developmentFirstProductionDate;

  private FieldStageSubCategory carbonCaptureSubCategory;

  private FieldStageSubCategory electrificationSubCategory;

  private FieldStageSubCategory hydrogenSubCategory;

  private FieldStageSubCategory oilAndGasSubCategory;

  private FieldStageSubCategory windEnergySubCategory;

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

  public QuarterYearInput getDevelopmentFirstProductionDate() {
    return developmentFirstProductionDate;
  }

  public void setDevelopmentFirstProductionDate(QuarterYearInput developmentFirstProductionDate) {
    this.developmentFirstProductionDate = developmentFirstProductionDate;
  }

  public FieldStageSubCategory getCarbonCaptureSubCategory() {
    return carbonCaptureSubCategory;
  }

  public void setCarbonCaptureSubCategory(FieldStageSubCategory carbonCaptureSubCategory) {
    this.carbonCaptureSubCategory = carbonCaptureSubCategory;
  }

  public FieldStageSubCategory getElectrificationSubCategory() {
    return electrificationSubCategory;
  }

  public void setElectrificationSubCategory(FieldStageSubCategory electrificationSubCategory) {
    this.electrificationSubCategory = electrificationSubCategory;
  }

  public FieldStageSubCategory getHydrogenSubCategory() {
    return hydrogenSubCategory;
  }

  public void setHydrogenSubCategory(FieldStageSubCategory hydrogenSubCategory) {
    this.hydrogenSubCategory = hydrogenSubCategory;
  }

  public FieldStageSubCategory getOilAndGasSubCategory() {
    return oilAndGasSubCategory;
  }

  public void setOilAndGasSubCategory(FieldStageSubCategory oilAndGasSubCategory) {
    this.oilAndGasSubCategory = oilAndGasSubCategory;
  }

  public FieldStageSubCategory getWindEnergySubCategory() {
    return windEnergySubCategory;
  }

  public void setWindEnergySubCategory(FieldStageSubCategory windEnergySubCategory) {
    this.windEnergySubCategory = windEnergySubCategory;
  }
}
