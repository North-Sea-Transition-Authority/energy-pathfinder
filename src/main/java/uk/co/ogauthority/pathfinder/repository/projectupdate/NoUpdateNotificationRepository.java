package uk.co.ogauthority.pathfinder.repository.projectupdate;

import org.springframework.data.repository.CrudRepository;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.NoUpdateNotification;

public interface NoUpdateNotificationRepository extends CrudRepository<NoUpdateNotification, Integer> {
}
