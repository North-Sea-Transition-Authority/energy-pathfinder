package uk.co.ogauthority.pathfinder.service.project.campaigninformation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.campaigninformation.CampaignInformationController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.project.campaigninformation.CampaignInformationForm;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;

@Service
public class CampaignInformationModelService {

  protected static final String FORM_TEMPLATE_PATH = "project/campaigninformation/campaignInformationForm";

  private final BreadcrumbService breadcrumbService;
  private final CampaignProjectService campaignProjectService;

  @Autowired
  public CampaignInformationModelService(BreadcrumbService breadcrumbService,
                                         CampaignProjectService campaignProjectService) {
    this.breadcrumbService = breadcrumbService;
    this.campaignProjectService = campaignProjectService;
  }

  public ModelAndView getCampaignInformationModelAndView(ProjectDetail projectDetail, CampaignInformationForm form) {

    final var modelAndView = new ModelAndView(FORM_TEMPLATE_PATH)
        .addObject("pageTitle", CampaignInformationController.PAGE_NAME)
        .addObject("form", form)
        .addObject(
            "publishedProjectRestUrl",
            campaignProjectService.getCampaignProjectRestUrl()
        )
        .addObject("alreadyAddedProjects", campaignProjectService.getCampaignProjectViews(form));

    ProjectTypeModelUtil.addProjectTypeDisplayNameAttributesToModel(modelAndView, projectDetail);

    breadcrumbService.fromTaskList(
        projectDetail.getProject().getId(),
        modelAndView,
        CampaignInformationController.PAGE_NAME
    );

    return modelAndView;
  }
}
