package uk.co.ogauthority.pathfinder.service.project.management;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.management.ProjectManagementView;
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

  public ProjectManagementView getProjectManagementView(ProjectDetail projectDetail,
                                                        AuthenticatedUserAccount authenticatedUserAccount) {
    var projectInformation = projectInformationService.getProjectInformationOrError(projectDetail);

    var projectOperator = projectOperatorService.getProjectOperatorByProjectDetail(projectDetail)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("Unable to find ProjectOperator for projectDetail with ID %s", projectDetail.getId())));

    var sections = projectManagementService.getSections(projectDetail, authenticatedUserAccount);

    String combinedRenderedHtml = sections.stream()
        .map(summary -> templateRenderingService.render(summary.getTemplatePath(), summary.getTemplateModel(), true))
        .collect(Collectors.joining());

    return new ProjectManagementView(
        projectInformation.getProjectTitle(),
        projectOperator.getOrganisationGroup().getName(),
        combinedRenderedHtml);
  }
}
