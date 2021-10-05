package uk.co.ogauthority.pathfinder.energyportal.repository.organisation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationUnit;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("integration-test")
@DirtiesContext
public class PortalOrganisationUnitRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private PortalOrganisationUnitRepository portalOrganisationUnitRepository;

  private final PortalOrganisationGroup organisationGroup1 = TeamTestingUtil.generateOrganisationGroup(100, "name", "short name");
  private final PortalOrganisationGroup organisationGroup2 = TeamTestingUtil.generateOrganisationGroup(200, "name", "short name");

  @Before
  public void setup() {
    entityManager.persist(organisationGroup1);
    entityManager.persist(organisationGroup2);
    entityManager.flush();
  }

  @Test
  public void findByNameContainingIgnoreCaseAndActiveTrue_verifyActiveFilterApplied() {

    var organisationUnitName = "name";

    var activePortalOrganisationUnit = new PortalOrganisationUnit(1, organisationUnitName, true, organisationGroup1);
    var nonActivePortalOrganisationUnit = new PortalOrganisationUnit(2, organisationUnitName, false, organisationGroup1);

    entityManager.persist(activePortalOrganisationUnit);
    entityManager.persist(nonActivePortalOrganisationUnit);

    entityManager.flush();

    var resultingOrganisationUnits = portalOrganisationUnitRepository.findByNameContainingIgnoreCaseAndActiveTrue(organisationUnitName);

    assertThat(resultingOrganisationUnits).containsExactly(activePortalOrganisationUnit);
  }

  @Test
  public void findByNameContainingIgnoreCaseAndActiveTrue_verifyNameFilterApplied() {

    var organisationUnitName = "name";

    var matchingNamePortalOrganisationUnit = new PortalOrganisationUnit(1, organisationUnitName, true, organisationGroup1);
    var nonMatchingNamePortalOrganisationUnit = new PortalOrganisationUnit(2, "not matching", true, organisationGroup1);

    entityManager.persist(matchingNamePortalOrganisationUnit);
    entityManager.persist(nonMatchingNamePortalOrganisationUnit);

    entityManager.flush();

    var resultingOrganisationUnits = portalOrganisationUnitRepository.findByNameContainingIgnoreCaseAndActiveTrue(organisationUnitName);

    assertThat(resultingOrganisationUnits).containsExactly(matchingNamePortalOrganisationUnit);
  }

  @Test
  public void findByNameContainingIgnoreCaseAndActiveTrue_verifyNameFilterIgnoresCase() {

    var mixedCaseOrganisationUnitName = "NaMe";

    var lowercaseNamePortalOrganisationUnit = new PortalOrganisationUnit(1, mixedCaseOrganisationUnitName.toUpperCase(), true, organisationGroup1);
    var upperCaseNamePortalOrganisationUnit = new PortalOrganisationUnit(2, mixedCaseOrganisationUnitName.toLowerCase(), true, organisationGroup1);

    entityManager.persist(lowercaseNamePortalOrganisationUnit);
    entityManager.persist(upperCaseNamePortalOrganisationUnit);

    entityManager.flush();

    var resultingOrganisationUnits = portalOrganisationUnitRepository.findByNameContainingIgnoreCaseAndActiveTrue(mixedCaseOrganisationUnitName);

    assertThat(resultingOrganisationUnits).containsExactlyInAnyOrder(
        lowercaseNamePortalOrganisationUnit,
        upperCaseNamePortalOrganisationUnit
    );
  }

  @Test
  public void findByNameContainingIgnoreCaseAndActiveTrue_verifyNameFilterPartialMatches() {

    var organisationUnitName = "name";

    var exactMatchingNamePortalOrganisationUnit = new PortalOrganisationUnit(1, organisationUnitName, true, organisationGroup1);
    var partialMatchingNamePortalOrganisationUnit = new PortalOrganisationUnit(2, String.format("ends with %s", organisationUnitName), true, organisationGroup1);

    entityManager.persist(exactMatchingNamePortalOrganisationUnit);
    entityManager.persist(partialMatchingNamePortalOrganisationUnit);

    entityManager.flush();

    var resultingOrganisationUnits = portalOrganisationUnitRepository.findByNameContainingIgnoreCaseAndActiveTrue(organisationUnitName);

    assertThat(resultingOrganisationUnits).containsExactlyInAnyOrder(
        exactMatchingNamePortalOrganisationUnit,
        partialMatchingNamePortalOrganisationUnit
    );
  }

  @Test
  public void findByActiveTrueAndPortalOrganisationGroupIn_verifyActiveFilterApplied() {

    var organisationUnitName = "name";

    var activePortalOrganisationUnit = new PortalOrganisationUnit(1, organisationUnitName, true, organisationGroup1);
    var nonActivePortalOrganisationUnit = new PortalOrganisationUnit(2, organisationUnitName, false, organisationGroup1);

    entityManager.persist(activePortalOrganisationUnit);
    entityManager.persist(nonActivePortalOrganisationUnit);

    entityManager.flush();

    var resultingOrganisationUnits = portalOrganisationUnitRepository.findByActiveTrueAndPortalOrganisationGroupIn(
        List.of(organisationGroup1)
    );

    assertThat(resultingOrganisationUnits).containsExactly(activePortalOrganisationUnit);
  }

  @Test
  public void findByActiveTrueAndPortalOrganisationGroupIn_verifyGroupFilterApplied() {

    var organisationUnitName = "name";
    var expectedOrganisationGroup = organisationGroup1;

    var organisationGroup1PortalOrganisationUnit = new PortalOrganisationUnit(1, organisationUnitName, true, expectedOrganisationGroup);
    var organisationGroup2PortalOrganisationUnit = new PortalOrganisationUnit(2, organisationUnitName, true, organisationGroup2);

    entityManager.persist(organisationGroup1PortalOrganisationUnit);
    entityManager.persist(organisationGroup2PortalOrganisationUnit);

    entityManager.flush();

    var resultingOrganisationUnits = portalOrganisationUnitRepository.findByActiveTrueAndPortalOrganisationGroupIn(
        List.of(expectedOrganisationGroup)
    );

    assertThat(resultingOrganisationUnits).containsExactly(organisationGroup1PortalOrganisationUnit);
  }

  @Test
  public void findByNameContainingIgnoreCaseAndActiveTrueAndPortalOrganisationGroupIn_verifyActiveFilterApplied() {

    var organisationUnitName = "name";
    var organisationGroup = organisationGroup1;

    var activePortalOrganisationUnit = new PortalOrganisationUnit(1, organisationUnitName, true, organisationGroup);
    var nonActivePortalOrganisationUnit = new PortalOrganisationUnit(2, organisationUnitName, false, organisationGroup);

    entityManager.persist(activePortalOrganisationUnit);
    entityManager.persist(nonActivePortalOrganisationUnit);

    entityManager.flush();

    var resultingOrganisationUnits = portalOrganisationUnitRepository.findByNameContainingIgnoreCaseAndActiveTrueAndPortalOrganisationGroupIn(
        organisationUnitName,
        List.of(organisationGroup)
    );

    assertThat(resultingOrganisationUnits).containsExactly(activePortalOrganisationUnit);
  }

  @Test
  public void findByNameContainingIgnoreCaseAndActiveTrueAndPortalOrganisationGroupIn_verifyNameFilterApplied() {

    var organisationUnitName = "name";
    var organisationGroup = organisationGroup1;

    var matchedNamePortalOrganisationUnit = new PortalOrganisationUnit(1, organisationUnitName, true, organisationGroup);
    var nonMatchedNamePortalOrganisationUnit = new PortalOrganisationUnit(2, "not matched", false, organisationGroup);

    entityManager.persist(matchedNamePortalOrganisationUnit);
    entityManager.persist(nonMatchedNamePortalOrganisationUnit);

    entityManager.flush();

    var resultingOrganisationUnits = portalOrganisationUnitRepository.findByNameContainingIgnoreCaseAndActiveTrueAndPortalOrganisationGroupIn(
        organisationUnitName,
        List.of(organisationGroup)
    );

    assertThat(resultingOrganisationUnits).containsExactly(matchedNamePortalOrganisationUnit);

  }

  @Test
  public void findByNameContainingIgnoreCaseAndActiveTrueAndPortalOrganisationGroupIn_verifyNameFilterIgnoresCase() {

    var mixedCaseOrganisationUnitName = "NaMe";
    var organisationGroup = organisationGroup1;

    var lowercaseNamePortalOrganisationUnit = new PortalOrganisationUnit(1, mixedCaseOrganisationUnitName.toUpperCase(), true, organisationGroup);
    var upperCaseNamePortalOrganisationUnit = new PortalOrganisationUnit(2, mixedCaseOrganisationUnitName.toLowerCase(), true, organisationGroup);

    entityManager.persist(lowercaseNamePortalOrganisationUnit);
    entityManager.persist(upperCaseNamePortalOrganisationUnit);

    entityManager.flush();

    var resultingOrganisationUnits = portalOrganisationUnitRepository.findByNameContainingIgnoreCaseAndActiveTrueAndPortalOrganisationGroupIn(
        mixedCaseOrganisationUnitName,
        List.of(organisationGroup)
    );

    assertThat(resultingOrganisationUnits).containsExactlyInAnyOrder(
        lowercaseNamePortalOrganisationUnit,
        upperCaseNamePortalOrganisationUnit
    );
  }

  @Test
  public void findByNameContainingIgnoreCaseAndActiveTrueAndPortalOrganisationGroupIn_verifyNameFilterPartialMatches() {

    var organisationUnitName = "name";
    var organisationGroup = organisationGroup1;

    var exactMatchingNamePortalOrganisationUnit = new PortalOrganisationUnit(1, organisationUnitName, true, organisationGroup);
    var partialMatchingNamePortalOrganisationUnit = new PortalOrganisationUnit(2, String.format("ends with %s", organisationUnitName), true, organisationGroup);

    entityManager.persist(exactMatchingNamePortalOrganisationUnit);
    entityManager.persist(partialMatchingNamePortalOrganisationUnit);

    entityManager.flush();

    var resultingOrganisationUnits = portalOrganisationUnitRepository.findByNameContainingIgnoreCaseAndActiveTrueAndPortalOrganisationGroupIn(
        organisationUnitName,
        List.of(organisationGroup)
    );

    assertThat(resultingOrganisationUnits).containsExactlyInAnyOrder(
        exactMatchingNamePortalOrganisationUnit,
        partialMatchingNamePortalOrganisationUnit
    );

  }

  @Test
  public void findByNameContainingIgnoreCaseAndActiveTrueAndPortalOrganisationGroupIn_verifyGroupFilterApplied() {

    var organisationUnitName = "name";
    var expectedOrganisationGroup = organisationGroup1;

    var organisationGroup1PortalOrganisationUnit = new PortalOrganisationUnit(1, organisationUnitName, true, expectedOrganisationGroup);
    var organisationGroup2PortalOrganisationUnit = new PortalOrganisationUnit(2, organisationUnitName, true, organisationGroup2);

    entityManager.persist(organisationGroup1PortalOrganisationUnit);
    entityManager.persist(organisationGroup2PortalOrganisationUnit);

    entityManager.flush();

    var resultingOrganisationUnits = portalOrganisationUnitRepository.findByNameContainingIgnoreCaseAndActiveTrueAndPortalOrganisationGroupIn(
        organisationUnitName,
        List.of(expectedOrganisationGroup)
    );

    assertThat(resultingOrganisationUnits).containsExactly(organisationGroup1PortalOrganisationUnit);
  }

  @Test
  public void existsByOuIdAndActiveTrueAndPortalOrganisationGroup_OrgGrpId_whenOuIdExists_thenTrue() {

    var ouIdToTest = 10;
    var organisationGroup = organisationGroup1;

    var portalOrganisationUnit = new PortalOrganisationUnit(ouIdToTest, "name", true, organisationGroup);

    entityManager.persistAndFlush(portalOrganisationUnit);

    var exists = portalOrganisationUnitRepository.existsByOuIdAndActiveTrueAndPortalOrganisationGroup_OrgGrpId(ouIdToTest, organisationGroup.getOrgGrpId());

    assertThat(exists).isTrue();
  }

  @Test
  public void existsByOuIdAndActiveTrueAndPortalOrganisationGroup_OrgGrpId_whenOuIdNotExists_thenFalse() {

    var ouIdToTest = 20;
    var organisationGroup = organisationGroup1;

    var portalOrganisationUnit = new PortalOrganisationUnit(10, "name", true, organisationGroup);

    entityManager.persistAndFlush(portalOrganisationUnit);

    var exists = portalOrganisationUnitRepository.existsByOuIdAndActiveTrueAndPortalOrganisationGroup_OrgGrpId(ouIdToTest, organisationGroup.getOrgGrpId());

    assertThat(exists).isFalse();
  }

  @Test
  public void existsByOuIdAndActiveTrueAndPortalOrganisationGroup_OrgGrpId_whenActive_thenTrue() {

    var organisationGroup = organisationGroup1;
    var unitId = 100;

    var portalOrganisationUnit = new PortalOrganisationUnit(unitId, "name", true, organisationGroup);

    entityManager.persistAndFlush(portalOrganisationUnit);

    var exists = portalOrganisationUnitRepository.existsByOuIdAndActiveTrueAndPortalOrganisationGroup_OrgGrpId(unitId, organisationGroup.getOrgGrpId());

    assertThat(exists).isTrue();
  }

  @Test
  public void existsByOuIdAndActiveTrueAndPortalOrganisationGroup_OrgGrpId_whenNotActive_thenFalse() {

    var organisationGroup = organisationGroup1;
    var unitId = 100;

    var portalOrganisationUnit = new PortalOrganisationUnit(unitId, "name", false, organisationGroup);

    entityManager.persistAndFlush(portalOrganisationUnit);

    var exists = portalOrganisationUnitRepository.existsByOuIdAndActiveTrueAndPortalOrganisationGroup_OrgGrpId(unitId, organisationGroup.getOrgGrpId());

    assertThat(exists).isFalse();
  }

  @Test
  public void existsByOuIdAndActiveTrueAndPortalOrganisationGroup_OrgGrpId_whenOrgGrpIdExists_thenTrue() {

    var organisationGroup = organisationGroup1;
    var unitId = 100;

    var portalOrganisationUnit = new PortalOrganisationUnit(unitId, "name", true, organisationGroup);

    entityManager.persistAndFlush(portalOrganisationUnit);

    var exists = portalOrganisationUnitRepository.existsByOuIdAndActiveTrueAndPortalOrganisationGroup_OrgGrpId(unitId, organisationGroup.getOrgGrpId());

    assertThat(exists).isTrue();
  }

  @Test
  public void existsByOuIdAndActiveTrueAndPortalOrganisationGroup_OrgGrpId_whenOrgGrpIdNotExists_thenFalse() {

    var unitId = 100;

    var portalOrganisationUnit = new PortalOrganisationUnit(unitId, "name", true, organisationGroup1);

    entityManager.persistAndFlush(portalOrganisationUnit);

    var exists = portalOrganisationUnitRepository.existsByOuIdAndActiveTrueAndPortalOrganisationGroup_OrgGrpId(unitId, organisationGroup2.getOrgGrpId());

    assertThat(exists).isFalse();
  }

}