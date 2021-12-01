package uk.co.ogauthority.pathfinder.service.projectmanagement.details.forwardworkplan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.details.ProjectManagementDetailView;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.details.forwardworkplan.ForwardWorkPlanManagementDetailViewUtil;
import uk.co.ogauthority.pathfinder.service.projectmanagement.details.ProjectManagementDetailService;

@Service
public class ForwardWorkPlanManagementDetailSectionService implements ProjectManagementDetailService {

  protected static final String TEMPLATE_PATH = "projectmanagement/details/forwardWorkPlanProjectDetails.ftl";

  private final WebUserAccountService webUserAccountService;

  @Autowired
  public ForwardWorkPlanManagementDetailSectionService(WebUserAccountService webUserAccountService) {
    this.webUserAccountService = webUserAccountService;
  }

  @Override
  public ProjectType getSupportedProjectType() {
    return ProjectType.FORWARD_WORK_PLAN;
  }

  @Override
  public String getTemplatePath() {
    return TEMPLATE_PATH;
  }

  @Override
  public ProjectManagementDetailView getManagementDetailView(ProjectDetail projectDetail) {
    final var submitterWebUserAccount = webUserAccountService.getWebUserAccountOrError(
        projectDetail.getSubmittedByWua()
    );
    return ForwardWorkPlanManagementDetailViewUtil.from(projectDetail, submitterWebUserAccount);
  }
}
