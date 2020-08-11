package uk.co.ogauthority.pathfinder.service.project;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.team.OrganisationRole;
import uk.co.ogauthority.pathfinder.repository.project.ProjectOperatorRepository;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.teammanagement.TeamManagementService;

/**
 * Service to manage the linking between ProjectDetails and Operators.
 */
@Service
public class ProjectOperatorService {

  private final TeamService teamService;
  private final TeamManagementService teamManagementService;
  private final ProjectOperatorRepository projectOperatorRepository;

  @Autowired
  public ProjectOperatorService(TeamService teamService,
                                TeamManagementService teamManagementService,
                                ProjectOperatorRepository projectOperatorRepository) {
    this.teamService = teamService;
    this.teamManagementService = teamManagementService;
    this.projectOperatorRepository = projectOperatorRepository;
  }


  /**
   * Create a link between the projectDetails and the PortalOrganisationGroup.
   *
   * @return the projectOperator entity if user is a member of a single team
   */
  @Transactional
  public ProjectOperator createProjectOperator(ProjectDetail detail, AuthenticatedUserAccount user) {
    var projectCreator = teamManagementService.getPerson(user.getLinkedPerson().getId().asInt());

    //TODO PAT-113 / PAT-133 - only limit if user is an Operator user
    var organisationTeams = teamService.getOrganisationTeamListIfPersonInRole(
        projectCreator,
        Collections.singletonList(OrganisationRole.PROJECT_SUBMITTER)
    );

    //TODO PAT-113 If multiple operators have user select
    if (organisationTeams.size() == 1) {
      var projectOperator = new ProjectOperator(detail, organisationTeams.get(0).getPortalOrganisationGroup());
      return projectOperatorRepository.save(projectOperator);
    }
    return null;
  }

  /**
   * If a user is not a regulator work out if they are in the team linked to the project.
   * @return true if the user provided is in the Organisation Group the project is linked to. True always if regulator.
   */
  public boolean isUserInProjectTeamOrRegulator(ProjectDetail detail, AuthenticatedUserAccount user) {
    var person = teamManagementService.getPerson(user.getLinkedPerson().getId().asInt());

    if (teamService.isPersonMemberOfRegulatorTeam(person)) {
      return true;
    }

    var userTeams = teamService.getOrganisationTeamsPersonIsMemberOf(person);

    var projectOperator = projectOperatorRepository.findByProjectDetail(detail)
        .orElseThrow(
            () -> new PathfinderEntityNotFoundException(
                String.format("Project Operator not found for detail with id: %d", detail.getId())
            )
        );

    return userTeams.stream().anyMatch(
        t -> projectOperator.getOrganisationGroup().equals(t.getPortalOrganisationGroup())
      );
  }


}
