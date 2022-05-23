package uk.co.ogauthority.pathfinder.service.project.projectinformation;

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
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.view.projectinformation.ProjectInformationView;
import uk.co.ogauthority.pathfinder.model.view.projectinformation.ProjectInformationViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectInformationSectionSummaryServiceTest {

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();
  private final ProjectInformation projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(detail);

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private DifferenceService differenceService;

  @Mock
  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  private ProjectInformationSectionSummaryService projectInformationSectionSummaryService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  @Before
  public void setUp() throws Exception {
    projectInformationSectionSummaryService = new ProjectInformationSectionSummaryService(
        projectInformationService,
        differenceService,
        projectSectionSummaryCommonModelService
    );
    when(projectInformationService.getProjectInformation(detail)).thenReturn(Optional.of(projectInformation));
  }

  @Test
  public void getSummary() {

    when(projectInformationService.getProjectInformation(detail)).thenReturn(Optional.of(projectInformation));

    var previousProjectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(detail);
    previousProjectInformation.setProjectTitle("Previous project information");

    when(projectInformationService.getProjectInformationByProjectAndVersion(
        detail.getProject(),
        detail.getVersion() - 1
    )).thenReturn(Optional.of(previousProjectInformation));

    var sectionSummary = projectInformationSectionSummaryService.getSummary(detail);

    var currentProjectInformationView = ProjectInformationViewUtil.from(projectInformation);
    var previousProjectInformationView = ProjectInformationViewUtil.from(previousProjectInformation);

    assertModelProperties(sectionSummary, detail);
    assertInteractions(currentProjectInformationView, previousProjectInformationView);
  }

  @Test
  public void getSummary_noProjectInformation() {
    when(projectInformationService.getProjectInformation(detail)).thenReturn(Optional.empty());

    when(projectInformationService.getProjectInformationByProjectAndVersion(
        detail.getProject(),
        detail.getVersion() - 1
    )).thenReturn(Optional.empty());

    var sectionSummary = projectInformationSectionSummaryService.getSummary(detail);
    assertModelProperties(sectionSummary, detail);
    assertInteractions(new ProjectInformationView(), new ProjectInformationView());
  }

  private void assertModelProperties(ProjectSectionSummary projectSectionSummary, ProjectDetail projectDetail) {

    assertThat(projectSectionSummary.getDisplayOrder()).isEqualTo(ProjectInformationSectionSummaryService.DISPLAY_ORDER);
    assertThat(projectSectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(ProjectInformationSectionSummaryService.SECTION_LINK));
    assertThat(projectSectionSummary.getTemplatePath()).isEqualTo(ProjectInformationSectionSummaryService.TEMPLATE_PATH);

    var model = projectSectionSummary.getTemplateModel();

    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        projectDetail,
        ProjectInformationSectionSummaryService.PAGE_NAME,
        ProjectInformationSectionSummaryService.SECTION_ID
    );

    assertThat(model).containsOnlyKeys(
        "projectInformationDiffModel",
        "isDevelopmentFieldStage",
        "isDiscoveryFieldStage",
        "isEnergyTransitionFieldStage"
    );
  }

  private void assertInteractions(ProjectInformationView currentProjectInformationView,
                                  ProjectInformationView previousProjectInformationView) {
    verify(differenceService, times(1)).differentiate(
        currentProjectInformationView,
        previousProjectInformationView
    );
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(projectInformationService.isTaskValidForProjectDetail(details)).thenReturn(true);

    assertThat(projectInformationSectionSummaryService.canShowSection(details)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(projectInformationService.isTaskValidForProjectDetail(details)).thenReturn(false);

    assertThat(projectInformationSectionSummaryService.canShowSection(details)).isFalse();
  }
}
