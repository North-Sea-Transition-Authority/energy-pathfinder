package uk.co.ogauthority.pathfinder.service.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorForm;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.repository.project.ProjectRepository;

/**
 * Service to create the required entities for a functioning Project to exist.
 */
@Service
public class StartProjectService {

  public static final Integer FIRST_VERSION = 1;
  public static final boolean CURRENT_VERSION = true;

  private final ProjectRepository projectRepository;
  private final ProjectDetailsRepository projectDetailsRepository;
  private final ProjectOperatorService projectOperatorService;

  @Autowired
  public StartProjectService(ProjectRepository projectRepository,
                             ProjectDetailsRepository projectDetailsRepository,
                             ProjectOperatorService projectOperatorService) {
    this.projectRepository = projectRepository;
    this.projectDetailsRepository = projectDetailsRepository;
    this.projectOperatorService = projectOperatorService;
  }


  /**
   * Create a draft project and projectOperator for the provided user.
   */
  private ProjectDetail startProject(
      AuthenticatedUserAccount user,
      ProjectOperatorForm projectOperatorForm,
      ProjectType projectType
  ) {
    var project = new Project();
    var projectDetails = new ProjectDetail(
        project,
        ProjectStatus.DRAFT,
        user.getWuaId(),
        FIRST_VERSION,
        CURRENT_VERSION,
        projectType
    );
    projectRepository.save(project);
    projectDetailsRepository.save(projectDetails);
    projectOperatorService.createOrUpdateProjectOperator(projectDetails, projectOperatorForm);

    return projectDetails;
  }

  @Transactional
  public ProjectDetail createInfrastructureProject(
      AuthenticatedUserAccount user,
      ProjectOperatorForm projectOperatorForm
  ) {
    return startProject(user, projectOperatorForm, ProjectType.INFRASTRUCTURE);
  }

  @Transactional
  public ProjectDetail createForwardWorkPlanProject(
      AuthenticatedUserAccount user,
      PortalOrganisationGroup portalOrganisationGroup
  ) {
    var projectOperatorForm = new ProjectOperatorForm();
    projectOperatorForm.setOperator(String.valueOf(portalOrganisationGroup.getOrgGrpId()));
    return startProject(user, projectOperatorForm, ProjectType.FORWARD_WORK_PLAN);
  }
}
