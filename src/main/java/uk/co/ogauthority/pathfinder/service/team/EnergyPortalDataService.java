package uk.co.ogauthority.pathfinder.service.team;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static uk.co.ogauthority.pathfinder.model.team.Role.TEAM_ADMINISTRATOR_ROLE_NAME;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportal.serviceproviders.epmq.ScopeType;
import uk.co.fivium.energyportal.serviceproviders.epmq.messages.ServiceProviderTeamDto;
import uk.co.fivium.energyportal.serviceproviders.epmq.messages.ServiceProviderTeamTypeRoleDto;
import uk.co.fivium.energyportal.serviceproviders.epmq.messages.ServiceProviderUserTeamRolesDto;
import uk.co.fivium.energyportal.starter.serviceproviders.EnergyPortalServiceProviderDataService;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamTypeRoleDto;
import uk.co.ogauthority.pathfinder.energyportal.service.team.PortalTeamAccessor;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.team.TeamType;

@Service
class EnergyPortalDataService implements EnergyPortalServiceProviderDataService {

  private final TeamDtoFactory teamDtoFactory;
  private final PortalTeamAccessor portalTeamAccessor;
  private final WebUserAccountService webUserAccountService;

  EnergyPortalDataService(
      TeamDtoFactory teamDtoFactory,
      PortalTeamAccessor portalTeamAccessor,
      WebUserAccountService webUserAccountService
  ) {
    this.teamDtoFactory = teamDtoFactory;
    this.portalTeamAccessor = portalTeamAccessor;
    this.webUserAccountService = webUserAccountService;
  }

  @Override
  public Collection<ServiceProviderTeamDto> getServiceProviderTeamDtos() {
    var portalTeamTypeToTeams = portalTeamAccessor.getAllPortalTeams()
        .stream()
        .collect(groupingBy(dto -> TeamType.findByPortalTeamType(dto.getType())));

    var organisationTeams = teamDtoFactory.createOrganisationTeamList(portalTeamTypeToTeams.get(TeamType.ORGANISATION));

    var regulator = portalTeamTypeToTeams.get(TeamType.REGULATOR).getFirst();

    var serviceProviderTeamDtos = organisationTeams
        .stream()
        .map(
            organisationTeam ->
                new ServiceProviderTeamDto(
                    String.valueOf(organisationTeam.getId()),
                    organisationTeam.getPortalOrganisationGroup().getOrgGrpId().toString(),
                    ScopeType.ORGANISATION_GROUP,
                    TeamType.ORGANISATION.name()
                )
        )
        .collect(toSet());

    serviceProviderTeamDtos.add(
        new ServiceProviderTeamDto(
            String.valueOf(regulator.getResId()),
            null,
            ScopeType.ORGANISATION_GROUP,
            TeamType.REGULATOR.name()
        ));

    return serviceProviderTeamDtos;
  }

  @Override
  public Map<String, Collection<ServiceProviderTeamTypeRoleDto>> getTeamTypeToServiceProviderTeamTypeRoleDtos() {
    Map<String, List<PortalTeamTypeRoleDto>> portalTeamTypeToRoleDto = portalTeamAccessor.getAllPortalTeamTypeRoles()
        .stream()
        .collect(groupingBy(PortalTeamTypeRoleDto::rolePortalTeamType));

    return portalTeamTypeToRoleDto.entrySet()
        .stream()
        .collect(Collectors.toMap(
            entry -> TeamType.findByPortalTeamType(entry.getKey()).name(),
            entry -> entry.getValue()
                .stream()
                .map(roleDto -> new ServiceProviderTeamTypeRoleDto(
                    roleDto.roleName(),
                    roleDto.roleTitle(),
                    roleDto.roleDescription().replace(" (%s)".formatted(roleDto.roleTitle()), ""),
                    Objects.equals(roleDto.roleName(), TEAM_ADMINISTRATOR_ROLE_NAME),
                    roleDto.roleDisplaySequence()
                ))
                .collect(toSet())));
  }

  @Override
  public Collection<String> getTeamTypes() {
    return Arrays.stream(TeamType.values())
        .map(TeamType::name)
        .collect(toSet());
  }

  @Override
  public Collection<ServiceProviderUserTeamRolesDto> getServiceProviderUserTeamRolesDtos() {
    var personTeamRoles = portalTeamAccessor.getAllPersonTeamRoles();
    var personIds = personTeamRoles.stream().map(PersonTeamRoleDto::personId).collect(toSet());

    var webUserAccountsByLinkedPersonId = webUserAccountService.findAllByPersonIdIn(personIds)
        .stream()
        .collect(toMap(webUserAccount -> webUserAccount.getLinkedPerson().getId().asInt(), Function.identity()));

    return personTeamRoles.stream()
        .filter(dto -> webUserAccountsByLinkedPersonId.containsKey(dto.personId()))
        .collect(groupingBy(dto -> new PersonTeamDto(dto.personId(), dto.resId())))
        .entrySet()
        .stream()
        .map(entry -> new ServiceProviderUserTeamRolesDto(
            webUserAccountsByLinkedPersonId.get(entry.getKey().personId()).getWuaId(),
            String.valueOf(entry.getKey().resId()),
            TeamType.findByPortalTeamType(entry.getValue().getFirst().portalTeamType()).name(),
            entry.getValue().stream().map(PersonTeamRoleDto::role).collect(toSet())
        ))
        .collect(toSet());
  }
}