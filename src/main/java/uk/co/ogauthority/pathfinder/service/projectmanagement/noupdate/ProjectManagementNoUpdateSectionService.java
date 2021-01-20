package uk.co.ogauthority.pathfinder.service.projectmanagement.noupdate;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.management.ProjectManagementSectionType;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionType;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.model.view.projectupdate.NoUpdateNotificationViewUtil;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectManagementSectionService;
import uk.co.ogauthority.pathfinder.service.projectupdate.OperatorProjectUpdateService;

@Service
public class ProjectManagementNoUpdateSectionService implements ProjectManagementSectionService {

  public static final String TEMPLATE_PATH = "projectmanagement/noupdate/noUpdate.ftl";
  public static final int DISPLAY_ORDER = ProjectManagementSectionType.PROJECT_NO_UPDATE_NOTIFICATION.getDisplayOrder();
  public static final ProjectManagementPageSectionType SECTION_TYPE = ProjectManagementPageSectionType.VERSION_CONTENT;

  private final OperatorProjectUpdateService operatorProjectUpdateService;
  private final WebUserAccountService webUserAccountService;
  private final ServiceProperties serviceProperties;

  @Autowired
  public ProjectManagementNoUpdateSectionService(
      OperatorProjectUpdateService operatorProjectUpdateService,
      WebUserAccountService webUserAccountService,
      ServiceProperties serviceProperties) {
    this.operatorProjectUpdateService = operatorProjectUpdateService;
    this.webUserAccountService = webUserAccountService;
    this.serviceProperties = serviceProperties;
  }

  @Override
  public boolean useSelectedVersionProjectDetail() {
    return true;
  }

  @Override
  public ProjectManagementSection getSection(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    Map<String, Object> summaryModel = new HashMap<>();

    operatorProjectUpdateService.getNoUpdateNotificationByUpdateToDetail(projectDetail).ifPresent(noUpdateNotification -> {
      var submittedByUser = webUserAccountService.getWebUserAccountOrError(projectDetail.getCreatedByWua());
      summaryModel.put("noUpdateNotificationView", NoUpdateNotificationViewUtil.from(
          noUpdateNotification,
          submittedByUser
      ));
      summaryModel.put("service", serviceProperties);
    });

    return new ProjectManagementSection(
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER,
        SECTION_TYPE
    );
  }
}
