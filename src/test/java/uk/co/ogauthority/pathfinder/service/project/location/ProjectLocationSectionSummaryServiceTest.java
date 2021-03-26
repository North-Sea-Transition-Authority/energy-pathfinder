package uk.co.ogauthority.pathfinder.service.project.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.view.location.ProjectLocationView;
import uk.co.ogauthority.pathfinder.model.view.location.ProjectLocationViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectLocationSectionSummaryServiceTest {

  @Mock
  private ProjectLocationService projectLocationService;

  @Mock
  private ProjectLocationBlocksService projectLocationBlocksService;

  @Mock
  private DifferenceService differenceService;

  private ProjectLocationSectionSummaryService projectLocationSectionSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();
  private final ProjectLocation projectLocation = ProjectLocationTestUtil.getProjectLocation(detail);

  @Before
  public void setup() {
    projectLocationSectionSummaryService = new ProjectLocationSectionSummaryService(
        projectLocationService,
        projectLocationBlocksService,
        differenceService
    );
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(projectLocationService.canShowInTaskList(detail)).thenReturn(true);

    assertThat(projectLocationSectionSummaryService.canShowSection(detail)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(projectLocationService.canShowInTaskList(detail)).thenReturn(false);

    assertThat(projectLocationSectionSummaryService.canShowSection(detail)).isFalse();
  }

  @Test
  public void getSummary() {
    when(projectLocationService.getProjectLocationByProjectDetail(detail)).thenReturn(Optional.of(projectLocation));

    var previousProjectLocation = ProjectLocationTestUtil.getProjectLocation(detail);

    when(projectLocationService.getProjectLocationByProjectAndVersion(
        detail.getProject(),
        detail.getVersion() - 1
    )).thenReturn(Optional.of(previousProjectLocation));

    var sectionSummary = projectLocationSectionSummaryService.getSummary(detail);

    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(ProjectLocationSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(ProjectLocationSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(ProjectLocationSectionSummaryService.TEMPLATE_PATH);

    var currentProjectLocationView = ProjectLocationViewUtil.from(projectLocation, Collections.emptyList());
    var previousProjectLocationView = ProjectLocationViewUtil.from(previousProjectLocation, Collections.emptyList());

    assertModelProperties(sectionSummary);
    assertInteractions(currentProjectLocationView, previousProjectLocationView);
  }

  @Test
  public void getSummary_noProjectLocation() {
    when(projectLocationService.getProjectLocationByProjectDetail(detail)).thenReturn(Optional.empty());

    when(projectLocationService.getProjectLocationByProjectAndVersion(
        detail.getProject(),
        detail.getVersion() - 1
    )).thenReturn(Optional.empty());

    var sectionSummary = projectLocationSectionSummaryService.getSummary(detail);
    assertModelProperties(sectionSummary);
    assertInteractions(new ProjectLocationView(), new ProjectLocationView());
  }

  private void assertModelProperties(ProjectSectionSummary projectSectionSummary) {
    assertThat(projectSectionSummary.getDisplayOrder()).isEqualTo(ProjectLocationSectionSummaryService.DISPLAY_ORDER);
    assertThat(projectSectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(ProjectLocationSectionSummaryService.SECTION_LINK));
    assertThat(projectSectionSummary.getTemplatePath()).isEqualTo(ProjectLocationSectionSummaryService.TEMPLATE_PATH);

    var model = projectSectionSummary.getTemplateModel();

    assertThat(model).containsOnlyKeys(
        "sectionTitle",
        "sectionId",
        "projectLocationDiffModel",
        "hasApprovedFieldDevelopmentPlan",
        "hasApprovedDecomProgram"
    );

    assertThat(model).containsEntry("sectionTitle", ProjectLocationSectionSummaryService.PAGE_NAME);
    assertThat(model).containsEntry("sectionId", ProjectLocationSectionSummaryService.SECTION_ID);
  }

  private void assertInteractions(ProjectLocationView currentProjectLocationView,
                                  ProjectLocationView previousProjectLocationView) {
    verify(differenceService, times(1)).differentiate(
        currentProjectLocationView,
        previousProjectLocationView
    );
  }
}
