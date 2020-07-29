package uk.co.ogauthority.pathfinder.model.entity.project.projectinformation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetails;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;

@Entity
@Table(name = "project_information")
public class ProjectInformation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "project_details_id")
  private ProjectDetails projectDetail;

  @Enumerated(EnumType.STRING)
  private FieldStage fieldStage;

  private String projectTitle;

  @Lob
  @Column(name = "project_summary", columnDefinition = "CLOB")
  private String projectSummary;

  public ProjectInformation() {
  }

  public ProjectInformation(ProjectDetails projectDetail,
                            FieldStage fieldStage,
                            String projectTitle,
                            String projectSummary) {
    this.projectDetail = projectDetail;
    this.fieldStage = fieldStage;
    this.projectTitle = projectTitle;
    this.projectSummary = projectSummary;
  }

  public Integer getId() {
    return id;
  }

  public ProjectDetails getProjectDetail() {
    return projectDetail;
  }

  public void setProjectDetail(ProjectDetails projectDetail) {
    this.projectDetail = projectDetail;
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
}
