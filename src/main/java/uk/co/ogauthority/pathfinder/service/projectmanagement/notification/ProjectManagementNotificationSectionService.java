package uk.co.ogauthority.pathfinder.service.projectmanagement.notification;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.management.ProjectManagementSectionType;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionType;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.model.view.projectupdate.RegulatorUpdateRequestView;
import uk.co.ogauthority.pathfinder.model.view.projectupdate.RegulatorUpdateRequestViewUtil;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectManagementSectionService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorUpdateRequestService;

@Service
public class ProjectManagementNotificationSectionService implements ProjectManagementSectionService {

  public static final String TEMPLATE_PATH = "projectmanagement/notification/notification.ftl";
  public static final int DISPLAY_ORDER = ProjectManagementSectionType.NOTIFICATION.getDisplayOrder();
  public static final ProjectManagementPageSectionType SECTION_TYPE = ProjectManagementPageSectionType.STATIC_CONTENT;

  private final ProjectService projectService;
  private final RegulatorUpdateRequestService regulatorUpdateRequestService;
  private final WebUserAccountService webUserAccountService;
  private final ServiceProperties serviceProperties;

  @Autowired
  public ProjectManagementNotificationSectionService(
      ProjectService projectService,
      RegulatorUpdateRequestService regulatorUpdateRequestService,
      WebUserAccountService webUserAccountService,
      ServiceProperties serviceProperties) {
    this.projectService = projectService;
    this.regulatorUpdateRequestService = regulatorUpdateRequestService;
    this.webUserAccountService = webUserAccountService;
    this.serviceProperties = serviceProperties;
  }

  @Override
  public ProjectManagementSection getSection(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    Map<String, Object> summaryModel = new LinkedHashMap<>();

    var showUpdateInProgressNotification = false;


    final var latestSubmittedProjectDetail = projectService.getLatestSubmittedDetailOrError(projectDetail.getProject().getId());
    final var regulatorUpdateRequestOptional = regulatorUpdateRequestService.getUpdateRequest(latestSubmittedProjectDetail);
    final var showRegulatorUpdateRequestNotification = regulatorUpdateRequestOptional.isPresent();
    summaryModel.put("showRegulatorUpdateRequestNotification", showRegulatorUpdateRequestNotification);

    if (showRegulatorUpdateRequestNotification) {
      regulatorUpdateRequestOptional.ifPresent(regulatorUpdateRequest -> {
        summaryModel.put("regulatorUpdateRequestView", getRegulatorUpdateRequestView(regulatorUpdateRequest));
        summaryModel.put("service", serviceProperties);
      });
    } else {
      final var latestProjectDetail = projectService.getLatestDetailOrError(projectDetail.getProject().getId());
      showUpdateInProgressNotification = latestProjectDetail.getStatus().equals(ProjectStatus.DRAFT);

      if (showUpdateInProgressNotification) {
        var updateCreatedByUser = webUserAccountService.getWebUserAccountOrError(projectDetail.getCreatedByWua());
        summaryModel.put("updateCreatedByUserName", updateCreatedByUser.getFullName());
        summaryModel.put("updateCreatedByUserEmailAddress", updateCreatedByUser.getEmailAddress());
      }
    }

    summaryModel.put("showUpdateInProgressNotification", showUpdateInProgressNotification);

    ProjectTypeModelUtil.addProjectTypeDisplayNameAttributesToModel(summaryModel, projectDetail);

    return new ProjectManagementSection(
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER,
        SECTION_TYPE
    );
  }

  private RegulatorUpdateRequestView getRegulatorUpdateRequestView(RegulatorUpdateRequest regulatorUpdateRequest) {
    final var requestedByUser = webUserAccountService.getWebUserAccountOrError(
        regulatorUpdateRequest.getRequestedByWuaId()
    );
    return RegulatorUpdateRequestViewUtil.from(regulatorUpdateRequest, requestedByUser);
  }
}
