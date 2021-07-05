package uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.PublishedProject;
import uk.co.ogauthority.pathfinder.service.entityduplication.ChildEntity;

@Entity
@Table(name = "campaign_projects")
public class CampaignProject implements ChildEntity<Integer, CampaignInformation> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "campaign_id")
  private CampaignInformation campaignInformation;

  @ManyToOne
  @JoinColumn(name = "project_id")
  private PublishedProject publishedProject;

  public Integer getId() {
    return id;
  }

  public CampaignInformation getCampaignInformation() {
    return campaignInformation;
  }

  public void setCampaignInformation(CampaignInformation campaignInformation) {
    this.campaignInformation = campaignInformation;
  }

  public PublishedProject getPublishedProject() {
    return publishedProject;
  }

  public void setPublishedProject(PublishedProject publishedProject) {
    this.publishedProject = publishedProject;
  }

  @Override
  public void clearId() {
    this.id = null;
  }

  @Override
  public void setParent(CampaignInformation parentEntity) {
    setCampaignInformation(parentEntity);
  }

  @Override
  public CampaignInformation getParent() {
    return getCampaignInformation();
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CampaignProject that = (CampaignProject) o;
    return Objects.equals(id, that.id)
        && Objects.equals(campaignInformation, that.campaignInformation)
        && Objects.equals(publishedProject, that.publishedProject);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        campaignInformation,
        publishedProject
    );
  }
}