package uk.co.ogauthority.pathfinder.service.projectmanagement.transfer;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.management.ProjectManagementSectionType;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionType;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.model.view.projecttransfer.ProjectTransferViewUtil;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectManagementSectionService;
import uk.co.ogauthority.pathfinder.service.projecttransfer.ProjectTransferService;

@Service
public class ProjectManagementTransferSectionService implements ProjectManagementSectionService {

  public static final String TEMPLATE_PATH = "projectmanagement/transfer/projectTransfer.ftl";
  public static final int DISPLAY_ORDER = ProjectManagementSectionType.PROJECT_TRANSFER.getDisplayOrder();
  public static final ProjectManagementPageSectionType SECTION_TYPE = ProjectManagementPageSectionType.VERSION_CONTENT;

  private final ProjectTransferService projectTransferService;
  private final WebUserAccountService webUserAccountService;

  public ProjectManagementTransferSectionService(
      ProjectTransferService projectTransferService,
      WebUserAccountService webUserAccountService) {
    this.projectTransferService = projectTransferService;
    this.webUserAccountService = webUserAccountService;
  }

  @Override
  public boolean useSelectedVersionProjectDetail() {
    return true;
  }

  @Override
  public ProjectManagementSection getSection(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    Map<String, Object> summaryModel = new HashMap<>();

    projectTransferService.getProjectTransfer(projectDetail).ifPresent(projectTransfer -> {
      var transferredByUser = webUserAccountService.getWebUserAccountOrError(projectDetail.getCreatedByWua());
      summaryModel.put("projectTransferView", ProjectTransferViewUtil.from(
          projectTransfer,
          transferredByUser
      ));
    });

    return new ProjectManagementSection(
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER,
        SECTION_TYPE
    );
  }
}
