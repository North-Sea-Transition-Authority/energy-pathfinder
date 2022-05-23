package uk.co.ogauthority.pathfinder.service.project.campaigninformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignInformation;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.view.campaigninformation.CampaignInformationView;
import uk.co.ogauthority.pathfinder.model.view.campaigninformation.CampaignInformationViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class CampaignInformationSectionSummaryServiceTest {

  @Mock
  private CampaignInformationService campaignInformationService;

  @Mock
  private CampaignProjectService campaignProjectService;

  @Mock
  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  @Mock
  private DifferenceService differenceService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private CampaignInformationSectionSummaryService campaignInformationSectionSummaryService;

  @Before
  public void setup() {
    campaignInformationSectionSummaryService = new CampaignInformationSectionSummaryService(
        campaignInformationService,
        campaignProjectService,
        projectSectionSummaryCommonModelService,
        differenceService
    );
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {

    when(campaignInformationService.isTaskValidForProjectDetail(projectDetail)).thenReturn(true);

    final var canShowSection = campaignInformationSectionSummaryService.canShowSection(projectDetail);

    assertThat(canShowSection).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {

    when(campaignInformationService.isTaskValidForProjectDetail(projectDetail)).thenReturn(false);

    final var canShowSection = campaignInformationSectionSummaryService.canShowSection(projectDetail);

    assertThat(canShowSection).isFalse();
  }

  @Test
  public void getSummary_whenCampaignInformationFound_verifyInteractions() {

    final var currentCampaignInformation = new CampaignInformation();
    currentCampaignInformation.setScopeDescription("current");

    final var currentProjectDetail = projectDetail;
    currentCampaignInformation.setProjectDetail(currentProjectDetail);

    when(campaignInformationService.getCampaignInformationByProjectDetail(currentProjectDetail))
        .thenReturn(Optional.of(currentCampaignInformation));

    final var expectedCurrentCampaignInformationView = CampaignInformationViewUtil.from(currentCampaignInformation, List.of());

    final var previousCampaignInformation = new CampaignInformation();
    previousCampaignInformation.setScopeDescription("previous");

    final var previousProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    previousCampaignInformation.setProjectDetail(previousProjectDetail);

    when(campaignInformationService.getCampaignInformationByProjectAndVersion(
        currentProjectDetail.getProject(),
        currentProjectDetail.getVersion() - 1
    )).thenReturn(Optional.of(previousCampaignInformation));

    final var expectedPreviousCampaignInformationView = CampaignInformationViewUtil.from(previousCampaignInformation, List.of());

    final var sectionSummary = campaignInformationSectionSummaryService.getSummary(currentProjectDetail);

    verify(campaignProjectService, times(1)).getCampaignProjects(currentProjectDetail);
    verify(campaignProjectService, times(1)).getCampaignProjects(previousProjectDetail);

    verify(differenceService, times(1)).differentiate(
        expectedCurrentCampaignInformationView,
        expectedPreviousCampaignInformationView
    );

    assertModelProperties(sectionSummary, currentProjectDetail);
  }

  @Test
  public void getSummary_whenCampaignInformationNotFound_verifyInteractions() {

    when(campaignInformationService.getCampaignInformationByProjectDetail(projectDetail))
        .thenReturn(Optional.empty());

    when(campaignInformationService.getCampaignInformationByProjectAndVersion(
        projectDetail.getProject(),
        projectDetail.getVersion() - 1
    )).thenReturn(Optional.empty());

    final var sectionSummary = campaignInformationSectionSummaryService.getSummary(projectDetail);

    verify(campaignProjectService, never()).getCampaignProjects(any());

    final var emptyCampaignInformationView = new CampaignInformationView();

    verify(differenceService, times(1)).differentiate(
        emptyCampaignInformationView,
        emptyCampaignInformationView
    );

    assertModelProperties(sectionSummary, projectDetail);

  }

  private void assertModelProperties(ProjectSectionSummary projectSectionSummary, ProjectDetail projectDetail) {
    assertThat(projectSectionSummary.getDisplayOrder()).isEqualTo(CampaignInformationSectionSummaryService.DISPLAY_ORDER);
    assertThat(projectSectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(CampaignInformationSectionSummaryService.SECTION_LINK));
    assertThat(projectSectionSummary.getTemplatePath()).isEqualTo(CampaignInformationSectionSummaryService.TEMPLATE_PATH);

    var model = projectSectionSummary.getTemplateModel();

    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        projectDetail,
        CampaignInformationSectionSummaryService.PAGE_NAME,
        CampaignInformationSectionSummaryService.SECTION_ID
    );

    assertThat(model).containsOnlyKeys(
        "isProjectIncludedInCampaign",
        "campaignInformationDiffModel",
        ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR,
        ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR
    );
  }

}