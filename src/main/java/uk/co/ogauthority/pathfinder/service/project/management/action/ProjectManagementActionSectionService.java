package uk.co.ogauthority.pathfinder.service.project.management.action;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.management.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.service.project.management.ProjectManagementSectionService;

@Service
public class ProjectManagementActionSectionService implements ProjectManagementSectionService {

  public static final String TEMPLATE_PATH = "project/management/actions/actions.ftl";
  public static final int DISPLAY_ORDER = 2;

  private final ProjectManagementActionService projectManagementActionService;

  @Autowired
  public ProjectManagementActionSectionService(ProjectManagementActionService projectManagementActionService) {
    this.projectManagementActionService = projectManagementActionService;
  }

  @Override
  public ProjectManagementSection getSection(ProjectDetail projectDetail, AuthenticatedUserAccount authenticatedUserAccount) {
    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("actions", projectManagementActionService.getUserActions(authenticatedUserAccount));
    return new ProjectManagementSection(
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }
}
