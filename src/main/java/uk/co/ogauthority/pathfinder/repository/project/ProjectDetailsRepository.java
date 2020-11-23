package uk.co.ogauthority.pathfinder.repository.project;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.dto.project.ProjectVersionDto;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;

@Repository
public interface ProjectDetailsRepository extends CrudRepository<ProjectDetail, Integer> {

  Optional<ProjectDetail> findByProjectIdAndIsCurrentVersionIsTrue(Integer projectId);

  Optional<ProjectDetail> findByProjectIdAndVersion(Integer projectId, Integer version);

  @Query("SELECT new uk.co.ogauthority.pathfinder.model.dto.project.ProjectVersionDto(" +
         "  pd.version" +
         ", pd.submittedInstant" +
         ") " +
         "FROM ProjectDetail pd " +
         "WHERE pd.project.id = :projectId " +
         "ORDER BY pd.version DESC ")
  List<ProjectVersionDto> getProjectVersionDtos(@Param("projectId") Integer projectId);
}
