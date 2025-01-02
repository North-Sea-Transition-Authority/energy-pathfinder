package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.SubseaInfrastructureTestUtil;

class InfrastructureProjectSubseaInfrastructureToBeDecommissionedJsonTest {

  @Test
  void from() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withDevUkFacility();

    var infrastructureProjectSubseaInfrastructureToBeDecommissionedJson =
        InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson.from(subseaInfrastructure);

    var expectedInfrastructureProjectSubseaInfrastructureToBeDecommissionedJson =
        new InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson(
            subseaInfrastructure.getId(),
            subseaInfrastructure.getFacility().getFacilityName(),
            subseaInfrastructure.getDescription(),
            subseaInfrastructure.getStatus().name(),
            subseaInfrastructure.getInfrastructureType().name(),
            InfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJson.from(subseaInfrastructure),
            null,
            null,
            new StartEndYearJson(
                subseaInfrastructure.getEarliestDecommissioningStartYear(),
                subseaInfrastructure.getLatestDecommissioningCompletionYear()
            )
        );

    assertThat(infrastructureProjectSubseaInfrastructureToBeDecommissionedJson)
        .isEqualTo(expectedInfrastructureProjectSubseaInfrastructureToBeDecommissionedJson);
  }

  @Test
  void from_subseaInfrastructureHasFacility() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withDevUkFacility();

    var infrastructureProjectSubseaInfrastructureToBeDecommissionedJson =
        InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson.from(subseaInfrastructure);

    assertThat(infrastructureProjectSubseaInfrastructureToBeDecommissionedJson.surfaceInfrastructureHostName())
        .isEqualTo(subseaInfrastructure.getFacility().getFacilityName());
  }

  @Test
  void from_subseaInfrastructureHasManualFacility() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();

    var infrastructureProjectSubseaInfrastructureToBeDecommissionedJson =
        InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson.from(subseaInfrastructure);

    assertThat(infrastructureProjectSubseaInfrastructureToBeDecommissionedJson.surfaceInfrastructureHostName())
        .isEqualTo(subseaInfrastructure.getManualFacility());
  }

  @Test
  void from_subseaInfrastructureInfrastructureTypeIsConcreteMattresses() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withConcreteMattresses();

    var infrastructureProjectSubseaInfrastructureToBeDecommissionedJson =
        InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson.from(subseaInfrastructure);

    assertThat(infrastructureProjectSubseaInfrastructureToBeDecommissionedJson)
        .extracting(
            InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson::concreteMattressesDetails,
            InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson::subseaStructureDetails,
            InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson::otherDetails
        )
        .containsExactly(
            InfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJson.from(subseaInfrastructure),
            null,
            null
        );
  }

  @Test
  void from_subseaInfrastructureInfrastructureTypeIsSubseaStructure() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withSubseaStructure();

    var infrastructureProjectSubseaInfrastructureToBeDecommissionedJson =
        InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson.from(subseaInfrastructure);

    assertThat(infrastructureProjectSubseaInfrastructureToBeDecommissionedJson)
        .extracting(
            InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson::concreteMattressesDetails,
            InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson::subseaStructureDetails,
            InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson::otherDetails
        )
        .containsExactly(
            null,
            InfrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJson.from(subseaInfrastructure),
            null
        );
  }

  @Test
  void from_subseaInfrastructureInfrastructureTypeIsOther() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withOtherInfrastructure();

    var infrastructureProjectSubseaInfrastructureToBeDecommissionedJson =
        InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson.from(subseaInfrastructure);

    assertThat(infrastructureProjectSubseaInfrastructureToBeDecommissionedJson)
        .extracting(
            InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson::concreteMattressesDetails,
            InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson::subseaStructureDetails,
            InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson::otherDetails
        )
        .containsExactly(
            null,
            null,
            InfrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJson.from(subseaInfrastructure)
        );
  }
}
