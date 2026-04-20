package uk.co.ogauthority.pathfinder.energyportal.service.user;

import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.organisationgroup.OrganisationGroupDto;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.organisationgroup.OrganisationGroupQueryService;
import uk.co.ogauthority.pathfinder.model.team.Team;
import uk.co.ogauthority.pathfinder.model.team.TeamType;

@Service
public class AllowedDomainService {

  private final OrganisationGroupQueryService organisationGroupQueryService;

  AllowedDomainService(OrganisationGroupQueryService organisationGroupQueryService) {
    this.organisationGroupQueryService = organisationGroupQueryService;
  }

  public boolean isAllowedDomain(String userEmail, Team team) {
    Optional<OrganisationGroupDto> group;
    switch (team.getType()) {
      case TeamType.ORGANISATION -> group = organisationGroupQueryService
          .getOrganisationGroupById(team.getId());
      case TeamType.REGULATOR -> group = organisationGroupQueryService.getRegulatorOrganisationGroup();
      default -> throw new IllegalStateException("Unexpected value: " + team.getType());
    }

    return group.stream()
        .flatMap(orgGroup -> orgGroup.emailDomains().stream())
        .map(String::toLowerCase)
        .anyMatch(domain -> userEmail.toLowerCase().endsWith('@' + domain));
  }
}
