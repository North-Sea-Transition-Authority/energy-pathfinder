package uk.co.ogauthority.pathfinder.service.project.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSubmissionSummaryView;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.util.InstantUtil;

@Service
public class ProjectSubmissionSummaryViewService {

  private final ProjectInformationService projectInformationService;
  private final WebUserAccountService webUserAccountService;

  @Autowired
  public ProjectSubmissionSummaryViewService(ProjectInformationService projectInformationService,
                                             WebUserAccountService webUserAccountService) {
    this.projectInformationService = projectInformationService;
    this.webUserAccountService = webUserAccountService;
  }

  public ProjectSubmissionSummaryView getProjectSubmissionSummaryView(ProjectDetail projectDetail) {
    var projectInformation = projectInformationService.getProjectInformationOrError(projectDetail);

    var webUserAccount = webUserAccountService.getWebUserAccountOrError(projectDetail.getSubmittedByWua());

    return new ProjectSubmissionSummaryView(projectInformation.getProjectTitle(),
        InstantUtil.formatInstant(projectDetail.getSubmittedInstant()),
        webUserAccount.getFullName());
  }
}
