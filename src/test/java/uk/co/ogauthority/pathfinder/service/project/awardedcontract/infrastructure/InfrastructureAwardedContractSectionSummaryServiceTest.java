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
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractServiceCommon;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@ExtendWith(MockitoExtension.class)
public class InfrastructureAwardedContractSectionSummaryServiceTest {

  @Mock
  private InfrastructureAwardedContractService awardedContractService;

  @Mock
  private DifferenceService differenceService;

  @Mock
  private ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  @Mock
  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  @Mock
  private AwardedContractServiceCommon awardedContractServiceCommon;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  private InfrastructureAwardedContractSectionSummaryService awardedContractSectionSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @BeforeEach
  public void setup() {
    awardedContractSectionSummaryService = new InfrastructureAwardedContractSectionSummaryService(
        awardedContractService,
        awardedContractServiceCommon,
        differenceService,
        projectSectionSummaryCommonModelService,
        projectSectionItemOwnershipService,
        portalOrganisationAccessor
    );
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(awardedContractService.isTaskValidForProjectDetail(detail)).thenReturn(true);

    assertThat(awardedContractSectionSummaryService.canShowSection(detail)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(awardedContractService.isTaskValidForProjectDetail(detail)).thenReturn(false);

    assertThat(awardedContractSectionSummaryService.canShowSection(detail)).isFalse();
  }

  @Test
  public void getSummary() {
    when(awardedContractServiceCommon.getAwardedContracts(detail)).thenReturn(List.of(
        AwardedContractTestUtil.createAwardedContract(),
        AwardedContractTestUtil.createAwardedContract()
    ));

    when(awardedContractServiceCommon.getAwardedContractsByProjectAndVersion(
        detail.getProject(),
        detail.getVersion() - 1
    )).thenReturn(List.of(
        AwardedContractTestUtil.createAwardedContract(),
        AwardedContractTestUtil.createAwardedContract()
    ));

    var sectionSummary = awardedContractSectionSummaryService.getSummary(detail);

    assertModelProperties(sectionSummary, detail);

    verify(differenceService, times(1)).differentiateComplexLists(
        any(),
        any(),
        eq(Set.of("summaryLinks")),
        any(),
        any()
    );
  }

  @Test
  public void getSummary_noAwardedContracts() {
    when(awardedContractServiceCommon.getAwardedContracts(detail)).thenReturn(Collections.emptyList());

    var sectionSummary = awardedContractSectionSummaryService.getSummary(detail);

    assertModelProperties(sectionSummary, detail);
  }

  private void assertModelProperties(ProjectSectionSummary projectSectionSummary, ProjectDetail projectDetail) {
    assertThat(projectSectionSummary.getDisplayOrder()).isEqualTo(InfrastructureAwardedContractSectionSummaryService.DISPLAY_ORDER);
    assertThat(projectSectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(
        InfrastructureAwardedContractSectionSummaryService.SECTION_LINK));
    assertThat(projectSectionSummary.getTemplatePath()).isEqualTo(InfrastructureAwardedContractSectionSummaryService.TEMPLATE_PATH);

    var model = projectSectionSummary.getTemplateModel();

    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        projectDetail,
        InfrastructureAwardedContractSectionSummaryService.PAGE_NAME,
        InfrastructureAwardedContractSectionSummaryService.SECTION_ID
    );

    assertThat(model).containsOnlyKeys("awardedContractDiffModel");
  }
}
