package uk.co.ogauthority.pathfinder.repository.quarterlystatistics;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.quarterlystatistics.ReportableProject;

@Repository
public interface ReportableProjectRepository extends CrudRepository<ReportableProject, Integer> {

  List<ReportableProject> findAll();

  List<ReportableProject> findByLastUpdatedDatetimeBetween(Instant earliestUpdateDatetime,
                                                           Instant latestUpdateDatetime);

  @Query("SELECT rp " +
         "FROM ReportableProject rp " +
         "WHERE rp.lastUpdatedDatetime NOT BETWEEN :earliestUpdateDatetime AND :latestUpdateDatetime"
  )
  List<ReportableProject> findByLastUpdatedDatetimeNotBetween(@Param("earliestUpdateDatetime") Instant earliestUpdateDatetime,
                                                              @Param("latestUpdateDatetime") Instant latestUpdateDatetime);

}
