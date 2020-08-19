package uk.co.ogauthority.pathfinder.model.entity.project.location;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkField;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldType;

@Entity
@Table(name = "project_locations")
public class ProjectLocation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @OneToOne
  @JoinColumn(name = "project_details_id")
  private ProjectDetail projectDetail;

  @ManyToOne
  @JoinColumn(name = "field_id")
  private DevUkField field;

  private String manualFieldName;

  @Enumerated(EnumType.STRING)
  private FieldType fieldType;

  private Integer waterDepth;

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

  public ProjectLocation(ProjectDetail projectDetail, String manualFieldName) {
    this.projectDetail = projectDetail;
    this.manualFieldName = manualFieldName;
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

  public DevUkField getField() {
    return field;
  }

  public void setField(DevUkField fieldId) {
    this.field = fieldId;
  }

  public String getManualFieldName() {
    return manualFieldName;
  }

  public void setManualFieldName(String manualFieldName) {
    this.manualFieldName = manualFieldName;
  }

  public FieldType getFieldType() {
    return fieldType;
  }

  public void setFieldType(FieldType fieldType) {
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
