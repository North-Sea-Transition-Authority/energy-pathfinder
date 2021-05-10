package uk.co.ogauthority.pathfinder.service.project.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSubmissionSummaryView;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@Service
public class ProjectSubmissionSummaryViewService {

  private final ProjectInformationService projectInformationService;
  private final WebUserAccountService webUserAccountService;
  private final ProjectOperatorService projectOperatorService;

  @Autowired
  public ProjectSubmissionSummaryViewService(ProjectInformationService projectInformationService,
                                             WebUserAccountService webUserAccountService,
                                             ProjectOperatorService projectOperatorService) {
    this.projectInformationService = projectInformationService;
    this.webUserAccountService = webUserAccountService;
    this.projectOperatorService = projectOperatorService;
  }

  public ProjectSubmissionSummaryView getProjectSubmissionSummaryView(ProjectDetail projectDetail) {

    var webUserAccount = webUserAccountService.getWebUserAccountOrError(projectDetail.getSubmittedByWua());

    return new ProjectSubmissionSummaryView(
        getProjectDisplayName(projectDetail),
        DateUtil.formatInstant(projectDetail.getSubmittedInstant()),
        webUserAccount.getFullName());
  }

  private String getProjectDisplayName(ProjectDetail projectDetail) {
    if (ProjectService.isForwardWorkPlanProject(projectDetail)) {
      return String.valueOf(projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail));
    } else if (ProjectService.isInfrastructureProject(projectDetail)) {
      var projectInformation = projectInformationService.getProjectInformationOrError(projectDetail);

      return projectInformation.getProjectTitle();
    } else {
      throw new RuntimeException(
          String.format(
              "Found unknown projectType %s for projectDetail with ID %s",
              projectDetail.getProjectType(),
              projectDetail.getId()
          )
      );
    }
  }
}
