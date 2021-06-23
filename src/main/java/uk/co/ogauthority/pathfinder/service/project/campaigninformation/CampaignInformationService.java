package uk.co.ogauthority.pathfinder.service.project.campaigninformation;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.campaigninformation.CampaignInformationController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;

@Service
public class CampaignInformationService implements ProjectFormSectionService {

  public static final String FORM_TEMPLATE_PATH = "project/campaigninformation/campaignInformationFormSummary";

  private final ProjectSetupService projectSetupService;

  public CampaignInformationService(
      ProjectSetupService projectSetupService) {
    this.projectSetupService = projectSetupService;
  }

  public ModelAndView getCampaignInformationModelAndView() {
    return new ModelAndView(FORM_TEMPLATE_PATH)
        .addObject("pageTitle", CampaignInformationController.PAGE_NAME);
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
    //TODO PAT-583
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    //TODO PAT-583
  }
}