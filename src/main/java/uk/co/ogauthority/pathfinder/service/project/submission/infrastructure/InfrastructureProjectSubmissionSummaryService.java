package uk.co.ogauthority.pathfinder.service.project.submission.infrastructure;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.submission.ProjectNoUpdateSubmissionSummaryView;
import uk.co.ogauthority.pathfinder.model.view.submission.ProjectSubmissionSummaryView;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.project.submission.ProjectSubmissionSummaryService;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@Service
public class InfrastructureProjectSubmissionSummaryService implements ProjectSubmissionSummaryService {

  private final ProjectInformationService projectInformationService;

  private final WebUserAccountService webUserAccountService;

  @Autowired
  public InfrastructureProjectSubmissionSummaryService(ProjectInformationService projectInformationService,
                                                       WebUserAccountService webUserAccountService) {
    this.projectInformationService = projectInformationService;
    this.webUserAccountService = webUserAccountService;
  }

  @Override
  public ProjectType getSupportedProjectType() {
    return ProjectType.INFRASTRUCTURE;
  }

  @Override
  public ProjectSubmissionSummaryView getSubmissionSummaryView(ProjectDetail projectDetail) {
    return new ProjectSubmissionSummaryView(
        getProjectTitle(projectDetail),
        getFormattedSubmissionDate(projectDetail.getSubmittedInstant()),
        getSubmittingUserName(projectDetail.getSubmittedByWua())
    );
  }

  @Override
  public ProjectNoUpdateSubmissionSummaryView getNoUpdateSubmissionSummaryView(ProjectDetail projectDetail) {
    return new ProjectNoUpdateSubmissionSummaryView(
        getProjectTitle(projectDetail),
        getFormattedSubmissionDate(projectDetail.getSubmittedInstant()),
        getSubmittingUserName(projectDetail.getSubmittedByWua())
    );
  }

  private String getFormattedSubmissionDate(Instant submissionDate) {
    return DateUtil.formatInstant(submissionDate);
  }

  private String getProjectTitle(ProjectDetail projectDetail) {
    return projectInformationService.getProjectTitle(projectDetail);
  }

  private String getSubmittingUserName(int submittedByWuaId) {
    return webUserAccountService.getWebUserAccountOrError(submittedByWuaId).getFullName();
  }
}
