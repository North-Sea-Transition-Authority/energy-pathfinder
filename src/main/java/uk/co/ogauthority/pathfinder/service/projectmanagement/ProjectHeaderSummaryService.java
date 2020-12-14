package uk.co.ogauthority.pathfinder.service.projectmanagement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionType;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.service.projectmanagement.details.ProjectManagementDetailSectionService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.heading.ProjectManagementHeadingSectionService;
import uk.co.ogauthority.pathfinder.service.rendering.TemplateRenderingService;

@Service
public class ProjectHeaderSummaryService {

  private final ProjectManagementHeadingSectionService projectManagementHeadingSectionService;
  private final ProjectManagementDetailSectionService projectManagementDetailSectionService;
  private final TemplateRenderingService templateRenderingService;

  @Autowired
  public ProjectHeaderSummaryService(
      ProjectManagementHeadingSectionService projectManagementHeadingSectionService,
      ProjectManagementDetailSectionService projectManagementDetailSectionService,
      TemplateRenderingService templateRenderingService) {
    this.projectManagementHeadingSectionService = projectManagementHeadingSectionService;
    this.projectManagementDetailSectionService = projectManagementDetailSectionService;
    this.templateRenderingService = templateRenderingService;
  }

  public String getProjectHeaderHtml(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    var sections = new ArrayList<ProjectManagementSection>();
    sections.add(projectManagementHeadingSectionService.getSection(projectDetail, user));
    sections.add(projectManagementDetailSectionService.getSection(projectDetail, user));

    sections.sort(Comparator.comparing(ProjectManagementSection::getDisplayOrder));

    return sections.stream()
        .filter(section -> section.getSectionType().equals(ProjectManagementPageSectionType.STATIC_CONTENT))
        .map(summary -> templateRenderingService.render(summary.getTemplatePath(), summary.getTemplateModel(), true))
        .collect(Collectors.joining());
  }
}
