package uk.co.ogauthority.pathfinder.service.projectmanagement.summary;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.management.ProjectManagementSectionType;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionPosition;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSummaryViewService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectManagementSectionService;

@Service
public class ProjectManagementSummarySectionService implements ProjectManagementSectionService {

  public static final String TEMPLATE_PATH = "projectmanagement/summary/projectManagementSummary.ftl";
  public static final int DISPLAY_ORDER = ProjectManagementSectionType.PROJECT_SUMMARY.getDisplayOrder();
  public static final ProjectManagementPageSectionPosition POSITION = ProjectManagementPageSectionPosition.VERSION_CONTENT;

  private final ProjectSummaryViewService projectSummaryViewService;

  @Autowired
  public ProjectManagementSummarySectionService(ProjectSummaryViewService projectSummaryViewService) {
    this.projectSummaryViewService = projectSummaryViewService;
  }

  @Override
  public boolean useSelectedVersionProjectDetail() {
    return true;
  }

  @Override
  public ProjectManagementSection getSection(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("projectSummaryView", projectSummaryViewService.getProjectSummaryView(projectDetail));
    return new ProjectManagementSection(
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER,
        POSITION
    );
  }
}
