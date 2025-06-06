package uk.co.ogauthority.pathfinder.service.project.summary;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSummaryView;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceFreemarkerService;
import uk.co.ogauthority.pathfinder.service.rendering.TemplateRenderingService;

/**
 * Provides the view objects which are used to render the project summary templates.
 */
@Service
public class ProjectSummaryViewService {

  private final ProjectSummaryService projectSummaryService;
  private final TemplateRenderingService templateRenderingService;
  private final DifferenceFreemarkerService differenceFreemarkerService;

  @Autowired
  public ProjectSummaryViewService(
      ProjectSummaryService projectSummaryService,
      TemplateRenderingService templateRenderingService,
      DifferenceFreemarkerService differenceFreemarkerService) {
    this.projectSummaryService = projectSummaryService;
    this.templateRenderingService = templateRenderingService;
    this.differenceFreemarkerService = differenceFreemarkerService;
  }

  public ProjectSummaryView getProjectSummaryView(ProjectDetail detail) {
    var summarisedSections = projectSummaryService.summarise(detail);
    String combinedRenderedSummaryHtml = summarisedSections.stream()
        .peek(projectSectionSummary ->
            projectSectionSummary.getTemplateModel().put("differenceFreemarkerService", differenceFreemarkerService))
        .map(summary -> templateRenderingService.render(summary.getTemplatePath(), summary.getTemplateModel(), true))
        .collect(Collectors.joining());

    List<SidebarSectionLink> sidebarSectionLinks = summarisedSections.stream()
        .flatMap(o -> o.getSidebarSectionLinks().stream())
        .collect(Collectors.toList());

    return new ProjectSummaryView(combinedRenderedSummaryHtml, sidebarSectionLinks);
  }
}
