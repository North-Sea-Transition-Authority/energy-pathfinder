package uk.co.ogauthority.pathfinder.service.project.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.projectmanagement.summary.ProjectManagementSummarySectionService;
import uk.co.ogauthority.pathfinder.service.rendering.TemplateRenderingService;

@Service
public class ProjectSummaryRenderingService {

  private final ProjectManagementSummarySectionService projectManagementSummarySectionService;
  private final TemplateRenderingService templateRenderingService;

  @Autowired
  public ProjectSummaryRenderingService(
      ProjectManagementSummarySectionService projectManagementSummarySectionService,
      TemplateRenderingService templateRenderingService) {
    this.projectManagementSummarySectionService = projectManagementSummarySectionService;
    this.templateRenderingService = templateRenderingService;
  }

  public String renderSummary(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    var projectSummarySection = projectManagementSummarySectionService.getSection(projectDetail, user);
    return templateRenderingService.render(
        projectSummarySection.getTemplatePath(),
        projectSummarySection.getTemplateModel(),
        true
    );
  }
}
