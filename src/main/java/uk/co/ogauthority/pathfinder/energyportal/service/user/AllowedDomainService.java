package uk.co.ogauthority.pathfinder.energyportal.service.user;

import java.util.List;
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
    var group = switch (team.getType()) {
      case TeamType.ORGANISATION -> organisationGroupQueryService
          .getOrganisationGroupById(team.getId());
      case TeamType.REGULATOR -> organisationGroupQueryService.getRegulatorOrganisationGroup();
    };

    var lowerEmail = userEmail.toLowerCase();
    return group.map(OrganisationGroupDto::emailDomains).orElse(List.of()).stream()
        .map(String::toLowerCase)
        .anyMatch(domain -> lowerEmail.endsWith("@" + domain));
  }
}
