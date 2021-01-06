package uk.co.ogauthority.pathfinder.service.projectmanagement.notification;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.management.ProjectManagementSectionType;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionType;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectManagementSectionService;

@Service
public class ProjectManagementNotificationSectionService implements ProjectManagementSectionService {

  public static final String TEMPLATE_PATH = "projectmanagement/notification/notification.ftl";
  public static final int DISPLAY_ORDER = ProjectManagementSectionType.NOTIFICATION.getDisplayOrder();
  public static final ProjectManagementPageSectionType SECTION_TYPE = ProjectManagementPageSectionType.STATIC_CONTENT;

  private final ProjectService projectService;
  private final WebUserAccountService webUserAccountService;

  @Autowired
  public ProjectManagementNotificationSectionService(
      ProjectService projectService,
      WebUserAccountService webUserAccountService) {
    this.projectService = projectService;
    this.webUserAccountService = webUserAccountService;
  }

  @Override
  public ProjectManagementSection getSection(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    Map<String, Object> summaryModel = new LinkedHashMap<>();

    var latestProjectDetail = projectService.getLatestDetailOrError(projectDetail.getProject().getId());
    var updateInProgress = latestProjectDetail.getStatus().equals(ProjectStatus.DRAFT);
    summaryModel.put("isUpdateInProgress", updateInProgress);
    if (updateInProgress) {
      var updateCreatedByUser = webUserAccountService.getWebUserAccountOrError(projectDetail.getCreatedByWua());
      summaryModel.put("updateCreatedByUserName", updateCreatedByUser.getFullName());
      summaryModel.put("updateCreatedByUserEmailAddress", updateCreatedByUser.getEmailAddress());
    }

    return new ProjectManagementSection(
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER,
        SECTION_TYPE
    );
  }
}
