package uk.co.ogauthority.pathfinder.model.form.project.location;

import java.util.Collections;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldType;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.PartialValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.positivewholenumber.PositiveWholeNumber;

public class ProjectLocationForm {

  @NotEmpty(message = "Select a field", groups = FullValidation.class)
  private String field;

  @NotNull(message = "Select a field type", groups = FullValidation.class)
  private FieldType fieldType;

  @NotNull(message = "Enter the maximum water depth", groups = FullValidation.class)
  @PositiveWholeNumber(messagePrefix = "Maximum water depth", groups = {FullValidation.class, PartialValidation.class})
  private Integer maximumWaterDepth;

  @NotNull(message = "Select yes if you have an approved Field Development Plan", groups = FullValidation.class)
  private Boolean approvedFieldDevelopmentPlan;

  private ThreeFieldDateInput approvedFdpDate;

  @NotNull(message = "Select yes if you have an approved Decommissioning Programme", groups = FullValidation.class)
  private Boolean approvedDecomProgram;

  private ThreeFieldDateInput approvedDecomProgramDate;

  @NotEmpty(message = "Select at least one licence block", groups = FullValidation.class)
  private List<String> licenceBlocks = Collections.emptyList();

  //Just here for the add to list selector
  private String licenceBlocksSelect;

  public ProjectLocationForm() {
  }

  public ProjectLocationForm(String field) {
    this.field = field;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }


  public FieldType getFieldType() {
    return fieldType;
  }

  public void setFieldType(FieldType fieldType) {
    this.fieldType = fieldType;
  }

  public Integer getMaximumWaterDepth() {
    return maximumWaterDepth;
  }

  public void setMaximumWaterDepth(Integer maximumWaterDepth) {
    this.maximumWaterDepth = maximumWaterDepth;
  }

  public Boolean getApprovedFieldDevelopmentPlan() {
    return approvedFieldDevelopmentPlan;
  }

  public void setApprovedFieldDevelopmentPlan(Boolean approvedFieldDevelopmentPlan) {
    this.approvedFieldDevelopmentPlan = approvedFieldDevelopmentPlan;
  }

  public ThreeFieldDateInput getApprovedFdpDate() {
    return approvedFdpDate;
  }

  public void setApprovedFdpDate(ThreeFieldDateInput approvedFdpDate) {
    this.approvedFdpDate = approvedFdpDate;
  }

  public Boolean getApprovedDecomProgram() {
    return approvedDecomProgram;
  }

  public void setApprovedDecomProgram(Boolean approvedDecomProgram) {
    this.approvedDecomProgram = approvedDecomProgram;
  }

  public ThreeFieldDateInput getApprovedDecomProgramDate() {
    return approvedDecomProgramDate;
  }

  public void setApprovedDecomProgramDate(ThreeFieldDateInput approvedDecomProgramDate) {
    this.approvedDecomProgramDate = approvedDecomProgramDate;
  }

  public List<String> getLicenceBlocks() {
    return licenceBlocks;
  }

  public void setLicenceBlocks(List<String> licenceBlocks) {
    this.licenceBlocks = licenceBlocks;
  }

  public String getLicenceBlocksSelect() {
    return licenceBlocksSelect;
  }

  public void setLicenceBlocksSelect(String licenceBlocksSelect) {
    this.licenceBlocksSelect = licenceBlocksSelect;
  }
}
