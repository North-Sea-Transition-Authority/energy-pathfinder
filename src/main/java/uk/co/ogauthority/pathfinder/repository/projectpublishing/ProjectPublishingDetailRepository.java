package uk.co.ogauthority.pathfinder.repository.projectpublishing;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.projectpublishing.ProjectPublishingDetail;

@Repository
public interface ProjectPublishingDetailRepository extends CrudRepository<ProjectPublishingDetail, Integer> {
}
