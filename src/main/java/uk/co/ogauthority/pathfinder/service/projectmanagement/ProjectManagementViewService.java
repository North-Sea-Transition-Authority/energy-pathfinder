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
import uk.co.ogauthority.pathfinder.model.dto.project.ProjectVersionDto;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionType;
import uk.co.ogauthority.pathfinder.model.form.projectmanagement.ProjectManagementForm;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.ProjectVersionService;
import uk.co.ogauthority.pathfinder.service.rendering.TemplateRenderingService;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@Service
public class ProjectManagementViewService {

  private static final String TEMPLATE_PATH = "projectmanagement/manage";

  private final ProjectService projectService;
  private final ProjectManagementService projectManagementService;
  private final ProjectVersionService projectVersionService;
  private final TemplateRenderingService templateRenderingService;

  @Autowired
  public ProjectManagementViewService(ProjectService projectService,
                                      ProjectManagementService projectManagementService,
                                      ProjectVersionService projectVersionService,
                                      TemplateRenderingService templateRenderingService) {
    this.projectService = projectService;
    this.projectManagementService = projectManagementService;
    this.projectVersionService = projectVersionService;
    this.templateRenderingService = templateRenderingService;
  }

  public ModelAndView getProjectManagementModelAndView(ProjectDetail projectDetail,
                                                       Integer version,
                                                       AuthenticatedUserAccount user) {
    var project = projectDetail.getProject();

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

    var projectManagementView = new ProjectManagementView(staticContentHtml, versionContentHtml);

    var viewableVersions = projectVersionService.getSubmittedProjectVersionDtos(project)
        .stream()
        .collect(Collectors.toMap(
            projectVersionDto -> Integer.toString(projectVersionDto.getVersion()),
            this::getViewableVersionDescription,
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

  String getViewableVersionDescription(ProjectVersionDto projectVersionDto) {
    return String.format(
        "(%s) Submitted: %s %s",
        projectVersionDto.getVersion(),
        DateUtil.formatInstant(projectVersionDto.getSubmittedInstant()),
        (projectVersionDto.isNoUpdate() ? " (No change)" : "")
    );
  }
}
