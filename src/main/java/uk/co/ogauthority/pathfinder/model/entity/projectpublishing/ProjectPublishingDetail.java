package uk.co.ogauthority.pathfinder.model.entity.projectpublishing;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;

@Entity
@Table(name = "project_publishing_details")
public class ProjectPublishingDetail extends ProjectDetailEntity {

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
