package uk.co.ogauthority.pathfinder.service.project;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.repository.project.ProjectOperatorRepository;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

/**
 * Service to manage the linking between ProjectDetails and Operators.
 */
@Service
public class ProjectOperatorService {

  private final TeamService teamService;
  private final ProjectOperatorRepository projectOperatorRepository;

  @Autowired
  public ProjectOperatorService(TeamService teamService,
                                ProjectOperatorRepository projectOperatorRepository) {
    this.teamService = teamService;
    this.projectOperatorRepository = projectOperatorRepository;
  }

  /**
   * Create a ProjectOperator for the specified detail and org group.
   * If one exists already for that detail update the operator
   *
   * @param detail            Detail to link to Org Group.
   * @param organisationGroup Org Group to create for.
   * @return ProjectOperator for the specified detail and org group.
   */
  @Transactional
  public ProjectOperator createOrUpdateProjectOperator(ProjectDetail detail,
                                                       PortalOrganisationGroup organisationGroup) {
    var projectOperator = getProjectOperatorByProjectDetail(detail).orElse(new ProjectOperator(detail));
    projectOperator.setOrganisationGroup(organisationGroup);
    return projectOperatorRepository.save(projectOperator);
  }

  /**
   * If a user is not a regulator work out if they are in the team linked to the project.
   *
   * @return true if the user provided is in the Organisation Group the project is linked to. True always if regulator.
   */
  public boolean isUserInProjectTeamOrRegulator(ProjectDetail detail, AuthenticatedUserAccount user) {
    var person = user.getLinkedPerson();

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

  public boolean canUserAccessOrgGroup(AuthenticatedUserAccount user, PortalOrganisationGroup portalOrganisationGroup) {
    var userTeams = teamService.getOrganisationTeamsPersonIsMemberOf(user.getLinkedPerson());
    return userTeams.stream().anyMatch(t -> t.getPortalOrganisationGroup().equals(portalOrganisationGroup));
  }


  public Optional<ProjectOperator> getProjectOperatorByProjectDetail(ProjectDetail detail) {
    return projectOperatorRepository.findByProjectDetail(detail);
  }

  public boolean isUserInMultipleTeams(AuthenticatedUserAccount user) {
    return teamService.getOrganisationTeamsPersonIsMemberOf(user.getLinkedPerson()).size() > 1;
  }

}
