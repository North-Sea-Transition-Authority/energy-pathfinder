package uk.co.ogauthority.pathfinder.model.entity.projectarchive;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;

@Entity
@Table(name = "project_archive_details")
public class ProjectArchiveDetail extends ProjectDetailEntity {

  @Lob
  @Column(name = "archive_reason", columnDefinition = "CLOB")
  private String archiveReason;

  public String getArchiveReason() {
    return archiveReason;
  }

  public void setArchiveReason(String archiveReason) {
    this.archiveReason = archiveReason;
  }
}
