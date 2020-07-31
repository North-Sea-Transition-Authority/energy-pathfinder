package uk.co.ogauthority.pathfinder.service.project;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.team.OrganisationRole;
import uk.co.ogauthority.pathfinder.repository.project.ProjectOperatorsRepository;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.teammanagement.TeamManagementService;

/**
 * Service to manage the linking between ProjectDetails and Operators.
 */
@Service
public class ProjectOperatorService {

  private final TeamService teamService;
  private final TeamManagementService teamManagementService;
  private final ProjectOperatorsRepository projectOperatorsRepository;

  @Autowired
  public ProjectOperatorService(TeamService teamService,
                                TeamManagementService teamManagementService,
                                ProjectOperatorsRepository projectOperatosrRepository) {
    this.teamService = teamService;
    this.teamManagementService = teamManagementService;
    this.projectOperatorsRepository = projectOperatosrRepository;
  }


  /**
   * Create a link between the projectDetails and the PortalOrganisationGroup.
   * @return the projectOperator entity if user is a member of a single team
   */
  @Transactional
  public ProjectOperator createProjectOperator(ProjectDetail projectDetail, AuthenticatedUserAccount webUserAccount) {
    var projectCreator = teamManagementService.getPerson(webUserAccount.getLinkedPerson().getId().asInt());

    //TODO PAT-113 / PAT-133 - only limit if user is not Operator user
    var organisationUnits = teamService.getOrganisationTeamListIfPersonInRole(
          projectCreator,
          Collections.singletonList(OrganisationRole.PROJECT_SUBMITTER)
      );

    //TODO PAT-113 If multiple operators have user select
    if (organisationUnits.size() == 1) {
      var projectOperator = new ProjectOperator(projectDetail, organisationUnits.get(0).getPortalOrganisationGroup());
      return projectOperatorsRepository.save(projectOperator);
    }
    return null;
  }



}
