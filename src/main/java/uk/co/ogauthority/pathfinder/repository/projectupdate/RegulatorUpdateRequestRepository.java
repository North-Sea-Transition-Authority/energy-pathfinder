package uk.co.ogauthority.pathfinder.repository.projectupdate;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateRequestProjectDto;

@Repository
public interface RegulatorUpdateRequestRepository extends CrudRepository<RegulatorUpdateRequest, Integer> {

  Optional<RegulatorUpdateRequest> findByProjectDetail(ProjectDetail projectDetail);

  boolean existsByProjectDetail(ProjectDetail projectDetail);

  Optional<RegulatorUpdateRequest> findByProjectDetail_projectAndProjectDetail_Version(Project project, Integer version);

  @Query("SELECT new uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateRequestProjectDto( " +
         "rur, po " +
         ") " +
         "FROM RegulatorUpdateRequest rur " +
         "JOIN ProjectDetail pd ON pd = rur.projectDetail " +
         "JOIN ProjectOperator po ON po.projectDetail = pd " +
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
         "AND rur.deadlineDate IS NOT NULL"
  )
  List<RegulatorUpdateRequestProjectDto> getAllProjectsWithOutstandingRegulatorUpdateRequestsWithDeadlines();
}
