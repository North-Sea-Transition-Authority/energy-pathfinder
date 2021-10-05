package uk.co.ogauthority.pathfinder.energyportal.service.organisation;


import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationUnit;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationUnitDetail;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.team.PortalTeamUsagePurpose;
import uk.co.ogauthority.pathfinder.energyportal.repository.organisation.PortalOrganisationGroupRepository;
import uk.co.ogauthority.pathfinder.energyportal.repository.organisation.PortalOrganisationUnitDetailRepository;
import uk.co.ogauthority.pathfinder.energyportal.repository.organisation.PortalOrganisationUnitRepository;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.dto.organisation.OrganisationUnitDetailDto;
import uk.co.ogauthority.pathfinder.model.dto.organisation.OrganisationUnitId;
import uk.co.ogauthority.pathfinder.model.team.TeamType;

/**
 * API to interact with Portal Organisations. This service should not be polluted with business logic, and
 * should simply perform Read operations.
 */
@Service
public class PortalOrganisationAccessor {
  private final PortalOrganisationGroupRepository organisationGroupRepository;
  private final PortalOrganisationUnitRepository organisationUnitRepository;
  private final PortalOrganisationUnitDetailRepository organisationUnitDetailRepository;

  @Autowired
  public PortalOrganisationAccessor(
      PortalOrganisationGroupRepository organisationGroupRepository,
      PortalOrganisationUnitRepository organisationUnitRepository,
      PortalOrganisationUnitDetailRepository organisationUnitDetailRepository) {
    this.organisationGroupRepository = organisationGroupRepository;
    this.organisationUnitRepository = organisationUnitRepository;
    this.organisationUnitDetailRepository = organisationUnitDetailRepository;
  }

  /**
   * Find an organisation unit with matching ouId.
   *
   * @param ouId search org unit id
   * @return portal organisation unit wrapped in optional
   */
  public Optional<PortalOrganisationUnit> getOrganisationUnitById(Integer ouId) {
    return organisationUnitRepository.findById(ouId);
  }

  public PortalOrganisationUnit getOrganisationUnitOrError(Integer portalOrganisationUnitId) {
    return getOrganisationUnitById(portalOrganisationUnitId).orElseThrow(
        () -> new PathfinderEntityNotFoundException(
            String.format("Could not find PortalOrganisationUnit with ID %d", portalOrganisationUnitId)
        )
    );
  }

  /**
   * Return a list of active organisation units where the search term is contained within the organisation name.
   *
   * @param searchTerm find org units with name containing this string
   * @return active organisation units where the organisation name includes the search term.
   */
  public List<PortalOrganisationUnit> findActiveOrganisationUnitsWhereNameContains(String searchTerm) {
    return organisationUnitRepository.findByNameContainingIgnoreCaseAndActiveTrue(searchTerm);
  }

  /**
   * Returns a list of all organisation units.
   */
  public List<PortalOrganisationUnit> getAllOrganisationUnits() {
    return organisationUnitRepository.findAll();
  }

  /**
   * Returns a list of Organisation units whose ouId matches a value in the param list.
   */
  public List<PortalOrganisationUnit> getOrganisationUnitsByIdIn(Iterable<Integer> organisationUnitList) {
    return IterableUtils.toList(organisationUnitRepository.findAllById(organisationUnitList));
  }

  /**
   * Returns a list of Organisation units whose ouId matches a value in the param list.
   */
  public List<PortalOrganisationUnit> getOrganisationUnitsByOrganisationUnitIdIn(
      Iterable<OrganisationUnitId> organisationUnitList) {
    var integerIdList = IterableUtils.toList(organisationUnitList)
        .stream().map(OrganisationUnitId::asInt)
        .collect(toList());
    return getOrganisationUnitsByIdIn(integerIdList);
  }

  public List<PortalOrganisationUnitDetail> getOrganisationUnitDetails(List<PortalOrganisationUnit> unit) {
    return organisationUnitDetailRepository.findByOrganisationUnitIn(unit);
  }

  public List<OrganisationUnitDetailDto> getOrganisationUnitDetailDtos(List<PortalOrganisationUnit> organisationUnits) {
    return organisationUnitDetailRepository.findByOrganisationUnitIn(organisationUnits)
        .stream()
        .map(OrganisationUnitDetailDto::from)
        .collect(Collectors.toList());
  }

  public List<OrganisationUnitDetailDto> getOrganisationUnitDetailDtosByOrganisationUnitId(
      Collection<OrganisationUnitId> organisationUnitIds) {

    var idsAsInts = organisationUnitIds.stream()
        .map(OrganisationUnitId::asInt)
        .collect(toSet());

    return organisationUnitDetailRepository.findByOrganisationUnit_ouIdIn(idsAsInts)
        .stream()
        .map(OrganisationUnitDetailDto::from)
        .collect(Collectors.toList());
  }

  /**
   * Return a list of all organisation groups.
   */
  public List<PortalOrganisationGroup> getAllOrganisationGroups() {
    return IterableUtils.toList(organisationGroupRepository.findAll());
  }

  /**
   * Return a list of organisation groups who have a uref value matching a value in the given list.
   */
  public List<PortalOrganisationGroup> getAllOrganisationGroupsWithUrefIn(List<String> organisationGroupUref) {
    return organisationGroupRepository.findByUrefValueIn(organisationGroupUref);
  }

  /**
   * Return a list of organisation groups where their org grp id matches a value in the given list.
   */
  public List<PortalOrganisationGroup> getOrganisationGroupsWhereIdIn(List<Integer> organisationGroupId) {
    return IterableUtils.toList(organisationGroupRepository.findAllById(organisationGroupId));
  }

  public Optional<PortalOrganisationGroup> getOrganisationGroupById(Integer id) {
    return organisationGroupRepository.findById(id);
  }

  /**
   * Returns a list of active organisation units which belong to organisation groups in the provided list.
   */
  public List<PortalOrganisationUnit> getActiveOrganisationUnitsForOrganisationGroupsIn(
      List<PortalOrganisationGroup> organisationGroups
  ) {
    return organisationUnitRepository.findByActiveTrueAndPortalOrganisationGroupIn(organisationGroups);
  }

  public List<PortalOrganisationGroup> findOrganisationGroupsWhereNameContains(String searchTerm) {
    return organisationGroupRepository.findByNameContainingIgnoreCase(searchTerm);
  }

  /**
   * Get the PortalOrganisationGroup for the specified id, error if it does not exist.
   * @param orgGrpId id of the PortalOrganisationGroup
   * @return the PortalOrganisationGroup with the specified id
   */
  public PortalOrganisationGroup getOrganisationGroupOrError(Integer orgGrpId) {
    return getOrganisationGroupById(orgGrpId)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("unable to find organisation group with id %d", orgGrpId))
        );
  }

  /**
   * Get a list of PortalOrganisationGroup which have a portal team of type teamType.
   * @param teamType The type of team organisations need to have to be returned
   * @return a list of PortalOrganisationGroup which have a portal team of type teamType
   */
  public List<PortalOrganisationGroup> getAllOrganisationGroupsWithAssociatedTeamType(TeamType teamType) {
    return organisationGroupRepository.findByExistenceOfPortalTeam(
        teamType.getPortalTeamType(),
        PortalTeamUsagePurpose.PRIMARY_DATA
    );
  }

  public List<PortalOrganisationGroup> getAllOrganisationGroupsWithAssociatedTeamTypeAndNameContaining(TeamType teamType,
                                                                                                       String searchTerm
  ) {
    return organisationGroupRepository.findByExistenceOfPortalTeamAndNameContaining(
        teamType.getPortalTeamType(),
        PortalTeamUsagePurpose.PRIMARY_DATA,
        searchTerm
    );
  }

  /**
   * Get active organisation units where the organisation unit name contains the
   * organisationUnitName provided and is within one of the provided organisationGroups.
   * @param organisationUnitName The name of the organisation unit to filter by
   * @param organisationGroups The organisation groups the matched organisation unit must be within
   * @return a list of organisation units matching organisationUnitName and within organisationGroups
   */
  public List<PortalOrganisationUnit> getActiveOrganisationUnitsByNameAndOrganisationGroupId(
      String organisationUnitName,
      List<PortalOrganisationGroup> organisationGroups
  ) {
    return organisationUnitRepository.findByNameContainingIgnoreCaseAndActiveTrueAndPortalOrganisationGroupIn(
        organisationUnitName,
        organisationGroups
    );
  }

  public boolean isOrganisationUnitActiveAndPartOfOrganisationGroup(int organisationGroupId, int organisationUnitId) {
    return organisationUnitRepository.existsByOuIdAndActiveTrueAndPortalOrganisationGroup_OrgGrpId(
        organisationUnitId,
        organisationGroupId
    );
  }

}
