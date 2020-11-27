package uk.co.ogauthority.pathfinder.service.projectmanagement.details;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.management.ProjectManagementSectionType;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionType;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.details.ProjectManagementDetailViewUtil;
import uk.co.ogauthority.pathfinder.service.project.location.ProjectLocationService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectManagementSectionService;

@Service
public class ProjectManagementDetailSectionService implements ProjectManagementSectionService {

  public static final String TEMPLATE_PATH = "projectmanagement/details/projectDetails.ftl";
  public static final int DISPLAY_ORDER = ProjectManagementSectionType.PROJECT_DETAILS.getDisplayOrder();
  public static final ProjectManagementPageSectionType SECTION_TYPE = ProjectManagementPageSectionType.STATIC_CONTENT;

  private final ProjectInformationService projectInformationService;
  private final ProjectLocationService projectLocationService;
  private final WebUserAccountService webUserAccountService;

  @Autowired
  public ProjectManagementDetailSectionService(ProjectInformationService projectInformationService,
                                               ProjectLocationService projectLocationService,
                                               WebUserAccountService webUserAccountService) {
    this.projectInformationService = projectInformationService;
    this.projectLocationService = projectLocationService;
    this.webUserAccountService = webUserAccountService;
  }

  @Override
  public ProjectManagementSection getSection(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    Map<String, Object> summaryModel = new HashMap<>();

    var projectInformation = projectInformationService.getProjectInformationOrError(projectDetail);
    var projectLocation = projectLocationService.getOrError(projectDetail);
    var submitterAccount = webUserAccountService.getWebUserAccountOrError(projectDetail.getSubmittedByWua());

    var projectManagementDetailView = ProjectManagementDetailViewUtil.from(
        projectDetail,
        projectInformation,
        projectLocation,
        submitterAccount
    );
    summaryModel.put("projectManagementDetailView", projectManagementDetailView);
    return new ProjectManagementSection(
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER,
        SECTION_TYPE
    );
  }
}
