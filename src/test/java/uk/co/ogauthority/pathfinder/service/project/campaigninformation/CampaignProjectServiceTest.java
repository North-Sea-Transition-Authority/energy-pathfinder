package uk.co.ogauthority.pathfinder.service.project.campaigninformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.SelectableProject;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignInformation;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignProject;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.project.campaigninformation.CampaignInformationForm;
import uk.co.ogauthority.pathfinder.model.view.campaigninformation.CampaignProjectView;
import uk.co.ogauthority.pathfinder.repository.project.campaigninformation.CampaignProjectRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class CampaignProjectServiceTest {

  @Mock
  private CampaignProjectRestService campaignProjectRestService;

  @Mock
  private CampaignProjectRepository campaignProjectRepository;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private ProjectDetail projectDetail;

  private CampaignInformation campaignInformation;

  private CampaignProjectService campaignProjectService;

  @Before
  public void setup() {
    campaignProjectService = new CampaignProjectService(
        campaignProjectRestService,
        campaignProjectRepository,
        entityDuplicationService
    );

    projectDetail = ProjectUtil.getProjectDetails();

    campaignInformation = new CampaignInformation();
    campaignInformation.setProjectDetail(projectDetail);
  }

  @Test
  public void persistCampaignProjects_whenProjectNotIncludedInCampaign_verifyInteractions() {

    campaignInformation.setIsPartOfCampaign(false);

    // add some ids to the list to ensure when not part of a campaign property takes priority over a populated list
    final var campaignProjectsIds = List.of(10, 20, 30);

    campaignProjectService.persistCampaignProjects(campaignInformation, campaignProjectsIds);

    verify(campaignProjectRestService, never()).getSelectableProjectsByIdIn(campaignProjectsIds);
    verify(campaignProjectRepository, times(1)).deleteAllByCampaignInformation(campaignInformation);
    verify(campaignProjectRepository, never()).saveAll(anyList());
  }

  @Test
  public void persistCampaignProjects_whenProjectIncludedInCampaignAndProjectsSelected_verifyInteractions() {

    campaignInformation.setIsPartOfCampaign(true);

    final var campaignProjectsIds = List.of(10, 20, 30);

    when(campaignProjectRestService.getSelectableProjectsByIdIn(campaignProjectsIds))
        .thenReturn(List.of(
            new SelectableProject(),
            new SelectableProject(),
            new SelectableProject()
        ));

    campaignProjectService.persistCampaignProjects(campaignInformation, campaignProjectsIds);

    verify(campaignProjectRestService, times(1)).getSelectableProjectsByIdIn(campaignProjectsIds);
    verify(campaignProjectRepository, times(1)).deleteAllByCampaignInformation(campaignInformation);
    verify(campaignProjectRepository, times(1)).saveAll(anyList());
  }

  @Test
  public void persistCampaignProjects_whenProjectIncludedInCampaignAndNoProjectsSelected_verifyInteractions() {

    campaignInformation.setIsPartOfCampaign(true);

    List<Integer> campaignProjectsIds = Collections.emptyList();

    campaignProjectService.persistCampaignProjects(campaignInformation, campaignProjectsIds);

    verify(campaignProjectRestService, never()).getSelectableProjectsByIdIn(campaignProjectsIds);
    verify(campaignProjectRepository, times(1)).deleteAllByCampaignInformation(campaignInformation);
    verify(campaignProjectRepository, never()).saveAll(anyList());
  }

  @Test
  public void persistCampaignProjects_whenDuplicateProjectsSelected_ensureDuplicatesAreRemoved() {

    campaignInformation.setIsPartOfCampaign(true);

    final var campaignProjectsIdsWithDuplicates = List.of(10, 10);
    final var sanitisedCampaignProjectsIds = campaignProjectsIdsWithDuplicates
        .stream()
        .distinct()
        .collect(Collectors.toList());

    final var campaignProjects = new ArrayList<CampaignProject>();

    sanitisedCampaignProjectsIds.forEach(projectId -> {
      final var campaignProject = new CampaignProject();
      campaignProject.setCampaignInformation(campaignInformation);
      campaignProject.setProject(new SelectableProject());
      campaignProjects.add(campaignProject);
    });

    when(campaignProjectRestService.getSelectableProjectsByIdIn(sanitisedCampaignProjectsIds))
        .thenReturn(List.of(new SelectableProject()));

    campaignProjectService.persistCampaignProjects(campaignInformation, campaignProjectsIdsWithDuplicates);

    verify(campaignProjectRestService, times(1)).getSelectableProjectsByIdIn(sanitisedCampaignProjectsIds);
    verify(campaignProjectRepository, times(1)).deleteAllByCampaignInformation(campaignInformation);
    verify(campaignProjectRepository, times(1)).saveAll(campaignProjects);
  }

  @Test
  public void persistCampaignProjects_whenCurrentProjectIdIsSelected_ensureCurrentProjectIdIsRemoved() {

    final var currentProjectId = 100;
    projectDetail.getProject().setId(currentProjectId);

    campaignInformation.setIsPartOfCampaign(true);

    final var campaignProjectsIdsWithCurrentProjectId = List.of(10, currentProjectId);
    final var sanitisedCampaignProjectsIds = new ArrayList<>(campaignProjectsIdsWithCurrentProjectId);
    sanitisedCampaignProjectsIds.remove(Integer.valueOf(currentProjectId));

    final var campaignProjects = new ArrayList<CampaignProject>();

    sanitisedCampaignProjectsIds.forEach(projectId -> {
      final var campaignProject = new CampaignProject();
      campaignProject.setCampaignInformation(campaignInformation);
      campaignProject.setProject(new SelectableProject());
      campaignProjects.add(campaignProject);
    });

    when(campaignProjectRestService.getSelectableProjectsByIdIn(sanitisedCampaignProjectsIds))
        .thenReturn(List.of(new SelectableProject()));

    campaignProjectService.persistCampaignProjects(campaignInformation, campaignProjectsIdsWithCurrentProjectId);

    verify(campaignProjectRestService, times(1)).getSelectableProjectsByIdIn(sanitisedCampaignProjectsIds);
    verify(campaignProjectRepository, times(1)).deleteAllByCampaignInformation(campaignInformation);
    verify(campaignProjectRepository, times(1)).saveAll(campaignProjects);
  }

  @Test
  public void getCampaignProjectViews_whenNoResult_thenEmptyList() {

    final var resultingPublishedProjectViews = campaignProjectService.getCampaignProjectViews(
        new CampaignInformationForm()
    );

    assertThat(resultingPublishedProjectViews).isEmpty();
  }

  @Test
  public void getCampaignProjectViews_whenResult_thenPopulatedList() {

    final var selectableProject = new SelectableProject();
    final var campaignProjectView = new CampaignProjectView(selectableProject);
    final var campaignProjectViews = List.of(campaignProjectView);

    when(campaignProjectRestService.getSelectableProjectsByIdIn(anyList()))
        .thenReturn(List.of(selectableProject));

    final var populatedForm = new CampaignInformationForm();
    populatedForm.setProjectsIncludedInCampaign(List.of(1));

    final var resultingPublishedProjectViews = campaignProjectService.getCampaignProjectViews(populatedForm);

    assertThat(resultingPublishedProjectViews).isEqualTo(campaignProjectViews);

  }

  @Test
  public void getCampaignProjectViews_verifySortOrder() {

    final var firstAlphabeticallyCampaignProject = createCampaignProjectWithDisplayName("a project name");
    final var lastAlphabeticallyCampaignProject = createCampaignProjectWithDisplayName("z project name");

    final var unsortedCampaignProjectList = List.of(
        lastAlphabeticallyCampaignProject,
        firstAlphabeticallyCampaignProject
    );

    final var unsortedCampaignProjectIds = unsortedCampaignProjectList
        .stream()
        .map(campaignProject -> campaignProject.getProject().getProjectId())
        .collect(Collectors.toList());

    final var campaignForm = new CampaignInformationForm();
    campaignForm.setProjectsIncludedInCampaign(unsortedCampaignProjectIds);

    when(campaignProjectRestService.getSelectableProjectsByIdIn(campaignForm.getProjectsIncludedInCampaign()))
        .thenReturn(unsortedCampaignProjectList.stream().map(CampaignProject::getProject).collect(Collectors.toList()));

    final var resultingPublishedProjectViews = campaignProjectService.getCampaignProjectViews(campaignForm);

    assertThat(resultingPublishedProjectViews).extracting(campaignProjectView -> campaignProjectView.getSelectableProject().getProjectDisplayName()).containsExactly(
        firstAlphabeticallyCampaignProject.getProject().getProjectDisplayName(),
        lastAlphabeticallyCampaignProject.getProject().getProjectDisplayName()
    );
  }

  @Test
  public void getCampaignProjects_whenNoResults_thenEmptyList() {
    when(campaignProjectRepository.findAllByCampaignInformation_ProjectDetail(projectDetail))
        .thenReturn(Collections.emptyList());

    final var resultingCampaignProjects = campaignProjectService.getCampaignProjects(projectDetail);

    assertThat(resultingCampaignProjects).isEmpty();
  }

  @Test
  public void getCampaignProjects_whenResults_thenPopulatedList() {

    final var campaignProject = new CampaignProject();
    final var campaignProjectList = List.of(campaignProject);

    when(campaignProjectRepository.findAllByCampaignInformation_ProjectDetail(projectDetail))
        .thenReturn(campaignProjectList);

    final var resultingCampaignProjects = campaignProjectService.getCampaignProjects(projectDetail);

    assertThat(resultingCampaignProjects).isEqualTo(campaignProjectList);
  }

  @Test
  public void getCampaignProjectRestUrl() {
    final var expectedUrl = CampaignProjectRestService.getCampaignProjectRestUrl();
    final var resultingUrl = campaignProjectService.getCampaignProjectRestUrl();
    assertThat(resultingUrl).isEqualTo(expectedUrl);
  }

  @Test
  public void deleteAllCampaignProjects_whenCampaignInformationVariant_verifyInteractions() {
    campaignProjectService.deleteAllCampaignProjects(campaignInformation);
    verify(campaignProjectRepository, times(1)).deleteAllByCampaignInformation(campaignInformation);
  }

  @Test
  public void deleteAllCampaignProjects_whenProjectDetailVariant_verifyInteractions() {
    campaignProjectService.deleteAllCampaignProjects(projectDetail);
    verify(campaignProjectRepository, times(1)).deleteAllByCampaignInformation_ProjectDetail(projectDetail);
  }

  @Test
  public void copyCampaignProjectsToNewCampaign_verifyInteractions() {

    final var fromCampaignInformation = new CampaignInformation();
    fromCampaignInformation.setProjectDetail(projectDetail);

    final var toCampaignInformation = new CampaignInformation();
    toCampaignInformation.setProjectDetail(ProjectUtil.getProjectDetails(ProjectStatus.QA));

    final var campaignProjects = List.of(new CampaignProject());

    when(campaignProjectRepository.findAllByCampaignInformation_ProjectDetail(fromCampaignInformation.getProjectDetail()))
        .thenReturn(campaignProjects);

    campaignProjectService.copyCampaignProjectsToNewCampaign(
        fromCampaignInformation,
        toCampaignInformation
    );

    verify(campaignProjectRepository, times(1)).findAllByCampaignInformation_ProjectDetail(
        fromCampaignInformation.getProjectDetail()
    );

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        campaignProjects,
        toCampaignInformation,
        CampaignProject.class
    );

  }

  private CampaignProject createCampaignProjectWithDisplayName(String displayName) {
    final var publishedProject = new SelectableProject();
    publishedProject.setProjectDisplayName(displayName);

    final var campaignProject = new CampaignProject();
    campaignProject.setProject(publishedProject);

    return campaignProject;
  }

}