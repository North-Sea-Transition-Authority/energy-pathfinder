package uk.co.ogauthority.pathfinder.service.project.submission.forwardworkplan;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.submission.ProjectNoUpdateSubmissionSummaryView;
import uk.co.ogauthority.pathfinder.model.view.submission.ProjectSubmissionSummaryView;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.submission.ProjectSubmissionSummaryService;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@Service
public class ForwardWorkPlanSubmissionSummaryService implements ProjectSubmissionSummaryService {

  private final ProjectOperatorService projectOperatorService;

  private final WebUserAccountService webUserAccountService;

  @Autowired
  public ForwardWorkPlanSubmissionSummaryService(ProjectOperatorService projectOperatorService,
                                                 WebUserAccountService webUserAccountService) {
    this.projectOperatorService = projectOperatorService;
    this.webUserAccountService = webUserAccountService;
  }

  @Override
  public ProjectType getSupportedProjectType() {
    return ProjectType.FORWARD_WORK_PLAN;
  }

  @Override
  public ProjectSubmissionSummaryView getSubmissionSummaryView(ProjectDetail projectDetail) {
    return new ProjectSubmissionSummaryView(
        getOperatorName(projectDetail),
        getFormattedSubmissionDate(projectDetail.getSubmittedInstant()),
        getSubmittingUserName(projectDetail.getSubmittedByWua())
    );
  }

  @Override
  public ProjectNoUpdateSubmissionSummaryView getNoUpdateSubmissionSummaryView(ProjectDetail projectDetail) {
    return new ProjectNoUpdateSubmissionSummaryView(
        getOperatorName(projectDetail),
        getFormattedSubmissionDate(projectDetail.getSubmittedInstant()),
        getSubmittingUserName(projectDetail.getSubmittedByWua())
    );
  }

  private String getFormattedSubmissionDate(Instant submissionDate) {
    return DateUtil.formatInstant(submissionDate);
  }

  private String getOperatorName(ProjectDetail projectDetail) {
    return projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)
        .getOrganisationGroup()
        .getName();
  }

  private String getSubmittingUserName(int submittedByWuaId) {
    return webUserAccountService.getWebUserAccountOrError(submittedByWuaId).getFullName();
  }
}
