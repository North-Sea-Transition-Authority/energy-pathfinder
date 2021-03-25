package uk.co.ogauthority.pathfinder.model.entity.projectarchive;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
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
