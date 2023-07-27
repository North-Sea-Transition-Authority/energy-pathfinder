package uk.co.ogauthority.pathfinder.service.project.awardedcontract.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractSectionSummaryService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@ExtendWith(MockitoExtension.class)
class InfrastructureAwardedContractSectionSummaryServiceTest {

  @Mock
  private InfrastructureAwardedContractService awardedContractService;

  @Mock
  private DifferenceService differenceService;

  @Mock
  private ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  @Mock
  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  private InfrastructureAwardedContractSectionSummaryService awardedContractSectionSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @BeforeEach
  void setup() {
    awardedContractSectionSummaryService = new InfrastructureAwardedContractSectionSummaryService(
        differenceService,
        projectSectionSummaryCommonModelService,
        projectSectionItemOwnershipService,
        portalOrganisationAccessor,
        awardedContractService
    );
  }

  @Test
  void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(awardedContractService.isTaskValidForProjectDetail(detail)).thenReturn(true);

    assertThat(awardedContractSectionSummaryService.canShowSection(detail)).isTrue();
  }

  @Test
  void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(awardedContractService.isTaskValidForProjectDetail(detail)).thenReturn(false);

    assertThat(awardedContractSectionSummaryService.canShowSection(detail)).isFalse();
  }

  @Test
  void getSummary() {
    when(awardedContractService.getAwardedContracts(detail)).thenReturn(List.of(
        AwardedContractTestUtil.createInfrastructureAwardedContract(),
        AwardedContractTestUtil.createInfrastructureAwardedContract()
    ));

    when(awardedContractService.getAwardedContractsByProjectAndVersion(
        detail.getProject(),
        detail.getVersion() - 1
    )).thenReturn(List.of(
        AwardedContractTestUtil.createInfrastructureAwardedContract(),
        AwardedContractTestUtil.createInfrastructureAwardedContract()
    ));

    var sectionSummary = awardedContractSectionSummaryService.getSummary(detail);

    AwardedContractTestUtil.assertModelProperties(sectionSummary, InfrastructureAwardedContractSectionSummaryService.TEMPLATE_PATH);

    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        detail,
        AwardedContractSectionSummaryService.PAGE_NAME,
        AwardedContractSectionSummaryService.SECTION_ID
    );

    verify(differenceService, times(1)).differentiateComplexLists(
        any(),
        any(),
        eq(Set.of("summaryLinks")),
        any(),
        any()
    );
  }

  @Test
  void getSummary_noAwardedContracts() {
    when(awardedContractService.getAwardedContracts(detail)).thenReturn(Collections.emptyList());

    var sectionSummary = awardedContractSectionSummaryService.getSummary(detail);

    AwardedContractTestUtil.assertModelProperties(sectionSummary, InfrastructureAwardedContractSectionSummaryService.TEMPLATE_PATH);
    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        detail,
        AwardedContractSectionSummaryService.PAGE_NAME,
        AwardedContractSectionSummaryService.SECTION_ID
    );
  }
}
