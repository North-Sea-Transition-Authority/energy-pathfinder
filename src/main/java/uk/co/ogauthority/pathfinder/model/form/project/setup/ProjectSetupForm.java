package uk.co.ogauthority.pathfinder.model.form.project.setup;

import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions.TaskListSectionAnswer;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class ProjectSetupForm {

  @NotNull(message = "Select yes if you plan to add any upcoming tenders to your project", groups = FullValidation.class)
  private TaskListSectionAnswer upcomingTendersIncluded;

  @NotNull(message = "Select yes if you plan to add any awarded contracts to your project", groups = FullValidation.class)
  private TaskListSectionAnswer awardedContractsIncluded;

  @NotNull(message = "Select yes if you plan to add any collaboration opportunities to your project", groups = FullValidation.class)
  private TaskListSectionAnswer collaborationOpportunitiesIncluded;

  @NotNull(message = "Select yes if you plan to add any campaign information to your project", groups = FullValidation.class)
  private TaskListSectionAnswer campaignInformationIncluded;

  private TaskListSectionAnswer wellsIncluded;

  private TaskListSectionAnswer platformsFpsosIncluded;

  private TaskListSectionAnswer integratedRigsIncluded;

  private TaskListSectionAnswer subseaInfrastructureIncluded;

  private TaskListSectionAnswer pipelinesIncluded;

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

  public TaskListSectionAnswer getCampaignInformationIncluded() {
    return campaignInformationIncluded;
  }

  public void setCampaignInformationIncluded(
      TaskListSectionAnswer campaignInformationIncluded) {
    this.campaignInformationIncluded = campaignInformationIncluded;
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

  public TaskListSectionAnswer getIntegratedRigsIncluded() {
    return integratedRigsIncluded;
  }

  public void setIntegratedRigsIncluded(
      TaskListSectionAnswer integratedRigsIncluded) {
    this.integratedRigsIncluded = integratedRigsIncluded;
  }

  public TaskListSectionAnswer getSubseaInfrastructureIncluded() {
    return subseaInfrastructureIncluded;
  }

  public void setSubseaInfrastructureIncluded(
      TaskListSectionAnswer subseaInfrastructureIncluded) {
    this.subseaInfrastructureIncluded = subseaInfrastructureIncluded;
  }

  public TaskListSectionAnswer getPipelinesIncluded() {
    return pipelinesIncluded;
  }

  public void setPipelinesIncluded(
      TaskListSectionAnswer pipelinesIncluded) {
    this.pipelinesIncluded = pipelinesIncluded;
  }
}
