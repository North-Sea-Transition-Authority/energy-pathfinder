package uk.co.ogauthority.pathfinder.service.projectmanagement;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionType;
import uk.co.ogauthority.pathfinder.model.form.projectmanagement.ProjectManagementForm;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.ProjectVersionService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.rendering.TemplateRenderingService;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@Service
public class ProjectManagementViewService {

  private static final String TEMPLATE_PATH = "projectmanagement/manage";

  private final ProjectService projectService;
  private final ProjectManagementService projectManagementService;
  private final ProjectInformationService projectInformationService;
  private final ProjectOperatorService projectOperatorService;
  private final ProjectVersionService projectVersionService;
  private final TemplateRenderingService templateRenderingService;

  @Autowired
  public ProjectManagementViewService(ProjectService projectService,
                                      ProjectManagementService projectManagementService,
                                      ProjectInformationService projectInformationService,
                                      ProjectOperatorService projectOperatorService,
                                      ProjectVersionService projectVersionService,
                                      TemplateRenderingService templateRenderingService) {
    this.projectService = projectService;
    this.projectManagementService = projectManagementService;
    this.projectInformationService = projectInformationService;
    this.projectOperatorService = projectOperatorService;
    this.projectVersionService = projectVersionService;
    this.templateRenderingService = templateRenderingService;
  }

  public ModelAndView getProjectManagementModelAndView(ProjectDetail projectDetail,
                                                       Integer version,
                                                       AuthenticatedUserAccount user) {
    var project = projectDetail.getProject();
    var projectInformation = projectInformationService.getProjectInformationOrError(projectDetail);
    var projectOperator = projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail);

    var selectedVersionProjectDetail = version == null || projectDetail.getVersion().equals(version)
        ? projectDetail
        : projectService.getDetailOrError(project.getId(), version);

    var sections = projectManagementService.getSections(projectDetail, selectedVersionProjectDetail, user);

    String staticContentHtml = sections.stream()
        .filter(section -> section.getSectionType().equals(ProjectManagementPageSectionType.STATIC_CONTENT))
        .map(summary -> templateRenderingService.render(summary.getTemplatePath(), summary.getTemplateModel(), true))
        .collect(Collectors.joining());

    String versionContentHtml = sections.stream()
        .filter(section -> section.getSectionType().equals(ProjectManagementPageSectionType.VERSION_CONTENT))
        .map(summary -> templateRenderingService.render(summary.getTemplatePath(), summary.getTemplateModel(), true))
        .collect(Collectors.joining());

    var projectManagementView = new ProjectManagementView(
        projectInformation.getProjectTitle(),
        projectOperator.getOrganisationGroup().getName(),
        staticContentHtml,
        versionContentHtml
    );

    var viewableVersions = projectVersionService.getProjectVersionDtos(project)
        .stream()
        .collect(Collectors.toMap(
            projectVersionDto -> Integer.toString(projectVersionDto.getVersion()),
            projectVersionDto -> String.format(
                "(%s) Submitted: %s",
                projectVersionDto.getVersion(),
                DateUtil.formatInstant(projectVersionDto.getSubmittedInstant())
            ),
            (x, y) -> y,
            LinkedHashMap::new));

    var form = new ProjectManagementForm();
    form.setVersion(selectedVersionProjectDetail.getVersion());

    return new ModelAndView(TEMPLATE_PATH)
        .addObject("projectManagementView", projectManagementView)
        .addObject("backLinkUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null, null)))
        .addObject("viewableVersions", viewableVersions)
        .addObject("form", form)
        .addObject("viewVersionUrl", ReverseRouter.route(on(ManageProjectController.class)
            .updateProjectVersion(project.getId(), null, null, null)));
  }
}
