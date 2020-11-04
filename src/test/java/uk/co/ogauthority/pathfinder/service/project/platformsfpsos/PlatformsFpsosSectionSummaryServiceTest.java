package uk.co.ogauthority.pathfinder.service.project.platformsfpsos;

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
import uk.co.ogauthority.pathfinder.model.view.platformfpso.PlatformFpsoView;
import uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class PlatformsFpsosSectionSummaryServiceTest {

  @Mock
  private PlatformsFpsosService platformsFpsosService;

  private PlatformsFpsosSectionSummaryService platformsFpsosSectionSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    platformsFpsosSectionSummaryService = new PlatformsFpsosSectionSummaryService(platformsFpsosService);
  }

  @Test
  public void getSummary() {
    when(platformsFpsosService.getPlatformsFpsosForDetail(detail)).thenReturn(List.of(
        PlatformFpsoTestUtil.getPlatformFpso_withSubstructuresRemoved(detail),
        PlatformFpsoTestUtil.getPlatformFpso_withSubstructuresRemoved(detail)
    ));
    var sectionSummary = platformsFpsosSectionSummaryService.getSummary(detail);
    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(PlatformsFpsosSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(PlatformsFpsosSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(PlatformsFpsosSectionSummaryService.TEMPLATE_PATH);

    var platformFpsoViews = (List<PlatformFpsoView>) model.get("platformFpsoViews");
    assertThat(platformFpsoViews).hasSize(2);

    assertThat(model).containsOnly(
        entry("sectionTitle", PlatformsFpsosSectionSummaryService.PAGE_NAME),
        entry("sectionId", PlatformsFpsosSectionSummaryService.SECTION_ID),
        entry("platformFpsoViews", platformFpsoViews)
    );
  }

  @Test
  public void getSummary_noPlatformsFpsos() {
    when(platformsFpsosService.getPlatformsFpsosForDetail(detail)).thenReturn(Collections.emptyList());
    var sectionSummary = platformsFpsosSectionSummaryService.getSummary(detail);
    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(PlatformsFpsosSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(PlatformsFpsosSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(PlatformsFpsosSectionSummaryService.TEMPLATE_PATH);

    var platformFpsoViews = (List<PlatformFpsoView>) model.get("platformFpsoViews");
    assertThat(platformFpsoViews).isEmpty();

    assertThat(model).containsOnly(
        entry("sectionTitle", PlatformsFpsosSectionSummaryService.PAGE_NAME),
        entry("sectionId", PlatformsFpsosSectionSummaryService.SECTION_ID),
        entry("platformFpsoViews", platformFpsoViews)
    );
  }
}
