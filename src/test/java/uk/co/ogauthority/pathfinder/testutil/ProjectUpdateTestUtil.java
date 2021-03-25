package uk.co.ogauthority.pathfinder.testutil;

import java.time.Instant;
import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.NoUpdateNotification;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.ProjectUpdate;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.model.enums.projectupdate.ProjectUpdateType;

public class ProjectUpdateTestUtil {

  private static final ProjectUpdateType UPDATE_TYPE = ProjectUpdateType.OPERATOR_INITIATED;

  private static final String NO_UPDATE_NOTIFICATION_SUPPLY_CHAIN_REASON = "Supply chain reason";
  private static final String NO_UPDATE_NOTIFICATION_REGULATOR_REASON = "Regulator reason";

  private static final String REGULATOR_UPDATE_REQUEST_UPDATE_REASON = "Update reason";
  private static final LocalDate REGULATOR_UPDATE_REQUEST_DEADLINE_DATE = LocalDate.now().plusMonths(1L);
  private static final Integer REGULATOR_UPDATE_REQUEST_REQUESTED_BY_WUA_ID = 1;

  private ProjectUpdateTestUtil() {
    throw new IllegalStateException("ProjectUpdateTestUtil is a utility class and should not be instantiated");
  }

  public static ProjectUpdate createProjectUpdate() {
    var projectUpdate = new ProjectUpdate();
    projectUpdate.setFromDetail(ProjectUtil.getProjectDetails());
    projectUpdate.setToDetail(ProjectUtil.getProjectDetails());
    projectUpdate.setUpdateType(UPDATE_TYPE);
    return projectUpdate;
  }

  public static NoUpdateNotification createNoUpdateNotification() {
    var noUpdateNotification = new NoUpdateNotification();
    noUpdateNotification.setProjectUpdate(createProjectUpdate());
    noUpdateNotification.setSupplyChainReason(NO_UPDATE_NOTIFICATION_SUPPLY_CHAIN_REASON);
    noUpdateNotification.setRegulatorReason(NO_UPDATE_NOTIFICATION_REGULATOR_REASON);
    return noUpdateNotification;
  }

  public static RegulatorUpdateRequest createRegulatorUpdateRequest() {
    var regulatorUpdateRequest = new RegulatorUpdateRequest();
    regulatorUpdateRequest.setProjectDetail(ProjectUtil.getProjectDetails());
    regulatorUpdateRequest.setUpdateReason(REGULATOR_UPDATE_REQUEST_UPDATE_REASON);
    regulatorUpdateRequest.setDeadlineDate(REGULATOR_UPDATE_REQUEST_DEADLINE_DATE);
    regulatorUpdateRequest.setRequestedByWuaId(REGULATOR_UPDATE_REQUEST_REQUESTED_BY_WUA_ID);
    regulatorUpdateRequest.setRequestedInstant(Instant.now());
    return regulatorUpdateRequest;
  }
}
