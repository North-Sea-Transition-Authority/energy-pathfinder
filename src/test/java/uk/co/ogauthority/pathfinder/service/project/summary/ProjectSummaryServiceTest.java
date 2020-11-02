package uk.co.ogauthority.pathfinder.service.project.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationSectionSummaryService;
import uk.co.ogauthority.pathfinder.testutil.ProjectSectionSummaryTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectSummaryServiceTest {

  @Mock
  private ProjectInformationSectionSummaryService projectInformationSectionSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private ProjectSummaryService projectSummaryService;

  @Before
  public void setUp() throws Exception {
    projectSummaryService = new ProjectSummaryService(List.of(projectInformationSectionSummaryService));
    when(projectInformationSectionSummaryService.getSummary(detail)).thenReturn(
        ProjectSectionSummaryTestUtil.getSummary()
    );
  }

  @Test
  public void summarise_sectionAppearsForDetail() {
    when(projectInformationSectionSummaryService.canShowSection(detail)).thenReturn(true);
    var summaryList = projectSummaryService.summarise(detail);
    assertThat(summaryList.size()).isEqualTo(1);
    assertThat(summaryList.get(0).getDisplayOrder()).isEqualTo(1);
    assertThat(summaryList.get(0).getTemplatePath()).isEqualTo(ProjectSectionSummaryTestUtil.TEMPLATE_PATH);
    assertThat(summaryList.get(0).getSidebarSectionLinks()).isEqualTo(ProjectSectionSummaryTestUtil.SIDEBAR_SECTION_LINKS);
    assertThat(summaryList.get(0).getTemplateModel()).isEqualTo(ProjectSectionSummaryTestUtil.TEMPLATE_MODEL);
  }

  @Test
  public void summarise_sectionDoesNotAppearForDetail() {
    when(projectInformationSectionSummaryService.canShowSection(detail)).thenReturn(false);
    var summaryList = projectSummaryService.summarise(detail);
    assertThat(summaryList.size()).isEqualTo(0);
  }
}
