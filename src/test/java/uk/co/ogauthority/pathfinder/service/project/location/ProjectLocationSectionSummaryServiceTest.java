package uk.co.ogauthority.pathfinder.service.project.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.view.location.ProjectLocationView;
import uk.co.ogauthority.pathfinder.model.view.location.ProjectLocationViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@ExtendWith(MockitoExtension.class)
public class ProjectLocationSectionSummaryServiceTest {

  @Mock
  private ProjectLocationService projectLocationService;

  @Mock
  private ProjectLocationBlocksService projectLocationBlocksService;

  @Mock
  private DifferenceService differenceService;

  @Mock
  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  @Mock
  private ProjectInformationService projectInformationService;

  private ProjectLocationSectionSummaryService projectLocationSectionSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();
  private final ProjectLocation projectLocation = ProjectLocationTestUtil.getProjectLocation(detail);

  @BeforeEach
  public void setup() {
    projectLocationSectionSummaryService = new ProjectLocationSectionSummaryService(
        projectLocationService,
        projectLocationBlocksService,
        differenceService,
        projectSectionSummaryCommonModelService,
        projectInformationService
    );
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(projectLocationService.isTaskValidForProjectDetail(detail)).thenReturn(true);

    assertThat(projectLocationSectionSummaryService.canShowSection(detail)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(projectLocationService.isTaskValidForProjectDetail(detail)).thenReturn(false);

    assertThat(projectLocationSectionSummaryService.canShowSection(detail)).isFalse();
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  public void getSummary(boolean isOilAndGasProject, boolean previouslyOilAndGasProject) {
    var previousProjectDetail = ProjectUtil.getProjectDetails();
    previousProjectDetail.setVersion(detail.getVersion() - 1);
    var previousProjectLocation = ProjectLocationTestUtil.getProjectLocation(previousProjectDetail);

    when(projectInformationService.isOilAndGasProject(detail)).thenReturn(isOilAndGasProject);
    when(projectInformationService.isOilAndGasProject(previousProjectDetail)).thenReturn(previouslyOilAndGasProject);

    when(projectLocationService.getProjectLocationByProjectDetail(detail)).thenReturn(Optional.of(projectLocation));

    when(projectLocationService.getProjectLocationByProjectAndVersion(
        detail.getProject(),
        detail.getVersion() - 1
    )).thenReturn(Optional.of(previousProjectLocation));

    var sectionSummary = projectLocationSectionSummaryService.getSummary(detail);

    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(ProjectLocationSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(ProjectLocationSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(ProjectLocationSectionSummaryService.TEMPLATE_PATH);

    var currentProjectLocationView = ProjectLocationViewUtil.from(projectLocation, isOilAndGasProject, Collections.emptyList());
    var previousProjectLocationView =
        ProjectLocationViewUtil.from(previousProjectLocation, previouslyOilAndGasProject, Collections.emptyList());

    assertModelProperties(sectionSummary, detail);
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
    assertModelProperties(sectionSummary, detail);
    assertInteractions(new ProjectLocationView(), new ProjectLocationView());
  }

  private void assertModelProperties(ProjectSectionSummary projectSectionSummary, ProjectDetail projectDetail) {
    assertThat(projectSectionSummary.getDisplayOrder()).isEqualTo(ProjectLocationSectionSummaryService.DISPLAY_ORDER);
    assertThat(projectSectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(ProjectLocationSectionSummaryService.SECTION_LINK));
    assertThat(projectSectionSummary.getTemplatePath()).isEqualTo(ProjectLocationSectionSummaryService.TEMPLATE_PATH);

    var model = projectSectionSummary.getTemplateModel();

    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        projectDetail,
        ProjectLocationSectionSummaryService.PAGE_NAME,
        ProjectLocationSectionSummaryService.SECTION_ID
    );

    assertThat(model).containsOnlyKeys(
        "isOilAndGasProject",
        "projectLocationDiffModel",
        "hasApprovedFieldDevelopmentPlan",
        "hasApprovedDecomProgram"
    );
  }

  private void assertInteractions(ProjectLocationView currentProjectLocationView,
                                  ProjectLocationView previousProjectLocationView) {
    verify(differenceService, times(1)).differentiate(
        currentProjectLocationView,
        previousProjectLocationView
    );
  }
}
