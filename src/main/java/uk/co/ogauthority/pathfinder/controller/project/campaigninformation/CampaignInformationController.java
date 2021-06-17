package uk.co.ogauthority.pathfinder.controller.project.campaigninformation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.campaigninformation.CampaignInformationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@ProjectTypeCheck(types = ProjectType.INFRASTRUCTURE)
@RequestMapping("/project/{projectId}/campaign-information")
public class CampaignInformationController {

  public static final String PAGE_NAME = "Campaign information";

  private final CampaignInformationService campaignInformationService;

  public CampaignInformationController(
      CampaignInformationService campaignInformationService) {
    this.campaignInformationService = campaignInformationService;
  }

  @GetMapping
  public ModelAndView getCampaignInformation(@PathVariable("projectId") Integer projectId,
                                             ProjectContext projectContext) {
    return campaignInformationService.getCampaignInformationModelAndView();
  }
}