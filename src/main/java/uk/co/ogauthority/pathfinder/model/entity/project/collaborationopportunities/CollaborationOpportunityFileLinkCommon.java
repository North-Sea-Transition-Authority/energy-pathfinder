package uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities;

import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
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