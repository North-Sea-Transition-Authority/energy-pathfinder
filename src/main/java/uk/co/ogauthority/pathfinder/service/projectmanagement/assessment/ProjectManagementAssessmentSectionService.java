package uk.co.ogauthority.pathfinder.service.projectmanagement.assessment;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.management.ProjectManagementSectionType;
import uk.co.ogauthority.pathfinder.model.view.projectassessment.ProjectAssessmentViewUtil;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectManagementSectionService;
import uk.co.ogauthority.pathfinder.service.team.ManageTeamService;

@Service
public class ProjectManagementAssessmentSectionService implements ProjectManagementSectionService {

  public static final String TEMPLATE_PATH = "projectmanagement/assessment/projectAssessment.ftl";
  public static final int DISPLAY_ORDER = ProjectManagementSectionType.PROJECT_ASSESSMENT.getDisplayOrder();

  private final ProjectAssessmentService projectAssessmentService;
  private final ManageTeamService manageTeamService;
  private final WebUserAccountService webUserAccountService;

  @Autowired
  public ProjectManagementAssessmentSectionService(ProjectAssessmentService projectAssessmentService,
                                                   ManageTeamService manageTeamService,
                                                   WebUserAccountService webUserAccountService) {
    this.projectAssessmentService = projectAssessmentService;
    this.manageTeamService = manageTeamService;
    this.webUserAccountService = webUserAccountService;
  }

  @Override
  public ProjectManagementSection getSection(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    Map<String, Object> summaryModel = new HashMap<>();

    if (manageTeamService.isPersonMemberOfRegulatorTeam(user)) {
      projectAssessmentService.getProjectAssessment(projectDetail).ifPresent(projectAssessment -> {
        var submitter = webUserAccountService.getWebUserAccountOrError(projectAssessment.getAssessorWuaId());
        summaryModel.put("projectAssessmentView", ProjectAssessmentViewUtil.from(projectAssessment, submitter));
      });
    }

    return new ProjectManagementSection(
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }
}
