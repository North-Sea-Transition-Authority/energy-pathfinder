package uk.co.ogauthority.pathfinder.service.project;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.team.OrganisationTeam;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.util.SecurityUtil;

@Service
public class ProjectSectionItemOwnershipService {

  private final ProjectOperatorService projectOperatorService;
  private final TeamService teamService;

  @Autowired
  public ProjectSectionItemOwnershipService(ProjectOperatorService projectOperatorService,
                                            TeamService teamService) {
    this.projectOperatorService = projectOperatorService;
    this.teamService = teamService;
  }

  /**
   * Check if the current user is the operator of a project or if they created something
   * related to such project (e.g. an UpcomingTender, AwardedContract, CollaborationOpportunity)
   * @param detail The project's detail
   * @param organisationGroupId The ID of the organisation group that created the piece of information that wants to be accessed
   * @return True if the user is the operator or their organisation created the piece of information
   */
  public boolean canCurrentUserAccessProjectSectionInfo(ProjectDetail detail, OrganisationGroupIdWrapper organisationGroupId) {
    var userAccount = SecurityUtil.getAuthenticatedUserFromSecurityContext()
        .orElseThrow(() -> {
          throw new PathfinderEntityNotFoundException("Could not get and authenticated user from security context");
        });
    var isProjectOperator = projectOperatorService.isUserInProjectTeam(detail, userAccount);

    if (isProjectOperator) {
      return true;
    }

    var userPortalOrganisationGroupIds = teamService.getOrganisationTeamsPersonIsMemberOf(userAccount.getLinkedPerson())
        .stream()
        .map(OrganisationTeam::getPortalOrganisationGroup)
        .map(PortalOrganisationGroup::getOrgGrpId)
        .collect(Collectors.toList());

    return userPortalOrganisationGroupIds.contains(organisationGroupId.getOrganisationGroupId());
  }
}
