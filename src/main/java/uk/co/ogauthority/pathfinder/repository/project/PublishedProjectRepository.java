package uk.co.ogauthority.pathfinder.repository.project;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.PublishedProject;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

@Repository
public interface PublishedProjectRepository extends CrudRepository<PublishedProject, Integer> {

  List<PublishedProject> findAllByProjectDisplayNameContainingIgnoreCaseAndProjectTypeOrderByProjectDisplayName(
      String projectDisplayName,
      ProjectType projectType
  );

  List<PublishedProject> findAllByProjectIdIn(List<Integer> publishedProjectIds);
}
