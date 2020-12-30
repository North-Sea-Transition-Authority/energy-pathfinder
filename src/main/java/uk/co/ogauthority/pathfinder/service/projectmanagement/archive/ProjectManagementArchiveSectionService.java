package uk.co.ogauthority.pathfinder.service.projectmanagement.archive;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.management.ProjectManagementSectionType;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionType;
import uk.co.ogauthority.pathfinder.model.view.projectarchive.ProjectArchiveDetailViewUtil;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.service.projectarchive.ArchiveProjectService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectManagementSectionService;

@Service
public class ProjectManagementArchiveSectionService implements ProjectManagementSectionService {

  public static final String TEMPLATE_PATH = "projectmanagement/archive/projectArchiveDetails.ftl";
  public static final int DISPLAY_ORDER = ProjectManagementSectionType.PROJECT_ARCHIVE.getDisplayOrder();
  public static final ProjectManagementPageSectionType SECTION_TYPE = ProjectManagementPageSectionType.VERSION_CONTENT;

  private final ArchiveProjectService archiveProjectService;
  private final WebUserAccountService webUserAccountService;

  public ProjectManagementArchiveSectionService(
      ArchiveProjectService archiveProjectService,
      WebUserAccountService webUserAccountService) {
    this.archiveProjectService = archiveProjectService;
    this.webUserAccountService = webUserAccountService;
  }

  @Override
  public boolean useSelectedVersionProjectDetail() {
    return true;
  }

  @Override
  public ProjectManagementSection getSection(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    Map<String, Object> summaryModel = new HashMap<>();

    if (projectDetail.getStatus().equals(ProjectStatus.ARCHIVED)) {
      var projectArchiveDetail = archiveProjectService.getProjectArchiveDetailOrError(projectDetail);
      var archivedByUser = webUserAccountService.getWebUserAccountOrError(projectDetail.getCreatedByWua());
      summaryModel.put("projectArchiveDetailView", ProjectArchiveDetailViewUtil.from(
          projectArchiveDetail,
          projectDetail.getCreatedDatetime(),
          archivedByUser
      ));
    }

    return new ProjectManagementSection(
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER,
        SECTION_TYPE
    );
  }
}
