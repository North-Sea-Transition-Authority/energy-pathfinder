package uk.co.ogauthority.pathfinder.service.project.projectoperator;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.selectoperator.ChangeProjectOperatorController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.projectoperator.ProjectOperatorView;
import uk.co.ogauthority.pathfinder.model.view.projectoperator.ProjectOperatorViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.selectoperator.SelectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;

@Service
public class ProjectOperatorSectionSummaryService implements ProjectSectionSummaryService {

  public static final String TEMPLATE_PATH = "project/projectoperator/projectOperatorSectionSummary.ftl";
  public static final String PAGE_NAME = ChangeProjectOperatorController.PAGE_NAME;
  public static final String SECTION_ID = "projectOperator";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = ProjectTask.PROJECT_OPERATOR.getDisplayOrder();

  private final ProjectOperatorService projectOperatorService;
  private final DifferenceService differenceService;
  private final SelectOperatorService selectOperatorService;
  private final ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  @Autowired
  public ProjectOperatorSectionSummaryService(
      ProjectOperatorService projectOperatorService,
      DifferenceService differenceService,
      SelectOperatorService selectOperatorService,
      ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService
  ) {
    this.projectOperatorService = projectOperatorService;
    this.differenceService = differenceService;
    this.selectOperatorService = selectOperatorService;
    this.projectSectionSummaryCommonModelService = projectSectionSummaryCommonModelService;
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {

    final var summaryModel = projectSectionSummaryCommonModelService.getCommonSummaryModelMap(
        detail,
        PAGE_NAME,
        SECTION_ID
    );

    final var currentProjectOperator = projectOperatorService.getProjectOperatorByProjectDetailOrError(detail);

    summaryModel.put("isPublishedAsOperator", BooleanUtils.isTrue(currentProjectOperator.isPublishedAsOperator()));

    summaryModel.put("projectOperatorDiffModel", getProjectOperatorDifferenceModel(detail, currentProjectOperator));

    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }

  private Map<String, Object> getProjectOperatorDifferenceModel(ProjectDetail projectDetail,
                                                                ProjectOperator currentProjectOperator) {

    var currentProjectOperatorView = ProjectOperatorViewUtil.from(currentProjectOperator);

    var previousProjectOperatorView = projectOperatorService.getProjectOperatorByProjectAndVersion(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )
        .map(ProjectOperatorViewUtil::from)
        .orElse(new ProjectOperatorView());

    return differenceService.differentiate(currentProjectOperatorView, previousProjectOperatorView);
  }

  @Override
  public boolean canShowSection(ProjectDetail detail) {
    return selectOperatorService.canShowInTaskList(detail);
  }
}
