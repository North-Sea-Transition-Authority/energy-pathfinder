package uk.co.ogauthority.pathfinder.service.project.location;

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
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectLocationSummaryServiceTest {

  @Mock
  private ProjectLocationService projectLocationService;

  @Mock
  private ProjectLocationBlocksService projectLocationBlocksService;

  private ProjectLocationSummaryService projectLocationSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();
  private final ProjectLocation projectLocation = ProjectLocationTestUtil.getProjectLocation_withField(detail);

  @Before
  public void setup() {
    projectLocationSummaryService = new ProjectLocationSummaryService(
        projectLocationService,
        projectLocationBlocksService
    );
  }

  @Test
  public void getSummary() {
    when(projectLocationService.findByProjectDetail(detail)).thenReturn(Optional.of(projectLocation));
    var sectionSummary = projectLocationSummaryService.getSummary(detail);
    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(ProjectLocationSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(ProjectLocationSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(ProjectLocationSummaryService.TEMPLATE_PATH);

    var projectLocationView = model.get("projectLocationView");
    assertThat(projectLocationView).isNotNull();

    assertThat(model).containsOnly(
        entry("sectionTitle", ProjectLocationSummaryService.PAGE_NAME),
        entry("sectionId", ProjectLocationSummaryService.SECTION_ID),
        entry("projectLocationView", projectLocationView)
    );
  }

  @Test
  public void getSummary_noProjectLocation() {
    when(projectLocationService.findByProjectDetail(detail)).thenReturn(Optional.empty());
    var sectionSummary = projectLocationSummaryService.getSummary(detail);
    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(ProjectLocationSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(ProjectLocationSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(ProjectLocationSummaryService.TEMPLATE_PATH);

    var projectLocationView = model.get("projectLocationView");
    assertThat(projectLocationView).isNotNull();

    assertThat(model).containsOnly(
        entry("sectionTitle", ProjectLocationSummaryService.PAGE_NAME),
        entry("sectionId", ProjectLocationSummaryService.SECTION_ID),
        entry("projectLocationView", projectLocationView)
    );
  }
}
