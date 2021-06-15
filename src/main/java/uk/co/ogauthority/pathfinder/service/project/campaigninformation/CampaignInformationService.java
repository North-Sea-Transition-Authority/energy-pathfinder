package uk.co.ogauthority.pathfinder.service.project.campaigninformation;

import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;

@Service
public class CampaignInformationService implements ProjectFormSectionService {

  private final ProjectSetupService projectSetupService;

  public CampaignInformationService(
      ProjectSetupService projectSetupService) {
    this.projectSetupService = projectSetupService;
  }

  @Override
  public boolean isComplete(ProjectDetail detail) {
    return false;
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail) {
    return projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.CAMPAIGN_INFORMATION);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {
    //TODO
  }
}