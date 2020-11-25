package uk.co.ogauthority.pathfinder.service.projectmanagement.action;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.management.ProjectManagementSectionType;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionPosition;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectManagementSectionService;

@Service
public class ProjectManagementActionSectionService implements ProjectManagementSectionService {

  public static final String TEMPLATE_PATH = "projectmanagement/actions/actions.ftl";
  public static final int DISPLAY_ORDER = ProjectManagementSectionType.ACTIONS.getDisplayOrder();
  public static final ProjectManagementPageSectionPosition POSITION = ProjectManagementPageSectionPosition.STATIC_CONTENT;

  private final ProjectManagementActionService projectManagementActionService;

  @Autowired
  public ProjectManagementActionSectionService(ProjectManagementActionService projectManagementActionService) {
    this.projectManagementActionService = projectManagementActionService;
  }

  @Override
  public ProjectManagementSection getSection(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    Map<String, Object> summaryModel = new HashMap<>();
    summaryModel.put("actions", projectManagementActionService.getActions(projectDetail, user));
    return new ProjectManagementSection(
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER,
        POSITION
    );
  }
}
