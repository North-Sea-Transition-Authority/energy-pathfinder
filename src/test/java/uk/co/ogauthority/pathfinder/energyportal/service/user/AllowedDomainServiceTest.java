package uk.co.ogauthority.pathfinder.energyportal.service.user;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.organisationgroup.OrganisationGroupDto;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.organisationgroup.OrganisationGroupQueryService;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@ExtendWith(MockitoExtension.class)
class AllowedDomainServiceTest {

  private static final String USER_EMAIL = "user@example.com";

  @Mock
  private OrganisationGroupQueryService organisationGroupQueryService;

  @InjectMocks
  private AllowedDomainService allowedDomainService;

  @ParameterizedTest
  @MethodSource("provideDomainIsAllowedCombinations")
  void isAllowedDomain_regulator(String domain, boolean isAllowed) {
    var regTeam = TeamTestingUtil.getRegulatorTeam();

    var orgGroup = new OrganisationGroupDto(1,"company1",List.of(domain));

    when(organisationGroupQueryService.getRegulatorOrganisationGroup()).thenReturn(
        Optional.of(orgGroup)
    );

    assertThat(allowedDomainService.isAllowedDomain(USER_EMAIL, regTeam)).isEqualTo(isAllowed);
  }

  @ParameterizedTest
  @MethodSource("provideDomainIsAllowedCombinations")
  void isAllowedDomain_organisation(String domain, boolean isAllowed) {

    var orgGroup = new OrganisationGroupDto(1,"company2",List.of(domain));

    var regTeam = TeamTestingUtil.getOrganisationTeam(200, "comp");

    when(organisationGroupQueryService.getOrganisationGroupById(regTeam.getId())).thenReturn(
        Optional.of(orgGroup)
    );

    assertThat(allowedDomainService.isAllowedDomain(USER_EMAIL, regTeam)).isEqualTo(isAllowed);
  }

  private static Stream<Arguments> provideDomainIsAllowedCombinations() {
    return Stream.of(
        Arguments.of("example.com", true),
        Arguments.of("domain.com", false)
    );
  }
}