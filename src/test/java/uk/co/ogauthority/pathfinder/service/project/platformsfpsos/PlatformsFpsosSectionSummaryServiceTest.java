package uk.co.ogauthority.pathfinder.service.project.platformsfpsos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import uk.co.ogauthority.pathfinder.model.view.platformfpso.PlatformFpsoView;
import uk.co.ogauthority.pathfinder.model.view.platformfpso.PlatformFpsoViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class PlatformsFpsosSectionSummaryServiceTest {

  @Mock
  private PlatformsFpsosService platformsFpsosService;

  @Mock
  private DifferenceService differenceService;

  private PlatformsFpsosSectionSummaryService platformsFpsosSectionSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    platformsFpsosSectionSummaryService = new PlatformsFpsosSectionSummaryService(
        platformsFpsosService,
        differenceService
    );
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(platformsFpsosService.canShowInTaskList(detail)).thenReturn(true);

    assertThat(platformsFpsosSectionSummaryService.canShowSection(detail)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(platformsFpsosService.canShowInTaskList(detail)).thenReturn(false);

    assertThat(platformsFpsosSectionSummaryService.canShowSection(detail)).isFalse();
  }

  @Test
  public void getSummary() {
    var platformFpso1 = PlatformFpsoTestUtil.getPlatformFpso_withPlatform(detail);
    var platformFpso2 = PlatformFpsoTestUtil.getPlatformFpso_withPlatform(detail);
    var previousPlatformFpsos = List.of(platformFpso1, platformFpso2);
    var previousPlatformFpsoViews = List.of(
        PlatformFpsoViewUtil.createView(platformFpso1, 1, detail.getProject().getId()),
        PlatformFpsoViewUtil.createView(platformFpso2, 2, detail.getProject().getId())
    );

    var platformFpso3 = PlatformFpsoTestUtil.getPlatformFpso_withPlatform(detail);
    var currentPlatformFpsos = List.of(platformFpso3);
    var currentPlatformFpsoViews = List.of(
        PlatformFpsoViewUtil.createView(platformFpso3, 1, detail.getProject().getId())
    );

    when(platformsFpsosService.getPlatformsFpsosByProjectDetail(detail)).thenReturn(currentPlatformFpsos);
    when(platformsFpsosService.getPlatformsFpsosByProjectAndVersion(
        detail.getProject(),
        detail.getVersion() - 1
    )).thenReturn(previousPlatformFpsos);
    var sectionSummary = platformsFpsosSectionSummaryService.getSummary(detail);

    assertModelProperties(sectionSummary);
    assertInteractions(currentPlatformFpsoViews, previousPlatformFpsoViews);
  }

  @Test
  public void getSummary_noPlatformsFpsos() {
    when(platformsFpsosService.getPlatformsFpsosByProjectDetail(detail)).thenReturn(Collections.emptyList());

    var sectionSummary = platformsFpsosSectionSummaryService.getSummary(detail);

    assertModelProperties(sectionSummary);

    verify(differenceService, never()).differentiate(any(), any(), any());
  }

  private void assertModelProperties(ProjectSectionSummary projectSectionSummary) {
    assertThat(projectSectionSummary.getDisplayOrder()).isEqualTo(PlatformsFpsosSectionSummaryService.DISPLAY_ORDER);
    assertThat(projectSectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(PlatformsFpsosSectionSummaryService.SECTION_LINK));
    assertThat(projectSectionSummary.getTemplatePath()).isEqualTo(PlatformsFpsosSectionSummaryService.TEMPLATE_PATH);

    var model = projectSectionSummary.getTemplateModel();

    assertThat(model).containsOnlyKeys(
        "sectionTitle",
        "sectionId",
        "platformFpsoDiffModel"
    );

    assertThat(model).containsEntry("sectionTitle", PlatformsFpsosSectionSummaryService.PAGE_NAME);
    assertThat(model).containsEntry("sectionId", PlatformsFpsosSectionSummaryService.SECTION_ID);
  }

  private void assertInteractions(List<PlatformFpsoView> currentPlatformFpsoViews,
                                  List<PlatformFpsoView> previousPlatformFpsoViews) {

    currentPlatformFpsoViews.forEach(platformFpsoView -> {

      var previousPlatformFpsoView = previousPlatformFpsoViews
          .stream()
          .filter(view -> view.getDisplayOrder().equals(platformFpsoView.getDisplayOrder()))
          .findFirst()
          .orElse(new PlatformFpsoView());

      verify(differenceService, times(1)).differentiate(
          platformFpsoView,
          previousPlatformFpsoView,
          Set.of("summaryLinks")
      );
    });
  }
}
