package uk.co.ogauthority.pathfinder.model.view.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;

public class ProjectLocationView {

  private StringWithTag field;

  private String fieldType;

  private Integer waterDepth;

  private Boolean approvedFieldDevelopmentPlan;

  private String approvedFdpDate;

  private Boolean approvedDecomProgram;

  private String approvedDecomProgramDate;

  private String ukcsArea;

  private List<String> licenceBlocks = new ArrayList<>();

  public StringWithTag getField() {
    return field;
  }

  public void setField(StringWithTag field) {
    this.field = field;
  }

  public String getFieldType() {
    return fieldType;
  }

  public void setFieldType(String fieldType) {
    this.fieldType = fieldType;
  }

  public Integer getWaterDepth() {
    return waterDepth;
  }

  public void setWaterDepth(Integer waterDepth) {
    this.waterDepth = waterDepth;
  }

  public Boolean getApprovedFieldDevelopmentPlan() {
    return approvedFieldDevelopmentPlan;
  }

  public void setApprovedFieldDevelopmentPlan(Boolean approvedFieldDevelopmentPlan) {
    this.approvedFieldDevelopmentPlan = approvedFieldDevelopmentPlan;
  }

  public String getApprovedFdpDate() {
    return approvedFdpDate;
  }

  public void setApprovedFdpDate(String approvedFdpDate) {
    this.approvedFdpDate = approvedFdpDate;
  }

  public Boolean getApprovedDecomProgram() {
    return approvedDecomProgram;
  }

  public void setApprovedDecomProgram(Boolean approvedDecomProgram) {
    this.approvedDecomProgram = approvedDecomProgram;
  }

  public String getApprovedDecomProgramDate() {
    return approvedDecomProgramDate;
  }

  public void setApprovedDecomProgramDate(String approvedDecomProgramDate) {
    this.approvedDecomProgramDate = approvedDecomProgramDate;
  }

  public String getUkcsArea() {
    return ukcsArea;
  }

  public void setUkcsArea(String ukcsArea) {
    this.ukcsArea = ukcsArea;
  }

  public List<String> getLicenceBlocks() {
    return licenceBlocks;
  }

  public void setLicenceBlocks(List<String> licenceBlocks) {
    this.licenceBlocks = licenceBlocks;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProjectLocationView that = (ProjectLocationView) o;
    return Objects.equals(field, that.field)
        && Objects.equals(fieldType, that.fieldType)
        && Objects.equals(waterDepth, that.waterDepth)
        && Objects.equals(approvedFieldDevelopmentPlan, that.approvedFieldDevelopmentPlan)
        && Objects.equals(approvedFdpDate, that.approvedFdpDate)
        && Objects.equals(approvedDecomProgram, that.approvedDecomProgram)
        && Objects.equals(approvedDecomProgramDate, that.approvedDecomProgramDate)
        && Objects.equals(ukcsArea, that.ukcsArea)
        && Objects.equals(licenceBlocks, that.licenceBlocks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        field,
        fieldType,
        waterDepth,
        approvedFieldDevelopmentPlan,
        approvedFdpDate,
        approvedDecomProgram,
        approvedDecomProgramDate, ukcsArea, licenceBlocks
    );
  }
}
