package uk.co.ogauthority.pathfinder.repository.project.campaigninformation;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignInformation;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignProject;

@Repository
public interface CampaignProjectRepository extends CrudRepository<CampaignProject, Integer> {

  List<CampaignProject> findAllByCampaignInformation_ProjectDetail(ProjectDetail projectDetail);

  void deleteAllByCampaignInformation(CampaignInformation campaignInformation);

  void deleteAllByCampaignInformation_ProjectDetail(ProjectDetail projectDetail);
}
