package uk.co.ogauthority.pathfinder.model.entity.projectpublishing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;

@Entity
@Table(name = "project_publishing_details")
public class ProjectPublishingDetail extends ProjectDetailEntity {

  @Column(name = "published_datetime")
  private Instant publishedInstant;

  private Integer publisherWuaId;

  public void setId(Integer id) {
    this.id = id;
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
}
