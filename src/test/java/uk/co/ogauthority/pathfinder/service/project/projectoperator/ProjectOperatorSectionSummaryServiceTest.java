package uk.co.ogauthority.pathfinder.service.project.projectoperator;

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
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.view.projectoperator.ProjectOperatorView;
import uk.co.ogauthority.pathfinder.model.view.projectoperator.ProjectOperatorViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.selectoperator.SelectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectOperatorSectionSummaryServiceTest {

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private DifferenceService differenceService;

  @Mock
  private SelectOperatorService selectOperatorService;

  @Mock
  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  private ProjectOperatorSectionSummaryService projectOperatorSectionSummaryService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    projectOperatorSectionSummaryService = new ProjectOperatorSectionSummaryService(
        projectOperatorService,
        differenceService,
        selectOperatorService,
        projectSectionSummaryCommonModelService
    );
  }

  @Test
  public void getSummary_whenProjectOperatorExists_thenProjectOperatorViewPopulated() {

    final var currentProjectOperator = ProjectOperatorTestUtil.getOperator(
        details,
        TeamTestingUtil.generateOrganisationGroup(
            10,
            "TEST",
            "TEST"
        )
    );
    final var currentProjectOperatorView = ProjectOperatorViewUtil.from(currentProjectOperator);

    final var previousProjectOperator = ProjectOperatorTestUtil.getOperator(
        details,
        TeamTestingUtil.generateOrganisationGroup(
          20,
          "TEST2",
          "TEST2"
        )
    );
    final var previousProjectOperatorView = ProjectOperatorViewUtil.from(previousProjectOperator);

    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(details))
        .thenReturn(currentProjectOperator);

    when(projectOperatorService.getProjectOperatorByProjectAndVersion(
        details.getProject(),
        details.getVersion() - 1
    ))
        .thenReturn(Optional.of(previousProjectOperator));

    final var sectionSummary = projectOperatorSectionSummaryService.getSummary(details);

    assertModelProperties(sectionSummary, details);
    assertInteractions(currentProjectOperatorView, previousProjectOperatorView);
  }

  @Test
  public void getSummary_whenProjectOperatorNotExist_thenProjectOperatorViewNotPopulated() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    final var currentProjectOperator = new ProjectOperator();
    final var currentProjectOperatorView = ProjectOperatorViewUtil.from(currentProjectOperator);
    final var previousProjectOperatorView = new ProjectOperatorView();

    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail))
        .thenReturn(currentProjectOperator);

    when(projectOperatorService.getProjectOperatorByProjectAndVersion(projectDetail.getProject(), projectDetail.getVersion()-1))
        .thenReturn(Optional.empty());

    final var sectionSummary = projectOperatorSectionSummaryService.getSummary(projectDetail);

    assertModelProperties(sectionSummary, projectDetail);
    assertInteractions(currentProjectOperatorView, previousProjectOperatorView);

  }

  private void assertModelProperties(ProjectSectionSummary sectionSummary, ProjectDetail projectDetail) {

    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(ProjectOperatorSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(ProjectOperatorSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(ProjectOperatorSectionSummaryService.TEMPLATE_PATH);

    var model = sectionSummary.getTemplateModel();

    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        projectDetail,
        ProjectOperatorSectionSummaryService.PAGE_NAME,
        ProjectOperatorSectionSummaryService.SECTION_ID
    );

    assertThat(model).containsOnlyKeys("isPublishedAsOperator", "projectOperatorDiffModel");
  }

  private void assertInteractions(ProjectOperatorView currentProjectOperatorView, ProjectOperatorView previousProjectOperatorView) {
    verify(differenceService, times(1)).differentiate(currentProjectOperatorView, previousProjectOperatorView);
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(selectOperatorService.canShowInTaskList(details)).thenReturn(true);

    assertThat(projectOperatorSectionSummaryService.canShowSection(details)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(selectOperatorService.canShowInTaskList(details)).thenReturn(false);

    assertThat(projectOperatorSectionSummaryService.canShowSection(details)).isFalse();
  }

}