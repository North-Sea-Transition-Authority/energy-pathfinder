package uk.co.ogauthority.pathfinder.repository.projectupdate;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.NoUpdateNotification;

@Repository
public interface NoUpdateNotificationRepository extends CrudRepository<NoUpdateNotification, Integer> {
}
