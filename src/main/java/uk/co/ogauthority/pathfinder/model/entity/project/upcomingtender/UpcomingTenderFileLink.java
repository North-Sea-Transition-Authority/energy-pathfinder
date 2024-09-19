package uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;
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
