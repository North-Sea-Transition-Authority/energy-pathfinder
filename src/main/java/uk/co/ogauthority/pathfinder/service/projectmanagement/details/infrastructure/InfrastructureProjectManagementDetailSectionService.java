package uk.co.ogauthority.pathfinder.service.projectmanagement.details.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.details.ProjectManagementDetailView;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.details.infrastructure.InfrastructureProjectManagementDetailViewUtil;
import uk.co.ogauthority.pathfinder.service.project.location.ProjectLocationService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.details.ProjectManagementDetailService;

@Service
public class InfrastructureProjectManagementDetailSectionService implements ProjectManagementDetailService {

  protected static final String TEMPLATE_PATH = "projectmanagement/details/infrastructureProjectDetails.ftl";

  private final ProjectInformationService projectInformationService;
  private final ProjectLocationService projectLocationService;
  private final WebUserAccountService webUserAccountService;

  @Autowired
  public InfrastructureProjectManagementDetailSectionService(ProjectInformationService projectInformationService,
                                                             ProjectLocationService projectLocationService,
                                                             WebUserAccountService webUserAccountService) {
    this.projectInformationService = projectInformationService;
    this.projectLocationService = projectLocationService;
    this.webUserAccountService = webUserAccountService;
  }

  @Override
  public ProjectType getSupportedProjectType() {
    return ProjectType.INFRASTRUCTURE;
  }

  @Override
  public String getTemplatePath() {
    return TEMPLATE_PATH;
  }

  @Override
  public ProjectManagementDetailView getManagementDetailView(ProjectDetail projectDetail) {

    var projectInformation = projectInformationService.getProjectInformationOrError(projectDetail);

    var isEnergyTransitionProject = projectInformationService.isEnergyTransitionProject(projectInformation);

    var projectLocationField = !isEnergyTransitionProject
        ? projectLocationService.getOrError(projectDetail).getField()
        : null;

    var submitterAccount = webUserAccountService.getWebUserAccountOrError(projectDetail.getSubmittedByWua());

    return InfrastructureProjectManagementDetailViewUtil.from(
        projectDetail,
        projectInformation.getFieldStage(),
        projectLocationField,
        isEnergyTransitionProject,
        submitterAccount
    );
  }
}
