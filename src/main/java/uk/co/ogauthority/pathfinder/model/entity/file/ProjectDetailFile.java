package uk.co.ogauthority.pathfinder.model.entity.file;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;

@Entity
@Table(name = "project_detail_files")
public class ProjectDetailFile extends ProjectDetailEntity {

  // not entity mapped to avoid selecting file data when not needed
  private String fileId;

  private String description;

  @Enumerated(EnumType.STRING)
  private ProjectDetailFilePurpose purpose;

  @Enumerated(EnumType.STRING)
  private FileLinkStatus fileLinkStatus;

  public ProjectDetailFile() {
  }

  public ProjectDetailFile(ProjectDetail projectDetail,
                           String fileId,
                           ProjectDetailFilePurpose projectDetailFilePurpose,
                           FileLinkStatus fileLinkStatus) {
    this.projectDetail = projectDetail;
    this.fileId = fileId;
    this.purpose = projectDetailFilePurpose;
    this.fileLinkStatus = fileLinkStatus;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Override
  public Integer getId() {
    return id;
  }

  @Override
  public ProjectDetail getProjectDetail() {
    return projectDetail;
  }

  @Override
  public void setProjectDetail(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ProjectDetailFilePurpose getPurpose() {
    return purpose;
  }

  public void setPurpose(ProjectDetailFilePurpose purpose) {
    this.purpose = purpose;
  }

  public FileLinkStatus getFileLinkStatus() {
    return fileLinkStatus;
  }

  public void setFileLinkStatus(FileLinkStatus fileLinkStatus) {
    this.fileLinkStatus = fileLinkStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProjectDetailFile projectDetailFile = (ProjectDetailFile) o;
    return Objects.equals(id, projectDetailFile.id)
        && Objects.equals(projectDetail, projectDetailFile.projectDetail)
        && Objects.equals(fileId, projectDetailFile.fileId)
        && Objects.equals(description, projectDetailFile.description)
        && purpose == projectDetailFile.purpose
        && fileLinkStatus == projectDetailFile.fileLinkStatus;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, projectDetail, fileId, description, purpose, fileLinkStatus);
  }

}
