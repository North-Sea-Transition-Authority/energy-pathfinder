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

  @Query("SELECT pd " +
         "FROM ProjectDetail pd " +
         "WHERE pd.version = ( " +
         "  SELECT MAX(detail.version) " +
         "  FROM ProjectDetail detail " +
         "  WHERE detail.status IN(" +
         "    uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus.QA" +
         "  , uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus.PUBLISHED" +
         "  , uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus.ARCHIVED" +
         "  ) " +
         "  AND detail.project.id = pd.project.id " +
         ") " +
         "AND pd.project.id = :projectId"
  )
  Optional<ProjectDetail> findByProjectIdAndIsLatestSubmittedVersion(@Param("projectId") Integer projectId);

  @Query("SELECT CASE WHEN COUNT(pd) > 0 THEN true ELSE false END " +
         "FROM ProjectDetail pd " +
         "WHERE pd.project.id = :projectId " +
         "AND pd.isCurrentVersion = true " +
         "AND pd.version > 1 " +
         "AND pd.status = uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus.DRAFT")
  boolean isProjectUpdateInProgress(@Param("projectId") Integer projectId);

  @Query("SELECT new uk.co.ogauthority.pathfinder.model.dto.project.ProjectVersionDto(" +
         "  pd.version" +
         ", pd.submittedInstant" +
         ") " +
         "FROM ProjectDetail pd " +
         "WHERE pd.project.id = :projectId " +
         "ORDER BY pd.version DESC ")
  List<ProjectVersionDto> getProjectVersionDtos(@Param("projectId") Integer projectId);
}
