package uk.co.ogauthority.pathfinder.service.project.projectcontribution;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.projectcontributor.ProjectContributorsController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.projectcontributor.ProjectContributorsView;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class ProjectContributorsSectionSummaryService implements ProjectSectionSummaryService {

  static final String TEMPLATE_PATH = "project/projectcontributors/projectContributorsSectionSummary.ftl";
  static final String PAGE_NAME = ProjectContributorsController.PAGE_NAME;
  static final String SECTION_ID = "project-contributors";
  static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  static final int DISPLAY_ORDER = ProjectTask.PROJECT_CONTRIBUTORS.getDisplayOrder();

  private final ProjectContributorsFormSectionService projectContributorsFormSectionService;
  private final ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;
  private final ProjectContributorSummaryService projectContributorSummaryService;
  private final DifferenceService differenceService;

  @Autowired
  public ProjectContributorsSectionSummaryService(
      ProjectContributorsFormSectionService projectContributorsFormSectionService,
      ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService,
      ProjectContributorSummaryService projectContributorSummaryService,
      DifferenceService differenceService) {
    this.projectContributorsFormSectionService = projectContributorsFormSectionService;
    this.projectSectionSummaryCommonModelService = projectSectionSummaryCommonModelService;
    this.projectContributorSummaryService = projectContributorSummaryService;
    this.differenceService = differenceService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return projectContributorsFormSectionService.canShowInTaskList(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    final var summaryModel = projectSectionSummaryCommonModelService.getCommonSummaryModelMap(
        detail,
        PAGE_NAME,
        SECTION_ID
    );

    var currentProjectContributorsView = projectContributorSummaryService.getProjectContributorsView(detail);

    summaryModel.put("projectContributorDiffModel", getProjectContributorDifferenceModel(
        detail,
        currentProjectContributorsView
    ));

    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }

  private Map<String, ?> getProjectContributorDifferenceModel(ProjectDetail detail,
                                                              ProjectContributorsView currentOrganisationGroupViews) {
    var previousProjectContributorViews = projectContributorSummaryService.getProjectContributorsView(
        detail.getProject(), detail.getVersion() - 1);

    return differenceService.differentiate(
        currentOrganisationGroupViews,
        previousProjectContributorViews
    );
  }
}
