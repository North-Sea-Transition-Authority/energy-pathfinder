package uk.co.ogauthority.pathfinder.service.projectmanagement;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.exception.ProjectManagementHeadingServiceImplementationException;
import uk.co.ogauthority.pathfinder.model.dto.project.ProjectVersionDto;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionType;
import uk.co.ogauthority.pathfinder.model.form.projectmanagement.ProjectManagementForm;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.service.project.ProjectVersionService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.heading.ProjectManagementHeadingService;
import uk.co.ogauthority.pathfinder.service.rendering.TemplateRenderingService;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@Service
public class ProjectManagementViewService {

  public static final String TEMPLATE_PATH = "projectmanagement/manage";

  private final ProjectService projectService;
  private final ProjectManagementService projectManagementService;
  private final ProjectVersionService projectVersionService;
  private final TemplateRenderingService templateRenderingService;

  private final List<ProjectManagementHeadingService> projectManagementHeadingServiceList;

  @Autowired
  public ProjectManagementViewService(ProjectService projectService,
                                      ProjectManagementService projectManagementService,
                                      ProjectVersionService projectVersionService,
                                      TemplateRenderingService templateRenderingService,
                                      List<ProjectManagementHeadingService> projectManagementHeadingServiceList) {
    this.projectService = projectService;
    this.projectManagementService = projectManagementService;
    this.projectVersionService = projectVersionService;
    this.templateRenderingService = templateRenderingService;
    this.projectManagementHeadingServiceList = projectManagementHeadingServiceList;
  }

  public ModelAndView getProjectManagementModelAndView(ProjectDetail latestSubmittedProjectDetail,
                                                       Integer selectedVersion,
                                                       AuthenticatedUserAccount user) {
    var project = latestSubmittedProjectDetail.getProject();

    var selectedVersionProjectDetail = selectedVersion == null || latestSubmittedProjectDetail.getVersion().equals(selectedVersion)
        ? latestSubmittedProjectDetail
        : projectService.getDetailOrError(project.getId(), selectedVersion);

    var form = new ProjectManagementForm();
    form.setVersion(selectedVersionProjectDetail.getVersion());

    final var modelAndView = new ModelAndView(TEMPLATE_PATH)
        .addObject("projectManagementView", getProjectManagementView(latestSubmittedProjectDetail, selectedVersionProjectDetail, user))
        .addObject("viewableVersions", getViewableVersionsMap(project))
        .addObject("form", form)
        .addObject("backLinkUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null, null)))
        .addObject("viewVersionUrl", ReverseRouter.route(on(ManageProjectController.class)
            .updateProjectVersion(project.getId(), null, null, null))
        )
        .addObject("pageTitle", getPageTitle(latestSubmittedProjectDetail));

    ProjectTypeModelUtil.addProjectTypeDisplayNameAttributesToModel(modelAndView, latestSubmittedProjectDetail);

    return modelAndView;
  }

  ProjectManagementView getProjectManagementView(ProjectDetail latestSubmittedProjectDetail,
                                                 ProjectDetail selectedVersionProjectDetail,
                                                 AuthenticatedUserAccount user) {
    var sections = projectManagementService.getSections(latestSubmittedProjectDetail, selectedVersionProjectDetail, user);

    String staticContentHtml = sections.stream()
        .filter(section -> section.getSectionType().equals(ProjectManagementPageSectionType.STATIC_CONTENT))
        .map(summary -> templateRenderingService.render(summary.getTemplatePath(), summary.getTemplateModel(), true))
        .collect(Collectors.joining());

    String versionContentHtml = sections.stream()
        .filter(section -> section.getSectionType().equals(ProjectManagementPageSectionType.VERSION_CONTENT))
        .map(summary -> templateRenderingService.render(summary.getTemplatePath(), summary.getTemplateModel(), true))
        .collect(Collectors.joining());

    return new ProjectManagementView(staticContentHtml, versionContentHtml);
  }

  Map<String, String> getViewableVersionsMap(Project project) {
    return projectVersionService.getSubmittedProjectVersionDtos(project)
        .stream()
        .collect(Collectors.toMap(
            projectVersionDto -> Integer.toString(projectVersionDto.getVersion()),
            this::getViewableVersionDescription,
            (x, y) -> y,
            LinkedHashMap::new
        ));
  }

  String getViewableVersionDescription(ProjectVersionDto projectVersionDto) {
    return String.format(
        "(%s) Submitted: %s %s",
        projectVersionDto.getVersion(),
        DateUtil.formatInstant(projectVersionDto.getSubmittedInstant()),
        (projectVersionDto.isNoUpdate() ? " (No change)" : "")
    );
  }

  private String getPageTitle(ProjectDetail projectDetail) {
    var projectManagementHeadingService = projectManagementHeadingServiceList
        .stream()
        .filter(headingService -> headingService.getSupportedProjectType().equals(projectDetail.getProjectType()))
        .findFirst()
        .orElseThrow(() -> new ProjectManagementHeadingServiceImplementationException(String.format(
            "Could not find implementation of ProjectManagementHeadingService that supports ProjectDetail with ID %d and type %s ",
            projectDetail.getId(),
            projectDetail.getProjectType()
        )));
    return projectManagementHeadingService.getHeadingText(projectDetail);
  }
}
