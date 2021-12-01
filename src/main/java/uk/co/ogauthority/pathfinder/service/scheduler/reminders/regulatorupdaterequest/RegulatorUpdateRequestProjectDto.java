package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;

public class RegulatorUpdateRequestProjectDto {

  private final RegulatorUpdateRequest regulatorUpdateRequest;

  private final ProjectOperator projectOperator;

  public RegulatorUpdateRequestProjectDto(RegulatorUpdateRequest regulatorUpdateRequest,
                                          ProjectOperator projectOperator) {
    this.regulatorUpdateRequest = regulatorUpdateRequest;
    this.projectOperator = projectOperator;
  }

  public RegulatorUpdateRequest getRegulatorUpdateRequest() {
    return regulatorUpdateRequest;
  }

  public ProjectOperator getProjectOperator() {
    return projectOperator;
  }
}
