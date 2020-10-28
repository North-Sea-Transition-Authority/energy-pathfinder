package uk.co.ogauthority.pathfinder.service.project.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.summary.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.service.rendering.TemplateRenderingService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectSummaryViewServiceTest {

  @Mock
  private ProjectSummaryService projectSummaryService;

  @Mock
  private TemplateRenderingService templateRenderingService;

  private ProjectSummaryViewService projectSummaryViewService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setUp() {
    projectSummaryViewService = new ProjectSummaryViewService(projectSummaryService, templateRenderingService);
  }

  @Test
  public void getApplicationSummaryView_usingDetail() {
    var stubRender = "FAKE";
    var sectionName1 = "text";
    var sectionName2 = "text2";
    when(projectSummaryService.summarise(detail)).thenReturn(List.of(
        new ProjectSectionSummary( List.of(SidebarSectionLink.createAnchorLink(sectionName1, "#")), sectionName1, Map.of("test", "1"), 1),
        new ProjectSectionSummary(List.of(SidebarSectionLink.createAnchorLink(sectionName2, "#")), sectionName2, Map.of("test", "2"), 2)
    ));

    when(templateRenderingService.render(any(), any(), anyBoolean())).thenReturn(stubRender);

    var appSummaryView = projectSummaryViewService.getProjectSummaryView(detail);

    assertThat(appSummaryView.getSummaryHtml()).isEqualTo(stubRender+stubRender);
    assertThat(appSummaryView.getSidebarSectionLinks())
        .extracting(SidebarSectionLink::getDisplayText)
        .containsExactly(sectionName1, sectionName2);


  }

}
