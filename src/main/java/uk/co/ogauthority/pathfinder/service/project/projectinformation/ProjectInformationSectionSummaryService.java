package uk.co.ogauthority.pathfinder.service.project.projectinformation;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.projectinformation.ProjectInformationController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStageSubCategory;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.projectinformation.ProjectInformationView;
import uk.co.ogauthority.pathfinder.model.view.projectinformation.ProjectInformationViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class ProjectInformationSectionSummaryService implements ProjectSectionSummaryService {
  public static final String TEMPLATE_PATH = "project/projectinformation/projectInformationSectionSummary.ftl";
  public static final String PAGE_NAME = ProjectInformationController.PAGE_NAME;
  public static final String SECTION_ID = "projectInformation";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = ProjectTask.PROJECT_INFORMATION.getDisplayOrder();

  private final ProjectInformationService projectInformationService;

  private final DifferenceService differenceService;

  private final ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  @Autowired
  public ProjectInformationSectionSummaryService(
      ProjectInformationService projectInformationService,
      DifferenceService differenceService,
      ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService
  ) {
    this.projectInformationService = projectInformationService;
    this.differenceService = differenceService;
    this.projectSectionSummaryCommonModelService = projectSectionSummaryCommonModelService;
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {

    final var summaryModel = projectSectionSummaryCommonModelService.getCommonSummaryModelMap(
        detail,
        PAGE_NAME,
        SECTION_ID
    );

    var projectInformationView = projectInformationService.getProjectInformation(detail)
        .map(ProjectInformationViewUtil::from)
        .orElse(new ProjectInformationView());

    summaryModel.put("projectInformationDiffModel", getProjectInformationDifferenceModel(
        detail,
        projectInformationView
    ));

    final var fieldStage = projectInformationView.getFieldStage();
    summaryModel.put("isDevelopmentFieldStage", FieldStage.DEVELOPMENT.getDisplayName().equals(fieldStage));
    summaryModel.put("isDiscoveryFieldStage", FieldStage.DISCOVERY.getDisplayName().equals(fieldStage));
    summaryModel.put("hasFieldStageSubCategories", FieldStageSubCategory.getAllFieldStagesWithSubCategoriesAsStrings().contains(fieldStage));

    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }

  private Map<String, Object> getProjectInformationDifferenceModel(
      ProjectDetail projectDetail,
      ProjectInformationView currentProjectInformationView
  ) {

    var previousProjectInformationView = projectInformationService.getProjectInformationByProjectAndVersion(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )
        .map(ProjectInformationViewUtil::from)
        .orElse(new ProjectInformationView());

    return differenceService.differentiate(currentProjectInformationView, previousProjectInformationView);
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return projectInformationService.isTaskValidForProjectDetail(detail);
  }
}
