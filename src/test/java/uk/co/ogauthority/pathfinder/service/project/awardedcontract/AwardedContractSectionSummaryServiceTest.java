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
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.AwardedContractView;
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
    when(awardedContractService.getAwardedContracts(detail)).thenReturn(List.of(
        AwardedContractTestUtil.createAwardedContract(),
        AwardedContractTestUtil.createAwardedContract()
    ));
    var sectionSummary = awardedContractSectionSummaryService.getSummary(detail);
    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(AwardedContractSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(AwardedContractSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(AwardedContractSectionSummaryService.TEMPLATE_PATH);

    var awardedContractViews = (List<AwardedContractView>) model.get("awardedContractViews");
    assertThat(awardedContractViews).isNotNull();
    assertThat(awardedContractViews.size()).isEqualTo(2);

    assertThat(model).containsOnly(
        entry("sectionTitle", AwardedContractSectionSummaryService.PAGE_NAME),
        entry("sectionId", AwardedContractSectionSummaryService.SECTION_ID),
        entry("awardedContractViews", awardedContractViews)
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

    var awardedContractViews = (List<AwardedContractView>) model.get("awardedContractViews");
    assertThat(awardedContractViews).isNotNull();
    assertThat(awardedContractViews).isEmpty();

    assertThat(model).containsOnly(
        entry("sectionTitle", AwardedContractSectionSummaryService.PAGE_NAME),
        entry("sectionId", AwardedContractSectionSummaryService.SECTION_ID),
        entry("awardedContractViews", awardedContractViews)
    );
  }
}
