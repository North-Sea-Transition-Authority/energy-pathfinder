package uk.co.ogauthority.pathfinder.model.form.project.projectinformation;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.EnergyTransitionCategory;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
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

  @NotNull(message = "Select a field stage", groups = FullValidation.class)
  private FieldStage fieldStage;

  private QuarterYearInput discoveryFirstProductionDate;

  private QuarterYearInput developmentFirstProductionDate;

  private QuarterYearInput decomWorkStartDate;

  private ThreeFieldDateInput productionCessationDate;

  private EnergyTransitionCategory energyTransitionCategory;

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

  public QuarterYearInput getDiscoveryFirstProductionDate() {
    return discoveryFirstProductionDate;
  }

  public void setDiscoveryFirstProductionDate(QuarterYearInput discoveryFirstProductionDate) {
    this.discoveryFirstProductionDate = discoveryFirstProductionDate;
  }

  public QuarterYearInput getDecomWorkStartDate() {
    return decomWorkStartDate;
  }

  public void setDecomWorkStartDate(QuarterYearInput decomWorkStartDate) {
    this.decomWorkStartDate = decomWorkStartDate;
  }

  public ThreeFieldDateInput getProductionCessationDate() {
    return productionCessationDate;
  }

  public void setProductionCessationDate(
      ThreeFieldDateInput productionCessationDate) {
    this.productionCessationDate = productionCessationDate;
  }

  public EnergyTransitionCategory getEnergyTransitionCategory() {
    return energyTransitionCategory;
  }

  public void setEnergyTransitionCategory(
      EnergyTransitionCategory energyTransitionCategory) {
    this.energyTransitionCategory = energyTransitionCategory;
  }

  public QuarterYearInput getDevelopmentFirstProductionDate() {
    return developmentFirstProductionDate;
  }

  public void setDevelopmentFirstProductionDate(QuarterYearInput developmentFirstProductionDate) {
    this.developmentFirstProductionDate = developmentFirstProductionDate;
  }
}
