package uk.co.ogauthority.pathfinder.model.entity.project.location;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkField;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldType;
import uk.co.ogauthority.pathfinder.service.entityduplication.ParentEntity;

@Entity
@Table(name = "project_locations")
public class ProjectLocation extends ProjectDetailEntity implements ParentEntity {

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
