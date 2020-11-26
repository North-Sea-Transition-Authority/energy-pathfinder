package uk.co.ogauthority.pathfinder.model.entity.projectpublishing;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;

@Entity
@Table(name = "project_publishing_details")
public class ProjectPublishingDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "project_detail_id")
  private ProjectDetail projectDetail;

  @Column(name = "published_datetime")
  private Instant publishedInstant;

  private Integer publisherWuaId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public ProjectDetail getProjectDetail() {
    return projectDetail;
  }

  public void setProjectDetail(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  public Instant getPublishedInstant() {
    return publishedInstant;
  }

  public void setPublishedInstant(Instant publishedInstant) {
    this.publishedInstant = publishedInstant;
  }

  public Integer getPublisherWuaId() {
    return publisherWuaId;
  }

  public void setPublisherWuaId(Integer publishedByWuaId) {
    this.publisherWuaId = publishedByWuaId;
  }
}
