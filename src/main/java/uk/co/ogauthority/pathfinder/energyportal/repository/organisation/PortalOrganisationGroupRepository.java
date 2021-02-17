package uk.co.ogauthority.pathfinder.energyportal.repository.organisation;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.team.PortalTeamUsagePurpose;

@Repository
public interface PortalOrganisationGroupRepository extends CrudRepository<PortalOrganisationGroup, Integer> {

  List<PortalOrganisationGroup> findByUrefValueIn(List<String> organisationGroupUrefValues);

  List<PortalOrganisationGroup> findByNameContainingIgnoreCase(String searchTerm);

  @Query("SELECT pog " +
         "FROM PortalTeam pt " +
         "JOIN PortalTeamUsage ptu ON ptu.portalTeam = pt " +
         "JOIN PortalOrganisationGroup pog ON pog.urefValue = ptu.uref " +
         "WHERE pt.portalTeamType.type = :portalTeamType " +
         "AND (ptu.purpose = :usagePurpose)")
  List<PortalOrganisationGroup> findByExistenceOfPortalTeam(@Param("portalTeamType") String portalTeamType,
                                                            @Param("usagePurpose") PortalTeamUsagePurpose portalTeamUsagePurpose);

  @Query("SELECT pog " +
         "FROM PortalTeam pt " +
         "JOIN PortalTeamUsage ptu ON ptu.portalTeam = pt " +
         "JOIN PortalOrganisationGroup pog ON pog.urefValue = ptu.uref " +
         "WHERE pt.portalTeamType.type = :portalTeamType " +
         "AND (ptu.purpose = :usagePurpose) " +
         "AND lower(pog.name) LIKE LOWER(concat('%', :searchTerm, '%'))")
  List<PortalOrganisationGroup> findByExistenceOfPortalTeamAndNameContaining(@Param("portalTeamType") String portalTeamType,
                                                                             @Param("usagePurpose") PortalTeamUsagePurpose usagePurpose,
                                                                             @Param("searchTerm") String searchTerm);
}
