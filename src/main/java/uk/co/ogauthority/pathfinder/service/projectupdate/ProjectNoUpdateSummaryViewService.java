package uk.co.ogauthority.pathfinder.service.projectupdate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.projectupdate.ProjectNoUpdateSummaryView;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@Service
public class ProjectNoUpdateSummaryViewService {

  private final ProjectInformationService projectInformationService;
  private final WebUserAccountService webUserAccountService;

  @Autowired
  public ProjectNoUpdateSummaryViewService(ProjectInformationService projectInformationService,
                                           WebUserAccountService webUserAccountService) {
    this.projectInformationService = projectInformationService;
    this.webUserAccountService = webUserAccountService;
  }

  public ProjectNoUpdateSummaryView getProjectNoUpdateSummaryView(ProjectDetail projectDetail) {
    var projectInformation = projectInformationService.getProjectInformationOrError(projectDetail);

    var webUserAccount = webUserAccountService.getWebUserAccountOrError(projectDetail.getSubmittedByWua());

    return new ProjectNoUpdateSummaryView(
        projectInformation.getProjectTitle(),
        DateUtil.formatInstant(projectDetail.getSubmittedInstant()),
        webUserAccount.getFullName());
  }
}
