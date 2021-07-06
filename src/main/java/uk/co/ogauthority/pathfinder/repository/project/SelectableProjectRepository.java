package uk.co.ogauthority.pathfinder.repository.project;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.SelectableProject;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

@Repository
public interface SelectableProjectRepository extends CrudRepository<SelectableProject, Integer> {

  @Query("SELECT sp " +
         "FROM SelectableProject sp " +
         "WHERE (" +
         "  LOWER(sp.projectDisplayName) LIKE LOWER(concat('%', :searchTerm, '%')) " +
         "  OR LOWER(sp.operatorGroupName) LIKE LOWER(concat('%', :searchTerm, '%')) " +
         ") " +
         "AND sp.projectType = :projectType " +
         "AND sp.isPublished = true")
  List<SelectableProject> findAllPublishedProjectsByProjectDisplayNameOrOperatorGroupNameContainingIgnoreCase(
      @Param("searchTerm") String searchTerm,
      @Param("projectType") ProjectType projectType
  );


  List<SelectableProject> findAllByProjectIdIn(List<Integer> publishedProjectIds);
}
