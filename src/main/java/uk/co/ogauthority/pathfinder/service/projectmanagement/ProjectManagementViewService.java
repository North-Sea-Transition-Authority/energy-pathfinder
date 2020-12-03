package uk.co.ogauthority.pathfinder.service.projectmanagement;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.rendering.TemplateRenderingService;

@Service
public class ProjectManagementViewService {

  private final ProjectManagementService projectManagementService;
  private final ProjectInformationService projectInformationService;
  private final ProjectOperatorService projectOperatorService;
  private final TemplateRenderingService templateRenderingService;

  @Autowired
  public ProjectManagementViewService(ProjectManagementService projectManagementService,
                                      ProjectInformationService projectInformationService,
                                      ProjectOperatorService projectOperatorService,
                                      TemplateRenderingService templateRenderingService) {
    this.projectManagementService = projectManagementService;
    this.projectInformationService = projectInformationService;
    this.projectOperatorService = projectOperatorService;
    this.templateRenderingService = templateRenderingService;
  }

  public ModelAndView getProjectManagementModelAndView(ProjectDetail projectDetail,
                                                       AuthenticatedUserAccount user) {
    var projectInformation = projectInformationService.getProjectInformationOrError(projectDetail);

    var projectOperator = projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail);

    var sections = projectManagementService.getSections(projectDetail, user);

    String combinedRenderedHtml = sections.stream()
        .map(summary -> templateRenderingService.render(summary.getTemplatePath(), summary.getTemplateModel(), true))
        .collect(Collectors.joining());

    var projectManagementView = new ProjectManagementView(
        projectInformation.getProjectTitle(),
        projectOperator.getOrganisationGroup().getName(),
        combinedRenderedHtml);

    return new ModelAndView("projectmanagement/manage")
        .addObject("projectManagementView", projectManagementView)
        .addObject("backLinkUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null, null)));
  }
}
