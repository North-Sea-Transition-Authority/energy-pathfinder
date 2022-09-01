package uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.repository.file.UploadedFileRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.ChildEntity;
import uk.co.ogauthority.pathfinder.service.file.FileLinkEntity;

@Entity
@Table(name = "upcoming_tender_file_links")
public class UpcomingTenderFileLink implements FileLinkEntity, ChildEntity<Integer, UpcomingTender> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "upcoming_tender_id")
  private UpcomingTender upcomingTender;

  @OneToOne
  @JoinColumn(name = "project_detail_file_id")
  private ProjectDetailFile projectDetailFile;

  public UpcomingTender getUpcomingTender() {
    return upcomingTender;
  }

  public void setUpcomingTender(UpcomingTender upcomingTender) {
    this.upcomingTender = upcomingTender;
  }

  public void setProjectDetailFile(ProjectDetailFile projectDetailFile) {
    this.projectDetailFile = projectDetailFile;
  }

  @Override
  public ProjectDetailFile getProjectDetailFile() {
    return projectDetailFile;
  }

  @Override
  public Integer getId() {
    return this.id;
  }

  @Override
  public void clearId() {
    this.id = null;
  }

  @Override
  public void setParent(UpcomingTender parentEntity) {
    setUpcomingTender(parentEntity);
  }

  @Override
  public UpcomingTender getParent() {
    return getUpcomingTender();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UploadedFileRepository)) {
      return false;
    }
    UpcomingTenderFileLink that = (UpcomingTenderFileLink) o;
    return Objects.equals(id, that.id)
        && Objects.equals(upcomingTender, that.upcomingTender)
        && Objects.equals(projectDetailFile, that.projectDetailFile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        upcomingTender,
        projectDetailFile
    );
  }
}
