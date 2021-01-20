package uk.co.ogauthority.pathfinder.repository.projectupdate;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.NoUpdateNotification;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.ProjectUpdate;

@Repository
public interface NoUpdateNotificationRepository extends CrudRepository<NoUpdateNotification, Integer> {

  boolean existsByProjectUpdate(ProjectUpdate update);

  @Query("SELECT nun " +
         "FROM NoUpdateNotification nun " +
         "JOIN ProjectUpdate pu ON pu = nun.projectUpdate " +
         "WHERE pu.toDetail = :projectDetail")
  Optional<NoUpdateNotification> findByUpdateToDetail(ProjectDetail projectDetail);
}
