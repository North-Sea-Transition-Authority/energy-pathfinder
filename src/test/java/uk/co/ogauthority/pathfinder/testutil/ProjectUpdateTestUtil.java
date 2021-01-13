package uk.co.ogauthority.pathfinder.testutil;

import java.time.Instant;
import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;

public class ProjectUpdateTestUtil {

  private static final String UPDATE_REASON = "Update reason";
  private static final LocalDate DEADLINE_DATE = LocalDate.now().plusMonths(1L);
  private static final Integer REQUESTED_BY_WUA_ID = 1;

  private ProjectUpdateTestUtil() {
    throw new IllegalStateException("ProjectUpdateTestUtil is a utility class and should not be instantiated");
  }

  public static RegulatorUpdateRequest createRegulatorUpdateRequest() {
    var regulatorUpdateRequest = new RegulatorUpdateRequest();
    regulatorUpdateRequest.setProjectDetail(ProjectUtil.getProjectDetails());
    regulatorUpdateRequest.setUpdateReason(UPDATE_REASON);
    regulatorUpdateRequest.setDeadlineDate(DEADLINE_DATE);
    regulatorUpdateRequest.setRequestedByWuaId(REQUESTED_BY_WUA_ID);
    regulatorUpdateRequest.setRequestedInstant(Instant.now());
    return regulatorUpdateRequest;
  }
}
