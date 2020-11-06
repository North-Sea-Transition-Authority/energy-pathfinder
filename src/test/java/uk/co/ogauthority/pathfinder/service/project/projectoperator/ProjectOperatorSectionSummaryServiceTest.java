package uk.co.ogauthority.pathfinder.service.project.projectoperator;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.view.projectoperator.ProjectOperatorView;
import uk.co.ogauthority.pathfinder.model.view.projectoperator.ProjectOperatorViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectOperatorSectionSummaryServiceTest {

  @Mock
  private ProjectOperatorService projectOperatorService;

  private ProjectOperatorSectionSummaryService projectOperatorSectionSummaryService;

  @Before
  public void setup() {
    projectOperatorSectionSummaryService = new ProjectOperatorSectionSummaryService(projectOperatorService);
  }

  @Test
  public void getSummary_whenProjectOperatorExists_thenProjectOperatorViewPopulated() {

    final var projectOperator = ProjectOperatorTestUtil.getOperator();
    final var projectOperatorView = ProjectOperatorViewUtil.from(projectOperator);
    final var projectDetail = projectOperator.getProjectDetail();

    when(projectOperatorService.getProjectOperatorByProjectDetail(projectDetail))
        .thenReturn(Optional.of(projectOperator));

    final var sectionSummary = projectOperatorSectionSummaryService.getSummary(projectDetail);

    assertModelProperties(sectionSummary, projectOperatorView);
  }

  @Test
  public void getSummary_whenProjectOperatorNotExist_thenProjectOperatorViewNotPopulated() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var projectOperatorView = new ProjectOperatorView();

    when(projectOperatorService.getProjectOperatorByProjectDetail(projectDetail))
        .thenReturn(Optional.empty());

    final var sectionSummary = projectOperatorSectionSummaryService.getSummary(projectDetail);

    assertModelProperties(sectionSummary, projectOperatorView);

  }

  private void assertModelProperties(ProjectSectionSummary sectionSummary, ProjectOperatorView projectOperatorView) {

    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(ProjectOperatorSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(ProjectOperatorSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(ProjectOperatorSectionSummaryService.TEMPLATE_PATH);

    var model = sectionSummary.getTemplateModel();

    assertThat(model).containsOnly(
        entry("sectionTitle", ProjectOperatorSectionSummaryService.PAGE_NAME),
        entry("sectionId", ProjectOperatorSectionSummaryService.SECTION_ID),
        entry("projectOperatorView", projectOperatorView)
    );
  }

}