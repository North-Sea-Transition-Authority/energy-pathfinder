package uk.co.ogauthority.pathfinder.service.project.projectinformation;

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
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectInformationSummaryServiceTest {

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();
  private final ProjectInformation projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(detail);

  @Mock
  private ProjectInformationService projectInformationService;

  private ProjectInformationSummaryService projectInformationSummaryService;

  @Before
  public void setUp() throws Exception {
    projectInformationSummaryService = new ProjectInformationSummaryService(projectInformationService);
    when(projectInformationService.getProjectInformation(detail)).thenReturn(Optional.of(projectInformation));
  }

  @Test
  public void getSummary() {
    when(projectInformationService.getProjectInformation(detail)).thenReturn(Optional.of(projectInformation));
    var sectionSummary = projectInformationSummaryService.getSummary(detail);
    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(ProjectInformationSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(ProjectInformationSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(ProjectInformationSummaryService.TEMPLATE_PATH);

    assertThat(model).containsOnly(
        entry("sectionTitle", ProjectInformationSummaryService.PAGE_NAME),
        entry("sectionId", ProjectInformationSummaryService.SECTION_ID),
        entry("projectTitle", projectInformation.getProjectTitle()),
        entry("projectSummary", projectInformation.getProjectSummary()),
        entry("fieldStage", projectInformation.getFieldStage().getDisplayName()),
        entry("developmentRelated", false),
        entry("discoveryRelated", false),
        entry("decomRelated", true),
        entry("developmentFirstProductionDate", ""),
        entry("discoveryFirstProductionDate", ""),
        entry("decomWorkStartDate", DateUtil.getDateFromQuarterYear(
            projectInformation.getDecomWorkStartDateQuarter(),
            projectInformation.getDecomWorkStartDateYear()
        )),
        entry("decomProductionCessationDate", DateUtil.format(projectInformation.getProductionCessationDate())),
        entry("name", projectInformation.getName()),
        entry("phoneNumber", projectInformation.getPhoneNumber()),
        entry("jobTitle", projectInformation.getJobTitle()),
        entry("emailAddress", projectInformation.getEmailAddress())
    );
  }

  @Test
  public void getSummary_noProjectInformation() {
    when(projectInformationService.getProjectInformation(detail)).thenReturn(Optional.empty());
    var sectionSummary = projectInformationSummaryService.getSummary(detail);
    var model = sectionSummary.getTemplateModel();
    assertThat(sectionSummary.getDisplayOrder()).isEqualTo(ProjectInformationSummaryService.DISPLAY_ORDER);
    assertThat(sectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(ProjectInformationSummaryService.SECTION_LINK));
    assertThat(sectionSummary.getTemplatePath()).isEqualTo(ProjectInformationSummaryService.TEMPLATE_PATH);
    assertThat(model).containsOnly(
        entry("sectionTitle", ProjectInformationSummaryService.PAGE_NAME),
        entry("sectionId", ProjectInformationSummaryService.SECTION_ID)
    );
  }
}
