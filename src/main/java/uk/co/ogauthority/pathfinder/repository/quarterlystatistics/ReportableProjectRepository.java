package uk.co.ogauthority.pathfinder.repository.quarterlystatistics;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.quarterlystatistics.ReportableProject;

@Repository
public interface ReportableProjectRepository extends CrudRepository<ReportableProject, Integer> {

  List<ReportableProject> findAll();

}
