package uk.co.ogauthority.pathfinder.energyportal.service.organisation.organisationgroup;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportal.starter.configuration.WellKnownOrganisationGroupsConfigurationProperties;
import uk.co.fivium.energyportalapi.client.RequestPurpose;
import uk.co.fivium.energyportalapi.client.organisation.OrganisationApi;
import uk.co.fivium.energyportalapi.generated.client.OrganisationGroupProjectionRoot;
import uk.co.fivium.energyportalapi.generated.client.OrganisationGroupsProjectionRoot;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroup;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroupEmailDomain;

@ExtendWith(MockitoExtension.class)
class OrganisationGroupQueryServiceTest {

  @Mock
  private OrganisationApi organisationApi;

  @InjectMocks
  private OrganisationGroupQueryService organisationGroupQueryService;

  private List<OrganisationGroup> groupList;

  @Mock
  private WellKnownOrganisationGroupsConfigurationProperties wellKnownGroups;

  @Mock
  private WellKnownOrganisationGroupsConfigurationProperties.WellKnownOrgGroup nsta;

  @BeforeEach
  void setup() {
    groupList = List.of(
        OrganisationGroup.newBuilder()
            .organisationGroupId(1)
            .name("Company 1")
            .emailDomains(List.of(OrganisationGroupEmailDomain.newBuilder().domain("company1.com").build()))
            .build(),
        OrganisationGroup.newBuilder()
            .organisationGroupId(2)
            .name("Company 2")
            .emailDomains(List.of(OrganisationGroupEmailDomain.newBuilder().domain("company2.com").build()))
            .build());
  }

  @Test
  void getOrganisationGroupsByName() {
    var searchTerm = "company";

    when(organisationApi.searchOrganisationGroups(
        eq(searchTerm),
        eq(OrganisationGroupQueryService.ORGANISATION_GROUPS_PROJECTION_ROOT),
        any(RequestPurpose.class)
    )).thenReturn(groupList);

    var organisationGroups =
        organisationGroupQueryService.getOrganisationGroupsByName(searchTerm);

    var argumentCaptor = ArgumentCaptor.forClass(OrganisationGroupsProjectionRoot.class);

    verify(organisationApi).searchOrganisationGroups(
        eq(searchTerm),
        argumentCaptor.capture(),
        any(RequestPurpose.class)
    );

    assertThat(argumentCaptor.getValue().getFields()).containsKeys("organisationGroupId", "name");
    assertThat(organisationGroups)
        .extracting(
            OrganisationGroupDto::organisationGroupId,
            OrganisationGroupDto::organisationGroupName
        )
        .containsExactly(
            tuple(
                groupList.get(0).getOrganisationGroupId(),
                groupList.get(0).getName()
            ),
            tuple(
                groupList.get(1).getOrganisationGroupId(),
                groupList.get(1).getName()
            )
        );
  }

  @Test
  void getOrganisationGroupById_verifyCallsApiWithCorrectParameters() {
    var argumentCaptor = ArgumentCaptor
        .forClass(OrganisationGroupProjectionRoot.class);
    var organisationGroup = new OrganisationGroup(
        1,
        "Royal Dutch Shell",
        "Shell",
        "shell.com",
        "ACTIVE",
        Collections.emptyList(),
        Collections.emptyList());


    when(organisationApi.findOrganisationGroup(
        eq(organisationGroup.getOrganisationGroupId()),
        eq(OrganisationGroupQueryService.ORGANISATION_GROUP_PROJECTION_ROOT),
        eq(new RequestPurpose("getOrganisationGroupById"))))
        .thenReturn(Optional.of(organisationGroup));

    organisationGroupQueryService.getOrganisationGroupById(1);

    verify(organisationApi).findOrganisationGroup(
        eq(organisationGroup.getOrganisationGroupId()),
        argumentCaptor.capture(),
        any(RequestPurpose.class));

    assertThat(argumentCaptor.getValue().getFields())
        .containsOnlyKeys("organisationGroupId", "name", "emailDomains");
  }

  @Test
  void getOrganisationGroupsByIds_whenOne_verifyApiCallsAndReturn() {
    var organisationGroup = new OrganisationGroup(
        1,
        "Royal Dutch Shell",
        "Shell",
        "shell.com",
        "ACTIVE",
        Collections.emptyList(),
        Collections.emptyList());

    when(organisationApi.getAllOrganisationGroupsByIds(
        eq(List.of(organisationGroup.getOrganisationGroupId())),
        eq(OrganisationGroupQueryService.ORGANISATION_GROUPS_UNITS_PROJECTION_ROOT),
        any(RequestPurpose.class)))
        .thenReturn(List.of(organisationGroup));

    var returnedOrganisations = organisationGroupQueryService
        .getOrganisationGroupsByIds(List.of(1));

    verify(organisationApi).getAllOrganisationGroupsByIds(
        eq(List.of(organisationGroup.getOrganisationGroupId())),
        eq(OrganisationGroupQueryService.ORGANISATION_GROUPS_UNITS_PROJECTION_ROOT),
        any(RequestPurpose.class));

    assertThat(returnedOrganisations)
        .containsExactly(organisationGroup);

  }

  @Test
  void getOrganisationGroupsByIds_whenTwo_verifyApiCallsAndReturn() {
    var organisationGroup = new OrganisationGroup(
        1,
        "Royal Dutch Shell",
        "Shell",
        "shell.com",
        "ACTIVE",
        Collections.emptyList(),
        Collections.emptyList());

    var organisationGroup2 = new OrganisationGroup(
        2,
        "BP",
        "BP",
        "bp.com",
        "ACTIVE",
        Collections.emptyList(),
        Collections.emptyList());

    when(organisationApi.getAllOrganisationGroupsByIds(
        eq(List.of(organisationGroup.getOrganisationGroupId(), organisationGroup2.getOrganisationGroupId())),
        eq(OrganisationGroupQueryService.ORGANISATION_GROUPS_UNITS_PROJECTION_ROOT),
        any(RequestPurpose.class)))
        .thenReturn(List.of(organisationGroup, organisationGroup2));

    var returnedOrganisations = organisationGroupQueryService
        .getOrganisationGroupsByIds(List.of(1, 2));


    assertThat(returnedOrganisations)
        .containsExactly(organisationGroup, organisationGroup2);
  }

  @Test
  void getRegulatorOrganisationGroup() {
    var expectedId = Math.toIntExact(10001L);

    when(wellKnownGroups.nsta()).thenReturn(nsta);
    when(nsta.idAsInteger()).thenReturn(expectedId);

    organisationGroupQueryService.getRegulatorOrganisationGroup();

    verify(organisationApi).findOrganisationGroup(
        eq(expectedId),
        any(),
        any(RequestPurpose.class)
    );
  }
}
