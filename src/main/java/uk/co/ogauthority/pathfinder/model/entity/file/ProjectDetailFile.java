package uk.co.ogauthority.pathfinder.model.entity.file;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;

@Entity
@Table(name = "project_detail_files")
public class ProjectDetailFile extends ProjectDetailEntity {

  @OneToOne
  @JoinColumn(name = "file_id")
  private UploadedFile uploadedFile;

  private String description;

  @Enumerated(EnumType.STRING)
  private ProjectDetailFilePurpose purpose;

  @Enumerated(EnumType.STRING)
  private FileLinkStatus fileLinkStatus;

  public ProjectDetailFile() {
  }

  public ProjectDetailFile(ProjectDetail projectDetail,
                           UploadedFile uploadedFile,
                           ProjectDetailFilePurpose projectDetailFilePurpose,
                           FileLinkStatus fileLinkStatus) {
    this.projectDetail = projectDetail;
    this.uploadedFile = uploadedFile;
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

  public UploadedFile getUploadedFile() {
    return uploadedFile;
  }

  public void setUploadedFile(UploadedFile uploadedFile) {
    this.uploadedFile = uploadedFile;
  }

  public String getFileId() {
    return uploadedFile.getFileId();
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
        && Objects.equals(uploadedFile != null ? uploadedFile.getFileId() : null,
            projectDetailFile.uploadedFile != null ? projectDetailFile.uploadedFile.getFileId() : null)
        && Objects.equals(description, projectDetailFile.description)
        && purpose == projectDetailFile.purpose
        && fileLinkStatus == projectDetailFile.fileLinkStatus;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        projectDetail,
        uploadedFile != null ? uploadedFile.getFileId() : null,
        description,
        purpose,
        fileLinkStatus
    );
  }

}
