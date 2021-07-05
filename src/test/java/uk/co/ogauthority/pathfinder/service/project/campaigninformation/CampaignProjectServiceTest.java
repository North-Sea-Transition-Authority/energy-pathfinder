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
import uk.co.ogauthority.pathfinder.model.entity.project.PublishedProject;
import uk.co.ogauthority.pathfinder.model.entity.project.PublishedProjectView;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignInformation;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignProject;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.project.campaigninformation.CampaignInformationForm;
import uk.co.ogauthority.pathfinder.repository.project.campaigninformation.CampaignProjectRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.PublishedProjectAccessorService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class CampaignProjectServiceTest {

  @Mock
  private PublishedProjectAccessorService publishedProjectAccessorService;

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
        publishedProjectAccessorService,
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

    verify(publishedProjectAccessorService, never()).getPublishedProjectsByIdIn(campaignProjectsIds);
    verify(campaignProjectRepository, times(1)).deleteAllByCampaignInformation(campaignInformation);
    verify(campaignProjectRepository, never()).saveAll(anyList());
  }

  @Test
  public void persistCampaignProjects_whenProjectIncludedInCampaignAndProjectsSelected_verifyInteractions() {

    campaignInformation.setIsPartOfCampaign(true);

    final var campaignProjectsIds = List.of(10, 20, 30);

    when(publishedProjectAccessorService.getPublishedProjectsByIdIn(campaignProjectsIds))
        .thenReturn(List.of(
            new PublishedProject(),
            new PublishedProject(),
            new PublishedProject()
        ));

    campaignProjectService.persistCampaignProjects(campaignInformation, campaignProjectsIds);

    verify(publishedProjectAccessorService, times(1)).getPublishedProjectsByIdIn(campaignProjectsIds);
    verify(campaignProjectRepository, times(1)).deleteAllByCampaignInformation(campaignInformation);
    verify(campaignProjectRepository, times(1)).saveAll(anyList());
  }

  @Test
  public void persistCampaignProjects_whenProjectIncludedInCampaignAndNoProjectsSelected_verifyInteractions() {

    campaignInformation.setIsPartOfCampaign(true);

    List<Integer> campaignProjectsIds = Collections.emptyList();

    campaignProjectService.persistCampaignProjects(campaignInformation, campaignProjectsIds);

    verify(publishedProjectAccessorService, never()).getPublishedProjectsByIdIn(campaignProjectsIds);
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
      campaignProject.setPublishedProject(new PublishedProject());
      campaignProjects.add(campaignProject);
    });

    when(publishedProjectAccessorService.getPublishedProjectsByIdIn(sanitisedCampaignProjectsIds))
        .thenReturn(List.of(new PublishedProject()));

    campaignProjectService.persistCampaignProjects(campaignInformation, campaignProjectsIdsWithDuplicates);

    verify(publishedProjectAccessorService, times(1)).getPublishedProjectsByIdIn(sanitisedCampaignProjectsIds);
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
      campaignProject.setPublishedProject(new PublishedProject());
      campaignProjects.add(campaignProject);
    });

    when(publishedProjectAccessorService.getPublishedProjectsByIdIn(sanitisedCampaignProjectsIds))
        .thenReturn(List.of(new PublishedProject()));

    campaignProjectService.persistCampaignProjects(campaignInformation, campaignProjectsIdsWithCurrentProjectId);

    verify(publishedProjectAccessorService, times(1)).getPublishedProjectsByIdIn(sanitisedCampaignProjectsIds);
    verify(campaignProjectRepository, times(1)).deleteAllByCampaignInformation(campaignInformation);
    verify(campaignProjectRepository, times(1)).saveAll(campaignProjects);
  }

  @Test
  public void getPublishedProjectViews_whenNoResult_thenEmptyList() {

    when(publishedProjectAccessorService.convertToPublishedProjectViews(anyList()))
        .thenReturn(Collections.emptyList());

    final var resultingPublishedProjectViews = campaignProjectService.getPublishedProjectViews(new CampaignInformationForm());

    assertThat(resultingPublishedProjectViews).isEmpty();
  }

  @Test
  public void getPublishedProjectViews_whenResult_thenPopulatedList() {

    final var publishedProjectView = new PublishedProjectView();
    final var publishedProjectViews = List.of(publishedProjectView);

    when(publishedProjectAccessorService.convertToPublishedProjectViews(anyList()))
        .thenReturn(publishedProjectViews);

    final var resultingPublishedProjectViews = campaignProjectService.getPublishedProjectViews(new CampaignInformationForm());

    assertThat(resultingPublishedProjectViews).isEqualTo(publishedProjectViews);

  }

  @Test
  public void getPublishedProjectViews_verifySortOrder() {

    final var firstAlphabeticallyCampaignProject = createCampaignProjectWithDisplayName("a project name");
    final var lastAlphabeticallyCampaignProject = createCampaignProjectWithDisplayName("z project name");

    final var unsortedCampaignProjectList = List.of(
        lastAlphabeticallyCampaignProject,
        firstAlphabeticallyCampaignProject
    );

    final var unsortedCampaignProjectIds = unsortedCampaignProjectList
        .stream()
        .map(campaignProject -> campaignProject.getPublishedProject().getProjectId())
        .collect(Collectors.toList());

    final var campaignForm = new CampaignInformationForm();
    campaignForm.setProjectsIncludedInCampaign(unsortedCampaignProjectIds);

    when(publishedProjectAccessorService.getPublishedProjectsByIdIn(campaignForm.getProjectsIncludedInCampaign()))
        .thenReturn(unsortedCampaignProjectList.stream().map(CampaignProject::getPublishedProject).collect(Collectors.toList()));

    when(publishedProjectAccessorService.convertToPublishedProjectViews(anyList())).thenCallRealMethod();

    final var resultingPublishedProjectViews = campaignProjectService.getPublishedProjectViews(campaignForm);

    assertThat(resultingPublishedProjectViews).extracting(PublishedProjectView::getDisplayName).containsExactly(
        firstAlphabeticallyCampaignProject.getPublishedProject().getProjectDisplayName(),
        lastAlphabeticallyCampaignProject.getPublishedProject().getProjectDisplayName()
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
  public void getPublishedProjectRestUrl_verifyInteraction() {
    campaignProjectService.getPublishedProjectRestUrl();
    verify(publishedProjectAccessorService, times(1)).getPublishedInfrastructureProjectsRestUrl();
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
    final var publishedProject = new PublishedProject();
    publishedProject.setProjectDisplayName(displayName);

    final var campaignProject = new CampaignProject();
    campaignProject.setPublishedProject(publishedProject);

    return campaignProject;
  }

}