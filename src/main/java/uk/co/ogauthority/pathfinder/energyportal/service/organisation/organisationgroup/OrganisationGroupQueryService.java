package uk.co.ogauthority.pathfinder.energyportal.service.organisation.organisationgroup;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportal.starter.configuration.WellKnownOrganisationGroupsConfigurationProperties;
import uk.co.fivium.energyportalapi.client.RequestPurpose;
import uk.co.fivium.energyportalapi.client.organisation.OrganisationApi;
import uk.co.fivium.energyportalapi.generated.client.OrganisationGroupProjectionRoot;
import uk.co.fivium.energyportalapi.generated.client.OrganisationGroupsProjectionRoot;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroup;

@Service
public class OrganisationGroupQueryService {
  public static final OrganisationGroupsProjectionRoot ORGANISATION_GROUPS_PROJECTION_ROOT =
      new OrganisationGroupsProjectionRoot()
          .organisationGroupId()
          .name()
          .emailDomains()
          .domain()
          .root();
  public static final OrganisationGroupProjectionRoot ORGANISATION_GROUP_PROJECTION_ROOT =
      new OrganisationGroupProjectionRoot()
          .organisationGroupId()
          .name()
          .emailDomains()
          .domain()
          .root();
  public static final OrganisationGroupsProjectionRoot ORGANISATION_GROUPS_UNITS_PROJECTION_ROOT =
      ORGANISATION_GROUPS_PROJECTION_ROOT
          .organisationUnits()
          .organisationUnitId()
          .name()
          .registeredNumber()
          .isDuplicate()
          .root();

  private final OrganisationApi organisationApi;
  private final WellKnownOrganisationGroupsConfigurationProperties wellKnownOrganisationGroups;

  OrganisationGroupQueryService(
      OrganisationApi organisationApi,
      WellKnownOrganisationGroupsConfigurationProperties wellKnownOrganisationGroups
  ) {
    this.organisationApi = organisationApi;
    this.wellKnownOrganisationGroups = wellKnownOrganisationGroups;
  }

  public List<OrganisationGroupDto> getOrganisationGroupsByName(String name) {
    return organisationApi.searchOrganisationGroups(
            name,
            ORGANISATION_GROUPS_PROJECTION_ROOT,
            new RequestPurpose("getOrganisationGroupsByName")
        )
        .stream()
        .map(OrganisationGroupDto::from)
        .toList();
  }

  public List<OrganisationGroup> getOrganisationGroupsByIds(List<Integer> organisationGroupsIds) {
    return organisationApi.getAllOrganisationGroupsByIds(
        organisationGroupsIds,
        ORGANISATION_GROUPS_UNITS_PROJECTION_ROOT,
        new RequestPurpose("getOrganisationGroupsByIds")
    );
  }

  public Optional<OrganisationGroupDto> getOrganisationGroupById(Integer id) {
    return organisationApi.findOrganisationGroup(
            id,
            ORGANISATION_GROUP_PROJECTION_ROOT,
            new RequestPurpose("getOrganisationGroupById")
        )
        .map(OrganisationGroupDto::from);
  }

  public Optional<OrganisationGroupDto> getRegulatorOrganisationGroup() {
    return getOrganisationGroupById(wellKnownOrganisationGroups.nsta().idAsInteger());
  }

}