package uk.co.ogauthority.pathfinder.service.project.location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.location.ProjectLocationController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.location.ProjectLocationView;
import uk.co.ogauthority.pathfinder.model.view.location.ProjectLocationViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class ProjectLocationSectionSummaryService implements ProjectSectionSummaryService {

  public static final String TEMPLATE_PATH = "project/location/locationSectionSummary.ftl";
  public static final String PAGE_NAME = ProjectLocationController.PAGE_NAME;
  public static final String SECTION_ID = "projectLocation";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = ProjectTask.PROJECT_LOCATION.getDisplayOrder();

  private final ProjectLocationService projectLocationService;
  private final ProjectLocationBlocksService projectLocationBlocksService;
  private final DifferenceService differenceService;

  @Autowired
  public ProjectLocationSectionSummaryService(ProjectLocationService projectLocationService,
                                              ProjectLocationBlocksService projectLocationBlocksService,
                                              DifferenceService differenceService) {
    this.projectLocationService = projectLocationService;
    this.projectLocationBlocksService = projectLocationBlocksService;
    this.differenceService = differenceService;
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return projectLocationService.canShowInTaskList(detail);
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("sectionTitle", PAGE_NAME);
    summaryModel.put("sectionId", SECTION_ID);

    var projectLocationView = projectLocationService.getProjectLocationByProjectDetail(detail)
        .map(projectLocation -> {
          var projectLocationBlocks = projectLocationBlocksService.getBlocks(projectLocation);
          return ProjectLocationViewUtil.from(projectLocation, projectLocationBlocks);
        })
        .orElse(new ProjectLocationView());
    summaryModel.put("hasApprovedFieldDevelopmentPlan", Boolean.TRUE.equals(projectLocationView.getApprovedFieldDevelopmentPlan()));
    summaryModel.put("hasApprovedDecomProgram", Boolean.TRUE.equals(projectLocationView.getApprovedDecomProgram()));
    summaryModel.put("projectLocationDiffModel", getProjectLocationDifferenceModel(
        detail,
        projectLocationView
    ));

    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }

  private Map<String, Object> getProjectLocationDifferenceModel(
      ProjectDetail projectDetail,
      ProjectLocationView currentProjectLocationView
  ) {
    var previousProjectLocationView = projectLocationService.getProjectLocationByProjectAndVersion(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )
        .map(projectLocation -> {
          var projectLocationBlocks = projectLocationBlocksService.getBlocks(projectLocation);
          return ProjectLocationViewUtil.from(projectLocation, projectLocationBlocks);
        })
        .orElse(new ProjectLocationView());

    return differenceService.differentiate(currentProjectLocationView, previousProjectLocationView);
  }
}
