package uk.co.ogauthority.pathfinder.repository.project.campaigninformation;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignInformation;

@Repository
public interface CampaignInformationRepository extends CrudRepository<CampaignInformation, Integer> {

  Optional<CampaignInformation> findByProjectDetail(ProjectDetail projectDetail);

  Optional<CampaignInformation> findByProjectDetail_ProjectAndProjectDetail_Version(Project project, int version);

  void deleteByProjectDetail(ProjectDetail projectDetail);

}