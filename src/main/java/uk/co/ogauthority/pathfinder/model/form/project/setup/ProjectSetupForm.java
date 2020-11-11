package uk.co.ogauthority.pathfinder.model.form.project.setup;

import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.TaskListSectionAnswer;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class ProjectSetupForm {

  @NotNull(message = "Select yes if you plan to add any upcoming tenders to your project", groups = FullValidation.class)
  private TaskListSectionAnswer upcomingTendersIncluded;

  @NotNull(message = "Select yes if you plan to add any awarded contracts to your project", groups = FullValidation.class)
  private TaskListSectionAnswer awardedContractsIncluded;

  @NotNull(message = "Select yes if you plan to add any collaboration opportunities to your project", groups = FullValidation.class)
  private TaskListSectionAnswer collaborationOpportunitiesIncluded;

  //TODO from here validation is custom
  //@NotNull(message = "Select yes if you plan to add any wells to be decommissioned to your project", groups = FullValidation.class)
  private TaskListSectionAnswer wellsIncluded;

//  @NotNull(message = "Select yes if you plan to add any platforms or FPSOs to be decommissioned to your project", groups = FullValidation.class)
  private TaskListSectionAnswer platformsFpsosIncluded;

//  @NotNull(message = "Select yes if you plan to add any subsea infrastructure to be decommissioned to your project", groups = FullValidation.class)
  private TaskListSectionAnswer subseaInfrastructureIncluded;

//  @NotNull(message = "Select yes if you plan to add any integrated rigs to be decommissioned to your project", groups = FullValidation.class)
  private TaskListSectionAnswer integratedRigsIncluded;

//  @NotNull(message = "Select yes if you plan to add any wells to be decommissioned to your project", groups = FullValidation.class)
  private TaskListSectionAnswer pipelinesIncluded;

  public ProjectSetupForm() {
  }

  public TaskListSectionAnswer getUpcomingTendersIncluded() {
    return upcomingTendersIncluded;
  }

  public void setUpcomingTendersIncluded(
      TaskListSectionAnswer upcomingTendersIncluded) {
    this.upcomingTendersIncluded = upcomingTendersIncluded;
  }

  public TaskListSectionAnswer getAwardedContractsIncluded() {
    return awardedContractsIncluded;
  }

  public void setAwardedContractsIncluded(
      TaskListSectionAnswer awardedContractsIncluded) {
    this.awardedContractsIncluded = awardedContractsIncluded;
  }

  public TaskListSectionAnswer getCollaborationOpportunitiesIncluded() {
    return collaborationOpportunitiesIncluded;
  }

  public void setCollaborationOpportunitiesIncluded(TaskListSectionAnswer collaborationOpportunitiesIncluded) {
    this.collaborationOpportunitiesIncluded = collaborationOpportunitiesIncluded;
  }

  public TaskListSectionAnswer getWellsIncluded() {
    return wellsIncluded;
  }

  public void setWellsIncluded(
      TaskListSectionAnswer wellsIncluded) {
    this.wellsIncluded = wellsIncluded;
  }

  public TaskListSectionAnswer getPlatformsFpsosIncluded() {
    return platformsFpsosIncluded;
  }

  public void setPlatformsFpsosIncluded(
      TaskListSectionAnswer platformsFpsosIncluded) {
    this.platformsFpsosIncluded = platformsFpsosIncluded;
  }

  public TaskListSectionAnswer getSubseaInfrastructureIncluded() {
    return subseaInfrastructureIncluded;
  }

  public void setSubseaInfrastructureIncluded(
      TaskListSectionAnswer subseaInfrastructureIncluded) {
    this.subseaInfrastructureIncluded = subseaInfrastructureIncluded;
  }

  public TaskListSectionAnswer getIntegratedRigsIncluded() {
    return integratedRigsIncluded;
  }

  public void setIntegratedRigsIncluded(
      TaskListSectionAnswer integratedRigsIncluded) {
    this.integratedRigsIncluded = integratedRigsIncluded;
  }

  public TaskListSectionAnswer getPipelinesIncluded() {
    return pipelinesIncluded;
  }

  public void setPipelinesIncluded(
      TaskListSectionAnswer pipelinesIncluded) {
    this.pipelinesIncluded = pipelinesIncluded;
  }
}
