package uk.co.ogauthority.pathfinder.service.project.management.summary;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.management.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.service.project.management.ProjectManagementSectionService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSummaryViewService;

@Service
public class ProjectManagementSummarySectionService implements ProjectManagementSectionService {

  public static final String TEMPLATE_PATH = "project/management/summary/projectSummary.ftl";
  public static final int DISPLAY_ORDER = 3;

  private final ProjectSummaryViewService projectSummaryViewService;

  @Autowired
  public ProjectManagementSummarySectionService(ProjectSummaryViewService projectSummaryViewService) {
    this.projectSummaryViewService = projectSummaryViewService;
  }

  @Override
  public ProjectManagementSection getSection(ProjectDetail projectDetail, AuthenticatedUserAccount authenticatedUserAccount) {
    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("projectSummaryView", projectSummaryViewService.getProjectSummaryView(projectDetail));
    return new ProjectManagementSection(
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }
}
