package uk.co.ogauthority.pathfinder.energyportal.service.organisation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamPersonMembershipDto;
import uk.co.ogauthority.pathfinder.energyportal.service.team.PortalTeamAccessor;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class PortalOrganisationGroupPersonMembershipServiceTest {

  @Mock
  private PortalTeamAccessor portalTeamAccessor;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  private PortalOrganisationGroupPersonMembershipService portalOrganisationGroupPersonMembershipService;

  @Before
  public void setup() {
    portalOrganisationGroupPersonMembershipService = new PortalOrganisationGroupPersonMembershipService(
        portalTeamAccessor,
        portalOrganisationAccessor
    );
  }

  @Test
  public void getOrganisationGroupMembershipForOrganisationGroupIdsIn_whenNoOrganisationGroupsFound_thenReturnEmptyList() {

    var invalidOrganisationGroupIdList = List.of(-1);

    var resultingOrganisationGroupMembership = portalOrganisationGroupPersonMembershipService.getOrganisationGroupMembershipForOrganisationGroupIdsIn(
        invalidOrganisationGroupIdList
    );

    assertThat(resultingOrganisationGroupMembership).isEmpty();
  }

  @Test
  public void getOrganisationGroupMembershipForOrganisationGroupIdsIn_whenOrganisationGroupsFound_thenReturnOrganisationGroupMembershipList() {

    var organisationGroupA = TeamTestingUtil.generateOrganisationGroup(
        1,
        "operator A",
        "operator short name"
    );

    var organisationATeamDto = TeamTestingUtil.portalTeamDtoFrom(TeamTestingUtil.getOrganisationTeam(10, organisationGroupA));

    var organisationGroupB = TeamTestingUtil.generateOrganisationGroup(
        2,
        "operator B",
        "operator short name"
    );

    var organisationBTeamDto = TeamTestingUtil.portalTeamDtoFrom(TeamTestingUtil.getOrganisationTeam(20, organisationGroupB));

    var organisationGroupTeamResourceIds = List.of(organisationATeamDto.getResId(), organisationBTeamDto.getResId());

    var personInOrganisationTeamA = UserTestingUtil.getPerson(1, "person A forename", "person A surname", "someone@example.com", "123");

    var personInOrganisationTeamB= UserTestingUtil.getPerson(2, "person B forename", "person B surname", "someone@example.com", "123");

    var validOrganisationGroupIdList = List.of(organisationGroupA.getOrgGrpId(), organisationGroupB.getOrgGrpId());

    when(portalOrganisationAccessor.getOrganisationGroupsWhereIdIn(validOrganisationGroupIdList)).thenReturn(
        List.of(organisationGroupA, organisationGroupB)
    );

    when(portalTeamAccessor.findPortalTeamByOrganisationGroupsIn(List.of(organisationGroupA, organisationGroupB))).thenReturn(
        List.of(organisationATeamDto, organisationBTeamDto)
    );

    var portalTeamPersonMembershipDtoMap = List.of(
        new PortalTeamPersonMembershipDto(organisationATeamDto.getResId(), personInOrganisationTeamA),
        new PortalTeamPersonMembershipDto(organisationBTeamDto.getResId(), personInOrganisationTeamB)
    );

    when(portalTeamAccessor.getPortalTeamPersonMembershipByResourceIdIn(organisationGroupTeamResourceIds)).thenReturn(
        portalTeamPersonMembershipDtoMap
    );

    var resultingOrganisationGroupMembership = portalOrganisationGroupPersonMembershipService.getOrganisationGroupMembershipForOrganisationGroupIdsIn(
        validOrganisationGroupIdList
    );

    assertThat(resultingOrganisationGroupMembership).containsExactlyInAnyOrder(
        new OrganisationGroupMembership(organisationATeamDto.getResId(), organisationGroupA, List.of(personInOrganisationTeamA)),
        new OrganisationGroupMembership(organisationBTeamDto.getResId(), organisationGroupB, List.of(personInOrganisationTeamB))
    );
  }

  @Test
  public void getOrganisationGroupMembershipForOrganisationGroupIn_thenReturnOrganisationGroupMembershipList() {

    var organisationGroupA = TeamTestingUtil.generateOrganisationGroup(
        1,
        "operator A",
        "operator short name"
    );

    var organisationATeamDto = TeamTestingUtil.portalTeamDtoFrom(TeamTestingUtil.getOrganisationTeam(10, organisationGroupA));

    var organisationGroupB = TeamTestingUtil.generateOrganisationGroup(
        2,
        "operator B",
        "operator short name"
    );

    var organisationBTeamDto = TeamTestingUtil.portalTeamDtoFrom(TeamTestingUtil.getOrganisationTeam(20, organisationGroupB));

    var organisationGroupTeamResourceIds = List.of(organisationATeamDto.getResId(), organisationBTeamDto.getResId());

    var personInOrganisationTeamA = UserTestingUtil.getPerson(1, "person A forename", "person A surname", "someone@example.com", "123");

    var personInOrganisationTeamB= UserTestingUtil.getPerson(2, "person B forename", "person B surname", "someone@example.com", "123");

    var validOrganisationGroupList = List.of(organisationGroupA, organisationGroupB);

    when(portalTeamAccessor.findPortalTeamByOrganisationGroupsIn(List.of(organisationGroupA, organisationGroupB))).thenReturn(
        List.of(organisationATeamDto, organisationBTeamDto)
    );

    var portalTeamPersonMembershipDtoMap = List.of(
        new PortalTeamPersonMembershipDto(organisationATeamDto.getResId(), personInOrganisationTeamA),
        new PortalTeamPersonMembershipDto(organisationBTeamDto.getResId(), personInOrganisationTeamB)
    );

    when(portalTeamAccessor.getPortalTeamPersonMembershipByResourceIdIn(organisationGroupTeamResourceIds)).thenReturn(
        portalTeamPersonMembershipDtoMap
    );

    var resultingOrganisationGroupMembership = portalOrganisationGroupPersonMembershipService.getOrganisationGroupMembershipForOrganisationGroupIn(
        validOrganisationGroupList
    );

    assertThat(resultingOrganisationGroupMembership).containsExactlyInAnyOrder(
        new OrganisationGroupMembership(organisationATeamDto.getResId(), organisationGroupA, List.of(personInOrganisationTeamA)),
        new OrganisationGroupMembership(organisationBTeamDto.getResId(), organisationGroupB, List.of(personInOrganisationTeamB))
    );
  }

  @Test
  public void getOrganisationGroupMembershipForOrganisationGroupIn_whenNoPeopleInOrganisationGroupTeam_thenOnlyOrganisationWithTeamMembersReturned() {

    // GIVEN an organisation group with a team member
    var organisationGroupWithMember = TeamTestingUtil.generateOrganisationGroup(
        1,
        "operator group with member name",
        "operator group with member name"
    );

    var organisationTeamWithMemberDto = TeamTestingUtil.portalTeamDtoFrom(TeamTestingUtil.getOrganisationTeam(10, organisationGroupWithMember));

    var personInOrganisationTeam = UserTestingUtil.getPerson(1, "person forename", "person surname", "someone@example.com", "123");

    var portalTeamPersonMemberships = List.of(
        new PortalTeamPersonMembershipDto(organisationTeamWithMemberDto.getResId(), personInOrganisationTeam)
    );

    // AND an organisation group with no team members
    var noMemberOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(
        2,
        "operator group with no member name",
        "operator group with no member name"
    );

    var noMemberOrganisationTeamDto = TeamTestingUtil.portalTeamDtoFrom(TeamTestingUtil.getOrganisationTeam(20, noMemberOrganisationGroup));

    var organisationGroupList = List.of(
        organisationGroupWithMember,
        noMemberOrganisationGroup
    );

    var organisationGroupTeamResourceIds = List.of(
        organisationTeamWithMemberDto.getResId(),
        noMemberOrganisationTeamDto.getResId()
    );

    when(portalTeamAccessor.findPortalTeamByOrganisationGroupsIn(organisationGroupList)).thenReturn(
        List.of(organisationTeamWithMemberDto, noMemberOrganisationTeamDto)
    );

    when(portalTeamAccessor.getPortalTeamPersonMembershipByResourceIdIn(organisationGroupTeamResourceIds)).thenReturn(portalTeamPersonMemberships);

    // WHEN we get the organisation group membership
    var resultingOrganisationGroupMembership = portalOrganisationGroupPersonMembershipService.getOrganisationGroupMembershipForOrganisationGroupIn(
        organisationGroupList
    );

    // THEN only the organisation with a team member is returned
    assertThat(resultingOrganisationGroupMembership).containsExactlyInAnyOrder(
        new OrganisationGroupMembership(
            organisationTeamWithMemberDto.getResId(),
            organisationGroupWithMember,
            List.of(personInOrganisationTeam)
        )
    );
  }
}