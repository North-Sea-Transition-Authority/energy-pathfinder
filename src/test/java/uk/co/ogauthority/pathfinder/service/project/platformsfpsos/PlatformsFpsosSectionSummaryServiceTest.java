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
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class PlatformsFpsosSectionSummaryServiceTest {

  @Mock
  private PlatformsFpsosService platformsFpsosService;

  @Mock
  private DifferenceService differenceService;

  @Mock
  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  private PlatformsFpsosSectionSummaryService platformsFpsosSectionSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    when(differenceService.getDiffableList(any(), any())).thenCallRealMethod();

    platformsFpsosSectionSummaryService = new PlatformsFpsosSectionSummaryService(
        platformsFpsosService,
        differenceService,
        projectSectionSummaryCommonModelService
    );
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(platformsFpsosService.isTaskValidForProjectDetail(detail)).thenReturn(true);

    assertThat(platformsFpsosSectionSummaryService.canShowSection(detail)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(platformsFpsosService.isTaskValidForProjectDetail(detail)).thenReturn(false);

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

    assertModelProperties(sectionSummary, detail);
    assertInteractions(currentPlatformFpsoViews, previousPlatformFpsoViews);
  }

  @Test
  public void getSummary_noPlatformsFpsos() {
    when(platformsFpsosService.getPlatformsFpsosByProjectDetail(detail)).thenReturn(Collections.emptyList());

    var sectionSummary = platformsFpsosSectionSummaryService.getSummary(detail);

    assertModelProperties(sectionSummary, detail);

    verify(differenceService, never()).differentiate(any(), any(), any());
  }

  private void assertModelProperties(ProjectSectionSummary projectSectionSummary, ProjectDetail projectDetail) {
    assertThat(projectSectionSummary.getDisplayOrder()).isEqualTo(PlatformsFpsosSectionSummaryService.DISPLAY_ORDER);
    assertThat(projectSectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(PlatformsFpsosSectionSummaryService.SECTION_LINK));
    assertThat(projectSectionSummary.getTemplatePath()).isEqualTo(PlatformsFpsosSectionSummaryService.TEMPLATE_PATH);

    var model = projectSectionSummary.getTemplateModel();

    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        projectDetail,
        PlatformsFpsosSectionSummaryService.PAGE_NAME,
        PlatformsFpsosSectionSummaryService.SECTION_ID
    );

    assertThat(model).containsOnlyKeys("platformFpsoDiffModel");
  }

  private void assertInteractions(List<PlatformFpsoView> currentPlatformFpsoViews,
                                  List<PlatformFpsoView> previousPlatformFpsoViews) {

    var diffList = differenceService.getDiffableList(currentPlatformFpsoViews, previousPlatformFpsoViews);

    diffList.forEach(platformFpsoView -> {

      var currentPlatformFpsoView = currentPlatformFpsoViews
          .stream()
          .filter(view -> view.getDisplayOrder().equals(platformFpsoView.getDisplayOrder()))
          .findFirst()
          .orElse(new PlatformFpsoView());

      var previousPlatformFpsoView = previousPlatformFpsoViews
          .stream()
          .filter(view -> view.getDisplayOrder().equals(platformFpsoView.getDisplayOrder()))
          .findFirst()
          .orElse(new PlatformFpsoView());

      verify(differenceService, times(1)).differentiate(
          currentPlatformFpsoView,
          previousPlatformFpsoView,
          Set.of("summaryLinks")
      );
    });
  }
}
