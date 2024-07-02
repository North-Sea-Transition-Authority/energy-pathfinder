package uk.co.ogauthority.pathfinder.controller.project.campaigninformation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.campaigninformation.CampaignInformationForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.project.campaigninformation.CampaignInformationModelService;
import uk.co.ogauthority.pathfinder.service.project.campaigninformation.CampaignInformationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@ProjectTypeCheck(types = ProjectType.INFRASTRUCTURE)
@RequestMapping("/project/{projectId}/campaign-information")
public class CampaignInformationController {

  public static final String PAGE_NAME = "Campaign information";

  private final ControllerHelperService controllerHelperService;
  private final CampaignInformationService campaignInformationService;
  private final CampaignInformationModelService campaignInformationModelService;

  @Autowired
  public CampaignInformationController(ControllerHelperService controllerHelperService,
                                       CampaignInformationService campaignInformationService,
                                       CampaignInformationModelService campaignInformationModelService) {
    this.controllerHelperService = controllerHelperService;
    this.campaignInformationService = campaignInformationService;
    this.campaignInformationModelService = campaignInformationModelService;
  }

  @GetMapping
  public ModelAndView getCampaignInformation(@PathVariable("projectId") Integer projectId,
                                             ProjectContext projectContext) {
    final var projectDetail = projectContext.getProjectDetails();
    return campaignInformationModelService.getCampaignInformationModelAndView(
        projectDetail,
        campaignInformationService.getForm(projectDetail)
    );
  }

  @PostMapping
  public ModelAndView saveCampaignInformation(@PathVariable("projectId") Integer projectId,
                                              @Valid @ModelAttribute("form") CampaignInformationForm form,
                                              BindingResult bindingResult,
                                              ValidationType validationType,
                                              ProjectContext projectContext) {

    final var projectDetail = projectContext.getProjectDetails();

    bindingResult = campaignInformationService.validate(form, bindingResult, validationType, projectDetail);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        campaignInformationModelService.getCampaignInformationModelAndView(projectDetail, form),
        form,
        () -> {
          var campaignInformation = campaignInformationService.createOrUpdateCampaignInformation(
              form,
              projectDetail
          );
          AuditService.audit(
              AuditEvent.CAMPAIGN_INFORMATION_UPDATED,
              String.format(
                  AuditEvent.CAMPAIGN_INFORMATION_UPDATED.getMessage(),
                  campaignInformation.getId(),
                  projectDetail.getId()
              )
          );
          return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
        }
    );
  }
}