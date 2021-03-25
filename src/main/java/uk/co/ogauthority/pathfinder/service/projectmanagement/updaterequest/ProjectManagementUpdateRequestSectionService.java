package uk.co.ogauthority.pathfinder.service.projectmanagement.updaterequest;

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
import uk.co.ogauthority.pathfinder.model.view.projectupdate.RegulatorUpdateRequestViewUtil;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectManagementSectionService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorUpdateRequestService;

@Service
public class ProjectManagementUpdateRequestSectionService implements ProjectManagementSectionService {

  public static final String TEMPLATE_PATH = "projectmanagement/updaterequest/updateRequest.ftl";
  public static final int DISPLAY_ORDER = ProjectManagementSectionType.PROJECT_UPDATE_REQUEST.getDisplayOrder();
  public static final ProjectManagementPageSectionType SECTION_TYPE = ProjectManagementPageSectionType.VERSION_CONTENT;

  private final RegulatorUpdateRequestService regulatorUpdateRequestService;
  private final WebUserAccountService webUserAccountService;
  private final ServiceProperties serviceProperties;

  @Autowired
  public ProjectManagementUpdateRequestSectionService(
      RegulatorUpdateRequestService regulatorUpdateRequestService,
      WebUserAccountService webUserAccountService,
      ServiceProperties serviceProperties) {
    this.regulatorUpdateRequestService = regulatorUpdateRequestService;
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

    regulatorUpdateRequestService.getUpdateRequest(projectDetail).ifPresent(regulatorUpdateRequest -> {
      var requestedByUser = webUserAccountService.getWebUserAccountOrError(
          regulatorUpdateRequest.getRequestedByWuaId());
      summaryModel.put("regulatorUpdateRequestView", RegulatorUpdateRequestViewUtil.from(
          regulatorUpdateRequest,
          requestedByUser
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
