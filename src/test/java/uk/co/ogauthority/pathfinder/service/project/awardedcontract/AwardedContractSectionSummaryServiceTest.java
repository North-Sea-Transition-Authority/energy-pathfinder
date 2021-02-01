package uk.co.ogauthority.pathfinder.service.project.awardedcontract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class AwardedContractSectionSummaryServiceTest {

  @Mock
  private AwardedContractService awardedContractService;

  @Mock
  private DifferenceService differenceService;

  private AwardedContractSectionSummaryService awardedContractSectionSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    awardedContractSectionSummaryService = new AwardedContractSectionSummaryService(
        awardedContractService,
        differenceService
    );
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(awardedContractService.canShowInTaskList(detail)).thenReturn(true);

    assertThat(awardedContractSectionSummaryService.canShowSection(detail)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(awardedContractService.canShowInTaskList(detail)).thenReturn(false);

    assertThat(awardedContractSectionSummaryService.canShowSection(detail)).isFalse();
  }

  @Test
  public void getSummary() {
    when(awardedContractService.getAwardedContracts(detail)).thenReturn(List.of(
        AwardedContractTestUtil.createAwardedContract(),
        AwardedContractTestUtil.createAwardedContract()
    ));

    when(awardedContractService.getAwardedContractsByProjectAndVersion(
        detail.getProject(),
        detail.getVersion() - 1
    )).thenReturn(List.of(
        AwardedContractTestUtil.createAwardedContract(),
        AwardedContractTestUtil.createAwardedContract()
    ));

    var sectionSummary = awardedContractSectionSummaryService.getSummary(detail);

    assertModelProperties(sectionSummary);

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
    when(awardedContractService.getAwardedContracts(detail)).thenReturn(Collections.emptyList());

    var sectionSummary = awardedContractSectionSummaryService.getSummary(detail);

    assertModelProperties(sectionSummary);
  }

  private void assertModelProperties(ProjectSectionSummary projectSectionSummary) {
    assertThat(projectSectionSummary.getDisplayOrder()).isEqualTo(AwardedContractSectionSummaryService.DISPLAY_ORDER);
    assertThat(projectSectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(AwardedContractSectionSummaryService.SECTION_LINK));
    assertThat(projectSectionSummary.getTemplatePath()).isEqualTo(AwardedContractSectionSummaryService.TEMPLATE_PATH);

    var model = projectSectionSummary.getTemplateModel();

    assertThat(model).containsOnlyKeys(
        "sectionTitle",
        "sectionId",
        "awardedContractDiffModel"
    );

    assertThat(model).containsEntry("sectionTitle", AwardedContractSectionSummaryService.PAGE_NAME);
    assertThat(model).containsEntry("sectionId", AwardedContractSectionSummaryService.SECTION_ID);
  }
}
