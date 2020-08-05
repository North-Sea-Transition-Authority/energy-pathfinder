package uk.co.ogauthority.pathfinder.model.entity.project.location;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkField;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;

@Entity
@Table(name = "project_locations")
public class ProjectLocation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "project_details_id")
  private ProjectDetail projectDetail;

  @ManyToOne
  @JoinColumn(name = "field_id")
  private DevUkField field;

  private String manualFieldName;

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
}
