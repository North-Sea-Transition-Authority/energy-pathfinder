package uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.entity.project.SelectableProject;
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
  private SelectableProject project;

  public Integer getId() {
    return id;
  }

  public CampaignInformation getCampaignInformation() {
    return campaignInformation;
  }

  public void setCampaignInformation(CampaignInformation campaignInformation) {
    this.campaignInformation = campaignInformation;
  }

  public SelectableProject getProject() {
    return project;
  }

  public void setProject(SelectableProject project) {
    this.project = project;
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
        && Objects.equals(project, that.project);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        campaignInformation,
        project
    );
  }
}