package uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;

@Entity
@Table(name = "upcoming_tender_file_links")
public class UpcomingTenderFileLink {

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

  public ProjectDetailFile getProjectDetailFile() {
    return projectDetailFile;
  }

  public void setProjectDetailFile(ProjectDetailFile projectDetailFile) {
    this.projectDetailFile = projectDetailFile;
  }
}
