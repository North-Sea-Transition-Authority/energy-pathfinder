package uk.co.ogauthority.pathfinder.energyportal.service.team;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.exception.team.PortalTeamNotFoundException;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalRoleDto;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalSystemPrivilegeDto;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamDto;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamMemberDto;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamPersonMembershipDto;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.PersonId;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.team.PortalTeam;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.team.PortalTeamTypeRole;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.team.PortalTeamUsagePurpose;
import uk.co.ogauthority.pathfinder.energyportal.repository.team.PortalTeamRepository;
import uk.co.ogauthority.pathfinder.model.team.TeamType;

@Service
public class PortalTeamAccessor {

  private final PortalTeamRepository portalTeamRepository;
  private final EntityManager entityManager;

  @Autowired
  public PortalTeamAccessor(PortalTeamRepository portalTeamRepository,
                            EntityManager entityManager) {
    this.portalTeamRepository = portalTeamRepository;
    this.entityManager = entityManager;
  }

  public Optional<PortalTeamDto> findPortalTeamById(int resId) {
    try {
      return Optional.of(entityManager.createQuery("" +
              "SELECT new uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamDto(" +
              "  pt.resId, pt.name, pt.description, ptt.type, ptu.uref " +
              ") " +
              "FROM PortalTeam pt " +
              "JOIN PortalTeamType ptt ON pt.portalTeamType = ptt " +
              "LEFT JOIN PortalTeamUsage ptu ON ptu.portalTeam = pt" +
              " " +
              "WHERE pt.resId = :resId ",
          PortalTeamDto.class)
          .setParameter("resId", resId)
          .getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }

  }

  /**
   * Given a person and a team Id get person membership details for that team.
   */
  public Optional<PortalTeamMemberDto> getPersonTeamMembership(Person person, int resId) {
    // This implementation is not optimal as it does processing for whole team before filtering to given person.
    // Need to pass optional parameter down the call stack if this is too slow.
    return getPortalTeamMembers(resId)
        .stream()
        .filter(ptm -> person.getId().equals(ptm.getPersonId()))
        .findFirst();
  }

  public List<PortalTeamMemberDto> getPortalTeamMembers(int resId) {
    PortalTeam team = portalTeamRepository.findById(resId)
        .orElseThrow(() -> new PortalTeamNotFoundException("Could not find portal team with resId:" + resId));

    return getPortalTeamMembers(team);

  }

  /**
   * Helper which transforms database results for team member roles into a list of PortalTeamMemberDto.
   */
  private List<PortalTeamMemberDto> getPortalTeamMembers(PortalTeam team) {
    Map<PersonId, Set<PortalRoleDto>> personIdToPortalTeamRoleMap = new HashMap<>();

    // simply converts the list of role results into a map where the personId of the team member is the key and the set of
    // their roles in the team is the value
    for (PortalTeamMemberRoleResult teamMemberRoleResult : getTeamMemberRoleResultsForTeam(team)) {
      PortalRoleDto roleDtoFromResult = convertTeamMemberRoleResultToRoleDto(teamMemberRoleResult);

      if (personIdToPortalTeamRoleMap.containsKey(teamMemberRoleResult.getPersonId())) {
        Set<PortalRoleDto> teamMemberRoles = personIdToPortalTeamRoleMap.get(teamMemberRoleResult.getPersonId());
        // int resId, String name, String title, String description, int displaySequence
        teamMemberRoles.add(roleDtoFromResult);
      } else {
        Set<PortalRoleDto> teamMemberRoles = new HashSet<>();
        teamMemberRoles.add(roleDtoFromResult);
        personIdToPortalTeamRoleMap.put(teamMemberRoleResult.getPersonId(), teamMemberRoles);
      }
    }

    // Using easy to process map, create PortalTeamMemberDto's where the role list of each
    List<PortalTeamMemberDto> teamMemberDtos = new ArrayList<>();
    for (Map.Entry<PersonId, Set<PortalRoleDto>> entry : personIdToPortalTeamRoleMap.entrySet()) {
      teamMemberDtos.add(new PortalTeamMemberDto(entry.getKey(), entry.getValue()));
    }

    return teamMemberDtos;
  }

  private PortalRoleDto convertTeamMemberRoleResultToRoleDto(PortalTeamMemberRoleResult teamMemberRoleResult) {
    return new PortalRoleDto(
        teamMemberRoleResult.getResId(),
        teamMemberRoleResult.getRoleName(),
        teamMemberRoleResult.getRoleTitle(),
        teamMemberRoleResult.getRoleDescription(),
        teamMemberRoleResult.getRoleDisplaySequence()
    );
  }

  /**
   * Helper to make it easier to convert data into API DTOs. This essentially creates one object per role per person in team.
   */
  private List<PortalTeamMemberRoleResult> getTeamMemberRoleResultsForTeam(PortalTeam team) {
    return entityManager.createQuery("" +
            "SELECT new uk.co.ogauthority.pathfinder.energyportal.service.team.PortalTeamMemberRoleResult(" +
            "  pt.resId, " + // team
            "  ptm.personId, " + // person
            "  pttr.name, pttr.title, pttr.description, pttr.displaySeq " + // role details
            ") " +
            "FROM PortalTeamMemberRole ptmr " +
            "JOIN PortalTeamMember ptm ON ptm = ptmr.portalTeamMember " +
            "JOIN PortalTeam pt ON pt = ptm.portalTeam " +
            "JOIN PortalTeamTypeRole pttr ON ptmr.portalTeamTypeRole = pttr " +
            "WHERE ptm.portalTeam = :portalTeam ",
        PortalTeamMemberRoleResult.class)
        .setParameter("portalTeam", team)
        .getResultList();
  }

  public List<PortalTeamDto> getPortalTeamsByPortalTeamType(String portalTeamType) {

    return entityManager.createQuery("" +
            "SELECT new uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamDto(" +
            "  pt.resId, pt.name, pt.description, pt.portalTeamType.type, ptu.uref " +
            ") " +
            "FROM PortalTeam pt " +
            "LEFT JOIN PortalTeamUsage ptu ON ptu.portalTeam = pt " +
            "WHERE pt.portalTeamType.type = :portalTeamType " +
            "AND (ptu.purpose = :usagePurpose OR ptu IS NULL)",
        PortalTeamDto.class)
        .setParameter("portalTeamType", portalTeamType)
        .setParameter("usagePurpose", PortalTeamUsagePurpose.PRIMARY_DATA)
        .getResultList();
  }

  /**
   * Get teams of a given type where some Person is a member and they have a role with matching name in that team.
   */
  public List<PortalTeamDto> getTeamsWherePersonMemberOfTeamTypeAndHasRoleMatching(Person person, String portalTeamType,
                                                                                   Collection<String> roleNames) {
    List<PortalTeamTypeRole> roles = getPortalTeamTypeRoles(
        portalTeamType,
        roleNames
    );

    return entityManager.createQuery("" +
            // Distinct required to remove duplicates caused by using the PortalTeamTypeRole entity as root
            "SELECT DISTINCT new uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamDto( " +
            "  pt.resId, pt.name, pt.description, pt.portalTeamType.type, ptu.uref " +
            ") " +
            "FROM PortalTeamTypeRole pttr " +
            "JOIN PortalTeamType ptt ON ptt = pttr.portalTeamType " +
            "JOIN PortalTeam pt ON pt.portalTeamType = ptt " +
            "JOIN PortalTeamMember ptm ON ptm.portalTeam = pt " +
            "JOIN PortalTeamMemberRole ptmr ON ptmr.portalTeamMember = ptm " +
            "LEFT JOIN PortalTeamUsage ptu ON ptu.portalTeam = pt " +
            "WHERE ptt.type = :portalTeamType " +
            "AND ptm.personId = :personId " +
            "AND (ptu.purpose = :usagePurpose OR ptu IS NULL) " +
            "AND ptmr.portalTeamTypeRole IN :portalTeamTypeRoles",
        PortalTeamDto.class)
        .setParameter("portalTeamType", portalTeamType)
        .setParameter("usagePurpose", PortalTeamUsagePurpose.PRIMARY_DATA)
        .setParameter("portalTeamTypeRoles", roles)
        .setParameter("personId", person.getId().asInt())
        .getResultList();
  }

  public List<PortalTeamDto> getTeamsWherePersonMemberOfTeamType(Person person, String portalTeamType) {
    return entityManager.createQuery("" +
            // Distinct required to remove duplicates caused by using the PortalTeamTypeRole entity as root
            "SELECT DISTINCT new uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamDto( " +
            "  pt.resId, pt.name, pt.description, pt.portalTeamType.type, ptu.uref " +
            ") " +
            "FROM PortalTeamType ptt " +
            "JOIN PortalTeam pt ON pt.portalTeamType = ptt " +
            "JOIN PortalTeamMember ptm ON ptm.portalTeam = pt " +
            "LEFT JOIN PortalTeamUsage ptu ON ptu.portalTeam = pt " +
            "WHERE ptt.type = :portalTeamType " +
            "AND ptm.personId = :personId " +
            "AND (ptu.purpose = :usagePurpose OR ptu IS NULL) ",
        PortalTeamDto.class)
        .setParameter("portalTeamType", portalTeamType)
        .setParameter("personId", person.getId().asInt())
        .setParameter("usagePurpose", PortalTeamUsagePurpose.PRIMARY_DATA)
        .getResultList();
  }

  public long getNumberOfTeamsWherePersonMemberOfTeamType(Person person,
                                                          String portalTeamType) {
    return (Long) entityManager.createQuery("" +
            "SELECT COUNT(pt) " +
            "FROM PortalTeamType ptt " +
            "JOIN PortalTeam pt ON pt.portalTeamType = ptt " +
            "JOIN PortalTeamMember ptm ON ptm.portalTeam = pt " +
            "LEFT JOIN PortalTeamUsage ptu ON ptu.portalTeam = pt " +
            "WHERE ptt.type = :portalTeamType " +
            "AND ptm.personId = :personId " +
            "AND (ptu.purpose = :usagePurpose OR ptu IS NULL)"
        )
        .setParameter("portalTeamType", portalTeamType)
        .setParameter("usagePurpose", PortalTeamUsagePurpose.PRIMARY_DATA)
        .setParameter("personId", person.getId().asInt())
        .getSingleResult();
  }

  /**
   * Get teams of a given type where the specified Person is a member, they have a role with matching name in that team
   * and the team name matches the search term.
   */
  public List<PortalTeamDto> getTeamsWherePersonMemberOfTeamWithNameLikeAndOrganisationHasRoleMatching(Person person,
                                                                                                       Collection<String> roleNames,
                                                                                                       String searchTerm) {
    List<PortalTeamTypeRole> roles = getPortalTeamTypeRoles(
        TeamType.ORGANISATION.getPortalTeamType(),
        roleNames
    );

    return entityManager.createQuery("" +
            // Distinct required to remove duplicates caused by using the PortalTeamTypeRole entity as root
            "SELECT DISTINCT new uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamDto( " +
            "  pt.resId, pt.name, pt.description, pt.portalTeamType.type, ptu.uref " +
            ") " +
            "FROM PortalTeamTypeRole pttr " +
            "JOIN PortalTeamType ptt ON ptt = pttr.portalTeamType " +
            "JOIN PortalTeam pt ON pt.portalTeamType = ptt " +
            "JOIN PortalTeamMember ptm ON ptm.portalTeam = pt " +
            "JOIN PortalTeamMemberRole ptmr ON ptmr.portalTeamMember = ptm " +
            "LEFT JOIN PortalTeamUsage ptu ON ptu.portalTeam = pt " +
            "JOIN PortalOrganisationGroup pog ON pog.urefValue = ptu.uref " +
            "WHERE ptt.type = :portalTeamType " +
            "AND ptm.personId = :personId " +
            "AND (ptu.purpose = :usagePurpose OR ptu IS NULL) " +
            "AND ptmr.portalTeamTypeRole IN :portalTeamTypeRoles " +
            "AND LOWER(pog.name) LIKE LOWER(concat('%',:searchTerm, '%')) ",
            PortalTeamDto.class)
        .setParameter("portalTeamType", TeamType.ORGANISATION.getPortalTeamType())
        .setParameter("usagePurpose", PortalTeamUsagePurpose.PRIMARY_DATA)
        .setParameter("portalTeamTypeRoles", roles)
        .setParameter("personId", person.getId().asInt())
        .setParameter("searchTerm", searchTerm)
        .getResultList();
  }

  private List<PortalTeamTypeRole> getPortalTeamTypeRoles(String portalTeamType, Collection<String> roleNames) {
    return entityManager.createQuery("" +
            "SELECT pttr " +
            "FROM PortalTeamType ptt " +
            "JOIN PortalTeamTypeRole pttr ON pttr.portalTeamType = ptt " +
            "WHERE ptt.type = :portalTeamType " +
            "AND pttr.name IN :roleNames ",
        PortalTeamTypeRole.class)
        .setParameter("portalTeamType", portalTeamType)
        .setParameter("roleNames", roleNames)
        .getResultList();
  }

  /**
   * Remove a given person from a team.
   *
   * @param resId id of team person being removed from
   * @param personToBeRemovedFromTeam Person who is being removed
   * @param actionPerformedBy User who is doing the removing
   */
  @Transactional
  public void removePersonFromTeam(int resId, Person personToBeRemovedFromTeam, WebUserAccount actionPerformedBy) {
    try {
      portalTeamRepository.removeUserFromTeam(resId, personToBeRemovedFromTeam.getId().asInt(), actionPerformedBy.getWuaId());
    } catch (Exception e) {
      String msg = String.format(
          "Error Removing person from team. paramSummary: resId:%s; personId:%s; actingPersonId:%s;",
          resId,
          personToBeRemovedFromTeam.getId(),
          actionPerformedBy.getWuaId()
      );
      throw new RuntimeException(msg, e);
    }
  }


  /**
   * For a team, set a person's roles within that team. Can add person to team if they are not a member already
   *
   * @param resId id of team the person being added to
   * @param person Person who is having their roles set.
   * @param actionPerformedBy User who is doing the adding
   */
  @Transactional
  public void addPersonToTeamWithRoles(int resId, Person person, Collection<String> roleNames, WebUserAccount actionPerformedBy) {
    String roleNameCsv = String.join(",", roleNames);
    try {
      portalTeamRepository.updateUserRoles(resId, roleNameCsv, person.getId().asInt(), actionPerformedBy.getWuaId());
    } catch (Exception e) {
      String msg = String.format("Error adding person to team. paramSummary: resId:%s; roleNameCSV:%s; personId:%s; actingPersonId:%s;",
          resId,
          person.getId(),
          roleNameCsv,
          actionPerformedBy.getWuaId()
      );
      throw new RuntimeException(msg, e);
    }
  }

  /**
   * Simple check to make sure some person has some role within a given team.
   */
  public boolean personIsAMemberOfTeam(int resId, Person person) {
    return !entityManager.createQuery("" +
            "SELECT 1 " +
            "FROM PortalTeamMember ptm " +
            "JOIN PortalTeam pt ON pt = ptm.portalTeam " +
            "WHERE pt.resId = :resId " +
            "AND ptm.personId = :personId",
        Integer.class)
        .setParameter("resId", resId)
        .setParameter("personId", person.getId().asInt())
        .getResultList()
        .isEmpty();
  }

  /**
   * Get a list of all possible roles members of a given team can have.
   */
  public List<PortalRoleDto> getAllPortalRolesForTeam(int resId) {

    return entityManager.createQuery("" +
            "SELECT new uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalRoleDto(" +
            "  pt.resId, pttr.name, pttr.title,  pttr.description, pttr.displaySeq " +
            ") " +
            "FROM PortalTeam pt " +
            "JOIN PortalTeamType ptt ON pt.portalTeamType = ptt " +
            "JOIN PortalTeamTypeRole pttr ON pttr.portalTeamType = ptt " +
            "WHERE pt.resId = :resId",
        PortalRoleDto.class)
        .setParameter("resId", resId)
        .getResultList();
  }

  public List<PortalSystemPrivilegeDto> getAllPortalSystemPrivilegesForPerson(Person person) {
    return entityManager.createQuery("" +
            // Distinct required to remove duplicates caused by using the PortalTeamTypeRole entity as root
            "SELECT DISTINCT new uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalSystemPrivilegeDto( " +
            "  pt.portalTeamType.type, pttr.name, pttrp.privilege" +
            ") " +
            "FROM PortalTeamMember ptm " +
            "JOIN PortalTeam pt ON pt = ptm.portalTeam " +
            "JOIN PortalTeamType ptt ON ptt = pt.portalTeamType " +
            "JOIN PortalTeamTypeRole pttr ON pttr.portalTeamType = ptt " +
            "JOIN PortalTeamMemberRole ptmr ON ptmr.portalTeamMember = ptm AND ptmr.portalTeamTypeRole = pttr " +
            "JOIN PortalTeamTypeRolePriv pttrp ON pttrp.portalTeamTypeRole = pttr " +
            "WHERE ptm.personId = :personId ",
        PortalSystemPrivilegeDto.class)
        .setParameter("personId", person.getId().asInt())
        .getResultList();

  }

  public Optional<PortalTeamDto> findPortalTeamByOrganisationGroup(PortalOrganisationGroup portalOrganisationGroup) {
    try {
      return Optional.of(entityManager.createQuery("" +
              "SELECT new uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamDto(" +
              "  pt.resId, pt.name, pt.description, ptt.type, ptu.uref " +
              ") " +
              "FROM PortalTeam pt " +
              "JOIN PortalTeamType ptt ON pt.portalTeamType = ptt " +
              "LEFT JOIN PortalTeamUsage ptu ON ptu.portalTeam = pt " +
              "LEFT JOIN PortalOrganisationGroup pog ON pog.urefValue = ptu.uref " +
              "WHERE ptt.type = :portalTeamType " +
              "AND pog.orgGrpId = :organisationGroupId",
          PortalTeamDto.class)
          .setParameter("portalTeamType", TeamType.ORGANISATION.getPortalTeamType())
          .setParameter("organisationGroupId", portalOrganisationGroup.getOrgGrpId())
          .getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  public List<PortalTeamDto> findPortalTeamByOrganisationGroupsIn(List<PortalOrganisationGroup> portalOrganisationGroups) {

    final var portalOrganisationGroupIds = portalOrganisationGroups
        .stream()
        .map(PortalOrganisationGroup::getOrgGrpId)
        .collect(Collectors.toList());

    return entityManager.createQuery("" +
            "SELECT new uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamDto(" +
            "  pt.resId, pt.name, pt.description, ptt.type, ptu.uref " +
            ") " +
            "FROM PortalTeam pt " +
            "JOIN PortalTeamType ptt ON pt.portalTeamType = ptt " +
            "LEFT JOIN PortalTeamUsage ptu ON ptu.portalTeam = pt " +
            "LEFT JOIN PortalOrganisationGroup pog ON pog.urefValue = ptu.uref " +
            "WHERE ptt.type = :portalTeamType " +
            "AND pog.orgGrpId IN (:organisationGroupIds)",
        PortalTeamDto.class)
        .setParameter("portalTeamType", TeamType.ORGANISATION.getPortalTeamType())
        .setParameter("organisationGroupIds", portalOrganisationGroupIds)
        .getResultList();
  }

  public List<Person> getPortalTeamMemberPeople(List<Integer> resourceIds) {
    return entityManager.createQuery("" +
            // Distinct to avoid returning the same person multiple times if they are in multiple teams
            "SELECT DISTINCT p " +
            "FROM PortalTeamMember ptm " +
            "JOIN PortalTeam pt ON pt = ptm.portalTeam " +
            "JOIN Person p ON p.id = ptm.personId " +
            "WHERE ptm.portalTeam.resId IN :portalTeamIds ",
        Person.class)
        .setParameter("portalTeamIds", resourceIds)
        .getResultList();
  }

  @Transactional
  public Integer createOrganisationGroupTeam(PortalOrganisationGroup organisationGroup,
                                             AuthenticatedUserAccount user) {

    final var teamType = TeamType.ORGANISATION;
    final var portalTeamType = teamType.getPortalTeamType();
    final var resourceTypeDisplayName = teamType.getPortalTeamTypeDisplayName();
    final var resourceDescription = String.format("%s - %s", resourceTypeDisplayName, organisationGroup.getName());
    final var organisationUref = organisationGroup.getUrefValue();
    final var webUserAccountId = user.getWuaId();

    try {
      return portalTeamRepository.createTeam(
          portalTeamType,
          resourceTypeDisplayName,
          resourceDescription,
          organisationUref,
          webUserAccountId
      );
    } catch (Exception e) {

      var message = String.format(
          "Error creating organisation group team with " +
              "p_resource_type: %s, " +
              "p_resource_name: %s, " +
              "p_resource_description: %s, " +
              "p_uref: %s, " +
              "p_requesting_wua_id: %s",
          portalTeamType,
          resourceTypeDisplayName,
          resourceDescription,
          organisationUref,
          webUserAccountId
      );

      throw new RuntimeException(message, e);
    }

  }

  public List<PortalTeamPersonMembershipDto> getPortalTeamPersonMembershipByResourceIdIn(List<Integer> resourceIds) {
    return entityManager.createQuery("" +
                "SELECT new uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamPersonMembershipDto(" +
                "  ptm.portalTeam.resId, " +
                "  p " +
                ")" +
                "FROM PortalTeamMember ptm " +
                "JOIN PortalTeam pt ON pt = ptm.portalTeam " +
                "JOIN Person p ON p.id = ptm.personId " +
                "WHERE ptm.portalTeam.resId IN :portalTeamIds ",
            PortalTeamPersonMembershipDto.class)
        .setParameter("portalTeamIds", resourceIds)
        .getResultList();
  }
}
