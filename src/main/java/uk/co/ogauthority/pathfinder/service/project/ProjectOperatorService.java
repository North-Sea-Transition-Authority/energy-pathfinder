package uk.co.ogauthority.pathfinder.service.project;

import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorForm;
import uk.co.ogauthority.pathfinder.repository.project.ProjectOperatorRepository;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

/**
 * Service to manage the linking between ProjectDetails and Operators.
 */
@Service
public class ProjectOperatorService {

  private final TeamService teamService;
  private final ProjectOperatorRepository projectOperatorRepository;
  private final PortalOrganisationAccessor portalOrganisationAccessor;

  @Autowired
  public ProjectOperatorService(TeamService teamService,
                                ProjectOperatorRepository projectOperatorRepository,
                                PortalOrganisationAccessor portalOrganisationAccessor) {
    this.teamService = teamService;
    this.projectOperatorRepository = projectOperatorRepository;
    this.portalOrganisationAccessor = portalOrganisationAccessor;
  }

  @Transactional
  public ProjectOperator createOrUpdateProjectOperator(ProjectDetail projectDetail,
                                                       ProjectOperatorForm form) {
    final var projectOperator = getProjectOperatorByProjectDetail(projectDetail)
        .orElse(new ProjectOperator(projectDetail));

    final var operatorOrganisationGroup = portalOrganisationAccessor.getOrganisationGroupOrError(
        Integer.parseInt(form.getOperator())
    );
    projectOperator.setOrganisationGroup(operatorOrganisationGroup);

    projectOperator.setIsPublishedAsOperator(form.isPublishedAsOperator());

    final var publishableOrganisation =
        (BooleanUtils.isFalse(projectOperator.isPublishedAsOperator()) && form.getPublishableOrganisation() != null)
        ? portalOrganisationAccessor.getOrganisationUnitOrError(Integer.parseInt(form.getPublishableOrganisation()))
        : null;

    projectOperator.setPublishableOrganisationUnit(publishableOrganisation);

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

    var projectOperator = getProjectOperatorByProjectDetailOrError(detail);

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

  public ProjectOperator getProjectOperatorByProjectDetailOrError(ProjectDetail projectDetail) {
    return getProjectOperatorByProjectDetail(projectDetail)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("Unable to find ProjectOperator for projectDetail with ID %s", projectDetail.getId())));
  }

  public Optional<ProjectOperator> getProjectOperatorByProjectAndVersion(Project project, Integer version) {
    return projectOperatorRepository.findByProjectDetail_ProjectAndProjectDetail_Version(project, version);
  }

  public void deleteProjectOperatorByProjectDetail(ProjectDetail projectDetail) {
    projectOperatorRepository.deleteByProjectDetail(projectDetail);
  }

  public boolean isUserInMultipleTeams(AuthenticatedUserAccount user) {
    return teamService.getOrganisationTeamsPersonIsMemberOf(user.getLinkedPerson()).size() > 1;
  }

}
