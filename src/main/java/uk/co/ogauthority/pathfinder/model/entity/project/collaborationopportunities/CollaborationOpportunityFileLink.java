package uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.service.entityduplication.ChildEntity;
import uk.co.ogauthority.pathfinder.service.file.FileLinkEntity;

@Entity
@Table(name = "collaboration_op_file_links")
public class CollaborationOpportunityFileLink
    implements FileLinkEntity, ChildEntity<Integer, CollaborationOpportunity> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "opportunity_id")
  private CollaborationOpportunity collaborationOpportunity;

  @OneToOne
  @JoinColumn(name = "project_detail_file_id")
  private ProjectDetailFile projectDetailFile;

  public CollaborationOpportunity getCollaborationOpportunity() {
    return collaborationOpportunity;
  }

  public void setCollaborationOpportunity(
      CollaborationOpportunity collaborationOpportunity) {
    this.collaborationOpportunity = collaborationOpportunity;
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
    return id;
  }

  @Override
  public void clearId() {
    this.id = null;
  }

  @Override
  public void setParent(CollaborationOpportunity parentEntity) {
    setCollaborationOpportunity(parentEntity);
  }

  @Override
  public CollaborationOpportunity getParent() {
    return getCollaborationOpportunity();
  }
}
