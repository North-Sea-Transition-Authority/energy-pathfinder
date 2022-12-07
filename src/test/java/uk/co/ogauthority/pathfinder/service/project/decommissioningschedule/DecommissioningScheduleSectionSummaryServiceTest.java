package uk.co.ogauthority.pathfinder.service.project.decommissioningschedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissioningschedule.DecommissioningSchedule;
import uk.co.ogauthority.pathfinder.model.view.decommissioningschedule.DecommissioningScheduleView;
import uk.co.ogauthority.pathfinder.model.view.decommissioningschedule.DecommissioningScheduleViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.testutil.DecommissioningScheduleTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class DecommissioningScheduleSectionSummaryServiceTest {

  @Mock
  private DecommissioningScheduleService decommissioningScheduleService;

  @Mock
  private DifferenceService differenceService;

  @Mock
  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  private DecommissioningScheduleSectionSummaryService decommissioningScheduleSectionSummaryService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final DecommissioningSchedule decommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule(projectDetail);

  @Before
  public void setUp() {
    decommissioningScheduleSectionSummaryService = new DecommissioningScheduleSectionSummaryService(
        decommissioningScheduleService,
        differenceService,
        projectSectionSummaryCommonModelService
    );
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(decommissioningScheduleService.isTaskValidForProjectDetail(projectDetail)).thenReturn(true);

    assertThat(decommissioningScheduleSectionSummaryService.canShowSection(projectDetail)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(decommissioningScheduleService.isTaskValidForProjectDetail(projectDetail)).thenReturn(false);

    assertThat(decommissioningScheduleSectionSummaryService.canShowSection(projectDetail)).isFalse();
  }

  @Test
  public void getSummary() {
    when(decommissioningScheduleService.getDecommissioningSchedule(projectDetail)).thenReturn(Optional.of(decommissioningSchedule));

    var previousDecommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule(projectDetail);

    when(decommissioningScheduleService.getDecommissioningScheduleByProjectAndVersion(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )).thenReturn(Optional.of(previousDecommissioningSchedule));

    var sectionSummary = decommissioningScheduleSectionSummaryService.getSummary(projectDetail);

    var currentProjectInformationView = DecommissioningScheduleViewUtil.from(decommissioningSchedule);
    var previousProjectInformationView = DecommissioningScheduleViewUtil.from(previousDecommissioningSchedule);

    assertModelProperties(sectionSummary, projectDetail);
    assertInteractions(currentProjectInformationView, previousProjectInformationView);
  }

  @Test
  public void getSummary_noProjectInformation() {
    when(decommissioningScheduleService.getDecommissioningSchedule(projectDetail)).thenReturn(Optional.empty());

    when(decommissioningScheduleService.getDecommissioningScheduleByProjectAndVersion(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )).thenReturn(Optional.empty());

    var sectionSummary = decommissioningScheduleSectionSummaryService.getSummary(projectDetail);
    assertModelProperties(sectionSummary, projectDetail);
    assertInteractions(new DecommissioningScheduleView(), new DecommissioningScheduleView());
  }

  private void assertModelProperties(ProjectSectionSummary projectSectionSummary, ProjectDetail projectDetail) {

    assertThat(projectSectionSummary.getDisplayOrder()).isEqualTo(DecommissioningScheduleSectionSummaryService.DISPLAY_ORDER);
    assertThat(projectSectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(DecommissioningScheduleSectionSummaryService.SECTION_LINK));
    assertThat(projectSectionSummary.getTemplatePath()).isEqualTo(DecommissioningScheduleSectionSummaryService.TEMPLATE_PATH);

    var model = projectSectionSummary.getTemplateModel();

    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        projectDetail,
        DecommissioningScheduleSectionSummaryService.PAGE_NAME,
        DecommissioningScheduleSectionSummaryService.SECTION_ID
    );

    assertThat(model).containsOnlyKeys("decommissioningScheduleDiffModel");
  }

  private void assertInteractions(DecommissioningScheduleView currentDecommissioningScheduleView,
                                  DecommissioningScheduleView previousDecommissioningScheduleView) {
    verify(differenceService, times(1)).differentiate(
        currentDecommissioningScheduleView,
        previousDecommissioningScheduleView
    );
  }
}
