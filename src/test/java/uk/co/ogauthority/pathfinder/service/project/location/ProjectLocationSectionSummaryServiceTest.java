package uk.co.ogauthority.pathfinder.service.project.location;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
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
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectLocationSectionSummaryServiceTest {

  @Mock
  private ProjectLocationService projectLocationService;

  @Mock
  private ProjectLocationBlocksService projectLocationBlocksService;

  private ProjectLocationSectionSummaryService projectLocationSectionSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();
  private final ProjectLocation projectLocation = ProjectLocationTestUtil.getProjectLocation_withField(detail);

  @Before
  public void setup() {
    projectLocationSectionSummaryService = new ProjectLocationSectionSummaryService(
        projectLocationService,
        projectLocationBlocksService
    );
  }

  @Test
  public void getSummary() {
    when(projectLocationService.findByProjectDetail(detail)).thenReturn(Optional.of(projectLocation));
    var sectionSummary = projectLocationSectionSummaryService.getSummary(detail);
    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(ProjectLocationSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(ProjectLocationSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(ProjectLocationSectionSummaryService.TEMPLATE_PATH);

    var projectLocationView = ProjectLocationViewUtil.from(projectLocation, Collections.emptyList());

    assertThat(model).containsOnly(
        entry("sectionTitle", ProjectLocationSectionSummaryService.PAGE_NAME),
        entry("sectionId", ProjectLocationSectionSummaryService.SECTION_ID),
        entry("projectLocationView", projectLocationView)
    );
  }

  @Test
  public void getSummary_noProjectLocation() {
    when(projectLocationService.findByProjectDetail(detail)).thenReturn(Optional.empty());
    var sectionSummary = projectLocationSectionSummaryService.getSummary(detail);
    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(ProjectLocationSectionSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(ProjectLocationSectionSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(ProjectLocationSectionSummaryService.TEMPLATE_PATH);

    var projectLocationView = new ProjectLocationView();

    assertThat(model).containsOnly(
        entry("sectionTitle", ProjectLocationSectionSummaryService.PAGE_NAME),
        entry("sectionId", ProjectLocationSectionSummaryService.SECTION_ID),
        entry("projectLocationView", projectLocationView)
    );
  }
}
