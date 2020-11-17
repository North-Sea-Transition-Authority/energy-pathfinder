package uk.co.ogauthority.pathfinder.service.project.projectoperator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.selectoperator.ChangeProjectOperatorController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.view.projectoperator.ProjectOperatorView;
import uk.co.ogauthority.pathfinder.model.view.projectoperator.ProjectOperatorViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.summary.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
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

  @Autowired
  public ProjectOperatorSectionSummaryService(ProjectOperatorService projectOperatorService) {
    this.projectOperatorService = projectOperatorService;
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("sectionTitle", PAGE_NAME);
    summaryModel.put("sectionId", SECTION_ID);

    var projectOperatorView = projectOperatorService.getProjectOperatorByProjectDetail(detail)
        .map(ProjectOperatorViewUtil::from)
        .orElse(new ProjectOperatorView());

    summaryModel.put("projectOperatorView", projectOperatorView);

    return new ProjectSectionSummary(
        List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }
}
