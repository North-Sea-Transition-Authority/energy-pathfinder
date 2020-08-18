package uk.co.ogauthority.pathfinder.service.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
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
  @Transactional
  public ProjectDetail startProject(AuthenticatedUserAccount user, PortalOrganisationGroup organisationGroup) {
    var project = new Project();
    var projectDetails = new ProjectDetail(project, ProjectStatus.DRAFT, user.getWuaId(), FIRST_VERSION, CURRENT_VERSION);
    projectRepository.save(project);
    projectDetailsRepository.save(projectDetails);
    projectOperatorService.createOrUpdateProjectOperator(projectDetails, organisationGroup);

    //TODO PAT-130 audit
    return projectDetails;
  }

  //TODO startProject but which takes an org group

}
