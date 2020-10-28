package uk.co.ogauthority.pathfinder.energyportal.service.organisation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationUnit;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationUnitDetail;
import uk.co.ogauthority.pathfinder.energyportal.repository.organisation.PortalOrganisationGroupRepository;
import uk.co.ogauthority.pathfinder.energyportal.repository.organisation.PortalOrganisationUnitDetailRepository;
import uk.co.ogauthority.pathfinder.energyportal.repository.organisation.PortalOrganisationUnitRepository;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.dto.organisation.OrganisationUnitDetailDto;
import uk.co.ogauthority.pathfinder.model.dto.organisation.OrganisationUnitId;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class PortalOrganisationAccessorTest {

  @Mock
  private PortalOrganisationGroupRepository organisationGroupRepository;

  @Mock
  private PortalOrganisationUnitRepository organisationUnitRepository;

  @Mock
  private PortalOrganisationUnitDetailRepository organisationUnitDetailRepository;

  private PortalOrganisationAccessor portalOrganisationAccessor;

  private PortalOrganisationUnit organisationUnit;

  private PortalOrganisationUnitDetail organisationUnitDetail;

  private PortalOrganisationGroup organisationGroup;

  @Before
  public void setup() {
    portalOrganisationAccessor = new PortalOrganisationAccessor(
        organisationGroupRepository,
        organisationUnitRepository,
        organisationUnitDetailRepository
    );

    organisationGroup = TeamTestingUtil.generateOrganisationGroup(1, "name", "short name");

    organisationUnit = TeamTestingUtil.generateOrganisationUnit(
        1,
        "unit name",
        organisationGroup
    );

    organisationUnitDetail = TeamTestingUtil.generateOrganisationUnitDetail(
        2,
        organisationUnit,
        "address",
        "number"
    );
  }

  @Test
  public void getOrganisationUnitById_whenNotFound_thenEmptyOptional() {
    final var organisationUnitId = 1;
    when(organisationUnitRepository.findById(organisationUnitId)).thenReturn(Optional.empty());

    var result = portalOrganisationAccessor.getOrganisationUnitById(organisationUnitId);
    assertThat(result).isEmpty();
  }

  @Test
  public void getOrganisationUnitById_whenFound_thenReturned() {

    when(organisationUnitRepository.findById(organisationUnit.getOuId())).thenReturn(Optional.of(organisationUnit));

    var result = portalOrganisationAccessor.getOrganisationUnitById(organisationUnit.getOuId());
    assertThat(result).contains(organisationUnit);
  }

  @Test
  public void findOrganisationUnitsWhereNameContains_whenMatch_thenListPopulated() {

    final var searchTerm = "searchTerm";

    when(organisationUnitRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(
        List.of(organisationUnit)
    );

    var result = portalOrganisationAccessor.findOrganisationUnitsWhereNameContains(searchTerm);

    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(organisationUnit);
  }

  @Test
  public void findOrganisationUnitsWhereNameContains_whenNoMatch_thenEmptyList() {

    final var searchTerm = "searchTerm";

    when(organisationUnitRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(List.of());

    var result = portalOrganisationAccessor.findOrganisationUnitsWhereNameContains(searchTerm);
    assertThat(result).isEmpty();
  }

  @Test
  public void getAllOrganisationUnits_whenResults_thenPopulatedList() {
    when(organisationUnitRepository.findAll()).thenReturn(List.of(organisationUnit));
    var result = portalOrganisationAccessor.getAllOrganisationUnits();
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(organisationUnit);
  }

  @Test
  public void getAllOrganisationUnits_whenNoResults_thenEmptyList() {
    when(organisationUnitRepository.findAll()).thenReturn(List.of());
    var result = portalOrganisationAccessor.getAllOrganisationUnits();
    assertThat(result).isEmpty();
  }

  @Test
  public void getOrganisationUnitsByIdIn_whenResults_thenPopulatedList() {

    final var listOfIds = List.of(organisationUnit.getOuId());
    when(organisationUnitRepository.findAllById(listOfIds)).thenReturn(List.of(organisationUnit));

    var result = portalOrganisationAccessor.getOrganisationUnitsByIdIn(listOfIds);

    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(organisationUnit);
  }

  @Test
  public void getOrganisationUnitsByIdIn_whenNoResults_thenEmptyList() {

    final var listOfIds = List.of(organisationUnit.getOuId());
    when(organisationUnitRepository.findAllById(listOfIds)).thenReturn(List.of());

    var result = portalOrganisationAccessor.getOrganisationUnitsByIdIn(listOfIds);
    assertThat(result).isEmpty();
  }

  @Test
  public void getOrganisationUnitsByOrganisationUnitIdIn_whenResults_thenPopulatedList() {

    when(organisationUnitRepository.findAllById(any())).thenReturn(List.of(organisationUnit));

    final var listOfOrgUnitIds = List.of(OrganisationUnitId.from(organisationUnit));
    var result = portalOrganisationAccessor.getOrganisationUnitsByOrganisationUnitIdIn(listOfOrgUnitIds);

    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(organisationUnit);
  }

  @Test
  public void getOrganisationUnitsByOrganisationUnitIdIn_whenNoResults_thenEmptyList() {

    when(organisationUnitRepository.findAllById(any())).thenReturn(List.of());

    final var listOfOrgUnitIds = List.of(OrganisationUnitId.from(organisationUnit));
    var result = portalOrganisationAccessor.getOrganisationUnitsByOrganisationUnitIdIn(listOfOrgUnitIds);

    assertThat(result).isEmpty();
  }

  @Test
  public void getOrganisationUnitDetails_whenFound_thenReturn() {

    var organisationUnitList = List.of(organisationUnit);
    when(organisationUnitDetailRepository.findByOrganisationUnitIn(organisationUnitList)).thenReturn(
        List.of(organisationUnitDetail)
    );

    var result = portalOrganisationAccessor.getOrganisationUnitDetails(organisationUnitList);
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(organisationUnitDetail);
  }

  @Test
  public void getOrganisationUnitDetails_whenNotFound_thenEmptyList() {

    var organisationUnitList = List.of(organisationUnit);
    when(organisationUnitDetailRepository.findByOrganisationUnitIn(organisationUnitList)).thenReturn(
        List.of()
    );

    var result = portalOrganisationAccessor.getOrganisationUnitDetails(organisationUnitList);
    assertThat(result).isEmpty();
  }

  @Test
  public void getOrganisationUnitDetailDtos_whenFound_thenReturnPopulatedList() {
    var organisationUnitList = List.of(organisationUnit);
    when(organisationUnitDetailRepository.findByOrganisationUnitIn(organisationUnitList)).thenReturn(
        List.of(organisationUnitDetail)
    );

    var result = portalOrganisationAccessor.getOrganisationUnitDetailDtos(organisationUnitList);

    assertThat(result).hasSize(1);

    var organisationUnitDetailDto = OrganisationUnitDetailDto.from(organisationUnitDetail);
    var resultingOrganisationUnitDetailDto = result.get(0);
    assertThat(resultingOrganisationUnitDetailDto.getOrgUnitId()).isEqualTo(organisationUnitDetailDto.getOrgUnitId());
  }

  @Test
  public void getOrganisationUnitDetailDtos_whenNotFound_thenReturnEmptyList() {
    var organisationUnitList = List.of(organisationUnit);

    when(organisationUnitDetailRepository.findByOrganisationUnitIn(any())).thenReturn(List.of());

    var result = portalOrganisationAccessor.getOrganisationUnitDetailDtos(organisationUnitList);
    assertThat(result).isEmpty();
  }

  @Test
  public void getOrganisationUnitDetailDtosByOrganisationUnitId_whenFound_thenReturnPopulatedList() {
    final var organisationUnitId = 1;
    final var organisationUnitIds = List.of(new OrganisationUnitId(organisationUnitId));

    when(organisationUnitDetailRepository.findByOrganisationUnit_ouIdIn(Set.of(organisationUnitId))).thenReturn(
        List.of(organisationUnitDetail)
    );

    var result = portalOrganisationAccessor.getOrganisationUnitDetailDtosByOrganisationUnitId(organisationUnitIds);
    var expectedOrganisationUnitDetailDto = OrganisationUnitDetailDto.from(organisationUnitDetail);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getOrgUnitId()).isEqualTo(expectedOrganisationUnitDetailDto.getOrgUnitId());
  }

  @Test
  public void getOrganisationUnitDetailDtosByOrganisationUnitId_whenNotFound_thenReturnEmptyList() {
    final var organisationUnitId = 1;
    final var organisationUnitIds = List.of(new OrganisationUnitId(organisationUnitId));

    when(organisationUnitDetailRepository.findByOrganisationUnit_ouIdIn(Set.of(organisationUnitId))).thenReturn(
        List.of()
    );

    var result = portalOrganisationAccessor.getOrganisationUnitDetailDtosByOrganisationUnitId(organisationUnitIds);
    assertThat(result).isEmpty();
  }

  @Test
  public void getAllOrganisationGroups_whenFound_thenPopulateList() {
    when(organisationGroupRepository.findAll()).thenReturn(List.of(organisationGroup));
    var result = portalOrganisationAccessor.getAllOrganisationGroups();
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(organisationGroup);
  }

  @Test
  public void getAllOrganisationGroups_whenNotFound_thenEmptyList() {
    when(organisationGroupRepository.findAll()).thenReturn(List.of());
    var result = portalOrganisationAccessor.getAllOrganisationGroups();
    assertThat(result).isEmpty();
  }

  @Test
  public void getAllOrganisationGroupsWithUrefIn_whenFound_thenPopulateList() {
    var urefs = List.of("123UREF");
    when(organisationGroupRepository.findByUrefValueIn(urefs)).thenReturn(List.of(organisationGroup));
    var result = portalOrganisationAccessor.getAllOrganisationGroupsWithUrefIn(urefs);
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(organisationGroup);
  }

  @Test
  public void getAllOrganisationGroupsWithUrefIn_whenNotFound_thenEmptyList() {
    var urefs = List.of("123UREF");
    when(organisationGroupRepository.findByUrefValueIn(urefs)).thenReturn(List.of());
    var result = portalOrganisationAccessor.getAllOrganisationGroupsWithUrefIn(urefs);
    assertThat(result).isEmpty();
  }

  @Test
  public void getOrganisationGroupById_whenFound_thenReturn() {

    final var organisationGroupId = 1;
    when(organisationGroupRepository.findById(organisationGroupId)).thenReturn(Optional.of(organisationGroup));

    var result = portalOrganisationAccessor.getOrganisationGroupById(organisationGroupId);
    assertThat(result).contains(organisationGroup);
  }

  @Test
  public void getOrganisationGroupById_whenNotFound_thenReturnEmptyOptional() {

    final var organisationGroupId = 1;
    when(organisationGroupRepository.findById(organisationGroupId)).thenReturn(Optional.empty());

    var result = portalOrganisationAccessor.getOrganisationGroupById(organisationGroupId);
    assertThat(result).isEmpty();
  }

  @Test
  public void getOrganisationUnitsForOrganisationGroupsIn_whenResults_thenReturnPopulatedList() {

    var organisationGroups = List.of(organisationGroup);

    when(organisationUnitRepository.findByPortalOrganisationGroupIn(organisationGroups)).thenReturn(List.of(organisationUnit));

    var result = portalOrganisationAccessor.getOrganisationUnitsForOrganisationGroupsIn(organisationGroups);

    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(organisationUnit);
  }

  @Test
  public void getOrganisationUnitsForOrganisationGroupsIn_whenNoResults_thenReturnEmptyList() {

    var organisationGroups = List.of(organisationGroup);

    when(organisationUnitRepository.findByPortalOrganisationGroupIn(organisationGroups)).thenReturn(List.of());

    var result = portalOrganisationAccessor.getOrganisationUnitsForOrganisationGroupsIn(organisationGroups);

    assertThat(result).isEmpty();
  }

  @Test
  public void findOrganisationGroupsWhereNameContains_whenResult_thenReturn() {
    final var searchTerm = "search term";
    when(organisationGroupRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(List.of(organisationGroup));

    var result = portalOrganisationAccessor.findOrganisationGroupsWhereNameContains(searchTerm);

    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(organisationGroup);
  }

  @Test
  public void findOrganisationGroupsWhereNameContains_whenNoResult_thenEmptyOptional() {
    final var searchTerm = "search term";
    when(organisationGroupRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(List.of());

    var result = portalOrganisationAccessor.findOrganisationGroupsWhereNameContains(searchTerm);

    assertThat(result).isEmpty();
  }

  @Test
  public void getOrganisationGroupOrError_whenFound_thenReturn() {
    final var organisationGroupId = 1;
    when(organisationGroupRepository.findById(organisationGroupId)).thenReturn(Optional.of(organisationGroup));

    var result = portalOrganisationAccessor.getOrganisationGroupOrError(organisationGroupId);
    assertThat(result).isEqualTo(organisationGroup);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getOrganisationGroupOrError_whenNotFound_thenException() {
    final var organisationGroupId = 1;
    when(organisationGroupRepository.findById(organisationGroupId)).thenReturn(Optional.empty());

    portalOrganisationAccessor.getOrganisationGroupOrError(organisationGroupId);
  }

  @Test
  public void getOrganisationGroupsWhereIdIn_whenResults_thenPopulatedList() {
    final var organisationGroupIdList = List.of(1);
    when(organisationGroupRepository.findAllById(organisationGroupIdList)).thenReturn(List.of(organisationGroup));

    var result = portalOrganisationAccessor.getOrganisationGroupsWhereIdIn(organisationGroupIdList);
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(organisationGroup);
  }

  @Test
  public void getOrganisationGroupsWhereIdIn_whenNoResults_thenEmptyList() {
    final var organisationGroupIdList = List.of(1);
    when(organisationGroupRepository.findAllById(organisationGroupIdList)).thenReturn(List.of());

    var result = portalOrganisationAccessor.getOrganisationGroupsWhereIdIn(organisationGroupIdList);
    assertThat(result).isEmpty();
  }
}