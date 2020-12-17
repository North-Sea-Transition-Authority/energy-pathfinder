package uk.co.ogauthority.pathfinder.service.projectmanagement.heading;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.management.ProjectManagementSectionType;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionType;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectManagementSectionService;

@Service
public class ProjectManagementHeadingSectionService implements ProjectManagementSectionService {

  public static final String TEMPLATE_PATH = "projectmanagement/heading/heading.ftl";
  public static final int DISPLAY_ORDER = ProjectManagementSectionType.HEADING.getDisplayOrder();
  public static final ProjectManagementPageSectionType SECTION_TYPE = ProjectManagementPageSectionType.STATIC_CONTENT;

  private final ProjectInformationService projectInformationService;
  private final ProjectOperatorService projectOperatorService;

  @Autowired
  public ProjectManagementHeadingSectionService(ProjectInformationService projectInformationService,
                                                ProjectOperatorService projectOperatorService) {
    this.projectInformationService = projectInformationService;
    this.projectOperatorService = projectOperatorService;
  }

  @Override
  public ProjectManagementSection getSection(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    Map<String, Object> summaryModel = new HashMap<>();

    var projectInformation = projectInformationService.getProjectInformationOrError(projectDetail);
    var projectOperator = projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail);

    summaryModel.put("projectTitle", projectInformation.getProjectTitle());
    summaryModel.put("projectOperator", projectOperator.getOrganisationGroup().getName());
    return new ProjectManagementSection(
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER,
        SECTION_TYPE
    );
  }
}
