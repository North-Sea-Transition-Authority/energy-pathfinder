package uk.co.ogauthority.pathfinder.service.project.subseainfrastructure;

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
import uk.co.ogauthority.pathfinder.model.view.subseainfrastructure.SubseaInfrastructureView;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.SubseaInfrastructureTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class SubseaInfrastructureSectionSummaryServiceTest {

  @Mock
  private SubseaInfrastructureService subseaInfrastructureService;

  private SubseaInfrastructureSectionSummaryService subseaInfrastructureSectionSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    subseaInfrastructureSectionSummaryService = new SubseaInfrastructureSectionSummaryService(subseaInfrastructureService);
  }

  @Test
  public void getSummary() {
    when(subseaInfrastructureService.getSubseaInfrastructures(detail)).thenReturn(List.of(
        SubseaInfrastructureTestUtil.createSubseaInfrastructure_withDevUkFacility(),
        SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility()
    ));
    var sectionSummary = subseaInfrastructureSectionSummaryService.getSummary(detail);
    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(SubseaInfrastructureSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(SubseaInfrastructureSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(SubseaInfrastructureSectionSummaryService.TEMPLATE_PATH);

    var subseaInfrastructureViews = (List<SubseaInfrastructureView>) model.get("subseaInfrastructureViews");
    assertThat(subseaInfrastructureViews).isNotNull();
    assertThat(subseaInfrastructureViews.size()).isEqualTo(2);

    assertThat(model).containsOnly(
        entry("sectionTitle", SubseaInfrastructureSectionSummaryService.PAGE_NAME),
        entry("sectionId", SubseaInfrastructureSectionSummaryService.SECTION_ID),
        entry("subseaInfrastructureViews", subseaInfrastructureViews)
    );
  }

  @Test
  public void getSummary_noSubseaInfrastructures() {
    when(subseaInfrastructureService.getSubseaInfrastructures(detail)).thenReturn(Collections.emptyList());
    var sectionSummary = subseaInfrastructureSectionSummaryService.getSummary(detail);
    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(SubseaInfrastructureSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(SubseaInfrastructureSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(SubseaInfrastructureSectionSummaryService.TEMPLATE_PATH);

    var subseaInfrastructureViews = (List<SubseaInfrastructureView>) model.get("subseaInfrastructureViews");
    assertThat(subseaInfrastructureViews).isNotNull();
    assertThat(subseaInfrastructureViews).isEmpty();

    assertThat(model).containsOnly(
        entry("sectionTitle", SubseaInfrastructureSectionSummaryService.PAGE_NAME),
        entry("sectionId", SubseaInfrastructureSectionSummaryService.SECTION_ID),
        entry("subseaInfrastructureViews", subseaInfrastructureViews)
    );
  }
}
