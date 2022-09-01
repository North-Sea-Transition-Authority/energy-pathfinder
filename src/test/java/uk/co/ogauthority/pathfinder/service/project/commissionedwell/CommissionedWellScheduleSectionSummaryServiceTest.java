package uk.co.ogauthority.pathfinder.service.project.commissionedwell;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.testutil.CommissionedWellTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@ExtendWith(MockitoExtension.class)
class CommissionedWellScheduleSectionSummaryServiceTest {

  @Mock
  private CommissionedWellScheduleSummaryService commissionedWellScheduleSummaryService;

  @Mock
  private DifferenceService differenceService;

  @Mock
  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private CommissionedWellScheduleSectionSummaryService commissionedWellScheduleSectionSummaryService;

  @BeforeEach
  void setup() {
    commissionedWellScheduleSectionSummaryService = new CommissionedWellScheduleSectionSummaryService(
        commissionedWellScheduleSummaryService,
        differenceService,
        projectSectionSummaryCommonModelService
    );
  }

  @Test
  void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(commissionedWellScheduleSummaryService.canShowInTaskList(projectDetail)).thenReturn(true);

    var canShowInTaskList = commissionedWellScheduleSectionSummaryService.canShowSection(projectDetail);

    assertTrue(canShowInTaskList);
  }

  @Test
  void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(commissionedWellScheduleSummaryService.canShowInTaskList(projectDetail)).thenReturn(false);

    var canShowInTaskList = commissionedWellScheduleSectionSummaryService.canShowSection(projectDetail);

    assertFalse(canShowInTaskList);
  }

  @Test
  void getSummary_whenCommissionedWellSchedules_thenViewsDiffed() {
    var currentCommissionedWellScheduleViews = List.of(
        CommissionedWellTestUtil.getCommissionedWellScheduleView(1, true),
        CommissionedWellTestUtil.getCommissionedWellScheduleView(2, true)
    );
    var previousCommissionedWellScheduleViews = List.of(
        CommissionedWellTestUtil.getCommissionedWellScheduleView(1, true)
    );

    when(commissionedWellScheduleSummaryService.getCommissionedWellScheduleViews(projectDetail)).thenReturn(currentCommissionedWellScheduleViews);
    when(commissionedWellScheduleSummaryService.getCommissionedWellScheduleViewViewsByProjectAndVersion(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )).thenReturn(previousCommissionedWellScheduleViews);

    var resultingSectionSummary = commissionedWellScheduleSectionSummaryService.getSummary(projectDetail);

    assertModelProperties(resultingSectionSummary, projectDetail);

    verify(differenceService, times(1)).differentiateComplexLists(
        eq(currentCommissionedWellScheduleViews),
        eq(previousCommissionedWellScheduleViews),
        eq(Set.of("summaryLinks")),
        any(),
        any()
    );
  }

  @Test
  void getSummary_whenNoCommissionedWellSchedules_thenEmptyListsDiffed() {

    var resultingSectionSummary = commissionedWellScheduleSectionSummaryService.getSummary(projectDetail);

    assertModelProperties(resultingSectionSummary, projectDetail);

    verify(differenceService, times(1)).differentiateComplexLists(
        eq(Collections.emptyList()),
        eq(Collections.emptyList()),
        eq(Set.of("summaryLinks")),
        any(),
        any()
    );
  }


  private void assertModelProperties(ProjectSectionSummary sectionSummary, ProjectDetail projectDetail) {

    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(CommissionedWellScheduleSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(CommissionedWellScheduleSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(CommissionedWellScheduleSectionSummaryService.TEMPLATE_PATH);

    var model = sectionSummary.getTemplateModel();

    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        projectDetail,
        CommissionedWellScheduleSectionSummaryService.PAGE_NAME,
        CommissionedWellScheduleSectionSummaryService.SECTION_ID
    );

    assertThat(model).containsOnlyKeys("commissionedWellScheduleDiffModel");
  }

}