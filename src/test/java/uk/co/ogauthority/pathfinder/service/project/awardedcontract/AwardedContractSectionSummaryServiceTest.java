package uk.co.ogauthority.pathfinder.service.project.awardedcontract;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.AwardedContractViewUtil;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class AwardedContractSectionSummaryServiceTest {

  @Mock
  private AwardedContractService awardedContractService;

  private AwardedContractSectionSummaryService awardedContractSectionSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    awardedContractSectionSummaryService = new AwardedContractSectionSummaryService(awardedContractService);
  }

  @Test
  public void getSummary() {
    var awardedContract1 = AwardedContractTestUtil.createAwardedContract();
    var awardedContract2 = AwardedContractTestUtil.createAwardedContract();
    when(awardedContractService.getAwardedContracts(detail)).thenReturn(List.of(
        awardedContract1,
        awardedContract2
    ));
    var sectionSummary = awardedContractSectionSummaryService.getSummary(detail);
    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(AwardedContractSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(AwardedContractSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(AwardedContractSectionSummaryService.TEMPLATE_PATH);

    var awardedContractView1 = AwardedContractViewUtil.from(awardedContract1, 1);
    var awardedContractView2 = AwardedContractViewUtil.from(awardedContract2, 2);

    assertThat(model).containsOnly(
        entry("sectionTitle", AwardedContractSectionSummaryService.PAGE_NAME),
        entry("sectionId", AwardedContractSectionSummaryService.SECTION_ID),
        entry("awardedContractViews", List.of(awardedContractView1, awardedContractView2))
    );
  }

  @Test
  public void getSummary_noAwardedContracts() {
    when(awardedContractService.getAwardedContracts(detail)).thenReturn(Collections.emptyList());
    var sectionSummary = awardedContractSectionSummaryService.getSummary(detail);
    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(AwardedContractSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(AwardedContractSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(AwardedContractSectionSummaryService.TEMPLATE_PATH);

    assertThat(model).containsOnly(
        entry("sectionTitle", AwardedContractSectionSummaryService.PAGE_NAME),
        entry("sectionId", AwardedContractSectionSummaryService.SECTION_ID),
        entry("awardedContractViews", Collections.emptyList())
    );
  }
}
