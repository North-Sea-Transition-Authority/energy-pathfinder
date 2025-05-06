package uk.co.ogauthority.pathfinder.model.entity.project.location;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkField;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldType;
import uk.co.ogauthority.pathfinder.service.entityduplication.ParentEntity;

@Entity
@Table(name = "project_locations")
public class ProjectLocation extends ProjectDetailEntity implements ParentEntity {

  private Integer centreOfInterestLatitudeDegrees;

  private Integer centreOfInterestLatitudeMinutes;

  private Double centreOfInterestLatitudeSeconds;

  private String centreOfInterestLatitudeHemisphere;

  private Integer centreOfInterestLongitudeDegrees;

  private Integer centreOfInterestLongitudeMinutes;

  private Double centreOfInterestLongitudeSeconds;

  private String centreOfInterestLongitudeHemisphere;

  @ManyToOne
  @JoinColumn(name = "field_id")
  private DevUkField field;

  @Enumerated(EnumType.STRING)
  private FieldType fieldType;

  private Integer maximumWaterDepth;

  @Column(name = "approved_fdp")
  private Boolean approvedFieldDevelopmentPlan;

  private LocalDate approvedFdpDate;

  private Boolean approvedDecomProgram;

  private LocalDate approvedDecomProgramDate;

  public ProjectLocation() {
  }

  public ProjectLocation(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  public ProjectLocation(ProjectDetail projectDetail, DevUkField field) {
    this.projectDetail = projectDetail;
    this.field = field;
  }

  public Integer getCentreOfInterestLatitudeDegrees() {
    return centreOfInterestLatitudeDegrees;
  }

  public void setCentreOfInterestLatitudeDegrees(Integer centreOfInterestLatitudeDegrees) {
    this.centreOfInterestLatitudeDegrees = centreOfInterestLatitudeDegrees;
  }

  public Integer getCentreOfInterestLatitudeMinutes() {
    return centreOfInterestLatitudeMinutes;
  }

  public void setCentreOfInterestLatitudeMinutes(Integer centreOfInterestLatitudeMinutes) {
    this.centreOfInterestLatitudeMinutes = centreOfInterestLatitudeMinutes;
  }

  public Double getCentreOfInterestLatitudeSeconds() {
    return centreOfInterestLatitudeSeconds;
  }

  public void setCentreOfInterestLatitudeSeconds(Double centreOfInterestLatitudeSeconds) {
    this.centreOfInterestLatitudeSeconds = centreOfInterestLatitudeSeconds;
  }

  public String getCentreOfInterestLatitudeHemisphere() {
    return centreOfInterestLatitudeHemisphere;
  }

  public void setCentreOfInterestLatitudeHemisphere(String centreOfInterestLatitudeHemisphere) {
    this.centreOfInterestLatitudeHemisphere = centreOfInterestLatitudeHemisphere;
  }

  public Integer getCentreOfInterestLongitudeDegrees() {
    return centreOfInterestLongitudeDegrees;
  }

  public void setCentreOfInterestLongitudeDegrees(Integer centreOfInterestLongitudeDegrees) {
    this.centreOfInterestLongitudeDegrees = centreOfInterestLongitudeDegrees;
  }

  public Integer getCentreOfInterestLongitudeMinutes() {
    return centreOfInterestLongitudeMinutes;
  }

  public void setCentreOfInterestLongitudeMinutes(Integer centreOfInterestLongitudeMinutes) {
    this.centreOfInterestLongitudeMinutes = centreOfInterestLongitudeMinutes;
  }

  public Double getCentreOfInterestLongitudeSeconds() {
    return centreOfInterestLongitudeSeconds;
  }

  public void setCentreOfInterestLongitudeSeconds(Double centreOfInterestLongitudeSeconds) {
    this.centreOfInterestLongitudeSeconds = centreOfInterestLongitudeSeconds;
  }

  public String getCentreOfInterestLongitudeHemisphere() {
    return centreOfInterestLongitudeHemisphere;
  }

  public void setCentreOfInterestLongitudeHemisphere(String centreOfInterestLongitudeHemisphere) {
    this.centreOfInterestLongitudeHemisphere = centreOfInterestLongitudeHemisphere;
  }

  public DevUkField getField() {
    return field;
  }

  public void setField(DevUkField fieldId) {
    this.field = fieldId;
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

  public LocalDate getApprovedFdpDate() {
    return approvedFdpDate;
  }

  public void setApprovedFdpDate(LocalDate approvedFdpDate) {
    this.approvedFdpDate = approvedFdpDate;
  }

  public Boolean getApprovedDecomProgram() {
    return approvedDecomProgram;
  }

  public void setApprovedDecomProgram(Boolean approvedDecomProgram) {
    this.approvedDecomProgram = approvedDecomProgram;
  }

  public LocalDate getApprovedDecomProgramDate() {
    return approvedDecomProgramDate;
  }

  public void setApprovedDecomProgramDate(LocalDate approvedDecomProgramDate) {
    this.approvedDecomProgramDate = approvedDecomProgramDate;
  }
}
