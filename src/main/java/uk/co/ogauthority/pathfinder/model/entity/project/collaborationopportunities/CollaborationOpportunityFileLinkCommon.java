package uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.service.file.FileLinkEntity;

@MappedSuperclass
public abstract class CollaborationOpportunityFileLinkCommon implements FileLinkEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @OneToOne
  @JoinColumn(name = "project_detail_file_id")
  private ProjectDetailFile projectDetailFile;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setProjectDetailFile(ProjectDetailFile projectDetailFile) {
    this.projectDetailFile = projectDetailFile;
  }

  @Override
  public ProjectDetailFile getProjectDetailFile() {
    return projectDetailFile;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CollaborationOpportunityFileLinkCommon)) {
      return false;
    }
    CollaborationOpportunityFileLinkCommon that = (CollaborationOpportunityFileLinkCommon) o;
    return Objects.equals(id, that.id)
        && Objects.equals(projectDetailFile, that.projectDetailFile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        projectDetailFile
    );
  }
}