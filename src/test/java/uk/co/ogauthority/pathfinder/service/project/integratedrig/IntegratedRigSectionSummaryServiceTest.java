package uk.co.ogauthority.pathfinder.service.project.integratedrig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import uk.co.ogauthority.pathfinder.testutil.IntegratedRigTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class IntegratedRigSectionSummaryServiceTest {

  @Mock
  private IntegratedRigSummaryService integratedRigSummaryService;

  @Mock
  private DifferenceService differenceService;

  private IntegratedRigSectionSummaryService integratedRigSectionSummaryService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    integratedRigSectionSummaryService = new IntegratedRigSectionSummaryService(
        integratedRigSummaryService,
        differenceService
    );
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(integratedRigSummaryService.canShowInTaskList(projectDetail)).thenReturn(true);

    assertThat(integratedRigSectionSummaryService.canShowSection(projectDetail)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(integratedRigSummaryService.canShowInTaskList(projectDetail)).thenReturn(false);

    assertThat(integratedRigSectionSummaryService.canShowSection(projectDetail)).isFalse();
  }

  @Test
  public void getSummary_whenIntegratedRigs_thenViewsDiffed() {
    var integratedRigViews = List.of(
        IntegratedRigTestUtil.createIntegratedRigView(1, true),
        IntegratedRigTestUtil.createIntegratedRigView(2, true)
    );
    var previousIntegratedRigViews = List.of(
        IntegratedRigTestUtil.createIntegratedRigView(1, true)
    );

    when(integratedRigSummaryService.getIntegratedRigSummaryViews(projectDetail)).thenReturn(integratedRigViews);
    when(integratedRigSummaryService.getIntegratedRigSummaryViewsByProjectAndVersion(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )).thenReturn(previousIntegratedRigViews);

    final var sectionSummary = integratedRigSectionSummaryService.getSummary(projectDetail);
    assertModelProperties(sectionSummary);

    verify(differenceService, times(1)).differentiateComplexLists(
        eq(integratedRigViews),
        eq(previousIntegratedRigViews),
        eq(Set.of("summaryLinks")),
        any(),
        any()
    );
  }

  @Test
  public void getSummary_whenNoIntegratedRigs_thenEmptyListsDiffed() {
    when(integratedRigSummaryService.getIntegratedRigSummaryViews(projectDetail)).thenReturn(Collections.emptyList());
    final var sectionSummary = integratedRigSectionSummaryService.getSummary(projectDetail);
    assertModelProperties(sectionSummary);

    verify(differenceService, times(1)).differentiateComplexLists(
        eq(Collections.emptyList()),
        eq(Collections.emptyList()),
        eq(Set.of("summaryLinks")),
        any(),
        any()
    );
  }


  private void assertModelProperties(ProjectSectionSummary sectionSummary) {

    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(IntegratedRigSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(IntegratedRigSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(IntegratedRigSectionSummaryService.TEMPLATE_PATH);

    var model = sectionSummary.getTemplateModel();

    assertThat(model).containsOnlyKeys(
        "sectionTitle",
        "sectionId",
        "integratedRigDiffModel"
    );

    assertThat(model).containsEntry("sectionTitle", IntegratedRigSectionSummaryService.PAGE_NAME);
    assertThat(model).containsEntry("sectionId", IntegratedRigSectionSummaryService.SECTION_ID);
  }

}