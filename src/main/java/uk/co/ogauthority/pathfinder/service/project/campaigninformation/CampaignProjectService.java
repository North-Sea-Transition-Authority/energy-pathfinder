package uk.co.ogauthority.pathfinder.service.project.campaigninformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.PublishedProject;
import uk.co.ogauthority.pathfinder.model.entity.project.PublishedProjectView;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignInformation;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignProject;
import uk.co.ogauthority.pathfinder.model.form.project.campaigninformation.CampaignInformationForm;
import uk.co.ogauthority.pathfinder.repository.project.campaigninformation.CampaignProjectRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.PublishedProjectAccessorService;

@Service
public class CampaignProjectService {

  private final PublishedProjectAccessorService publishedProjectAccessorService;
  private final CampaignProjectRepository campaignProjectRepository;
  private final EntityDuplicationService entityDuplicationService;

  @Autowired
  public CampaignProjectService(PublishedProjectAccessorService publishedProjectAccessorService,
                                CampaignProjectRepository campaignProjectRepository,
                                EntityDuplicationService entityDuplicationService) {
    this.publishedProjectAccessorService = publishedProjectAccessorService;
    this.campaignProjectRepository = campaignProjectRepository;
    this.entityDuplicationService = entityDuplicationService;
  }

  @Transactional
  public void persistCampaignProjects(CampaignInformation campaignInformation,
                                      List<Integer> campaignProjectIds) {

    final var isProjectIncludedInCampaign = BooleanUtils.isTrue(campaignInformation.isPartOfCampaign());

    final var sanitisePublishedProjectIds = sanitisePublishedProjectIds(campaignProjectIds, campaignInformation);

    final var projects = isProjectIncludedInCampaign
        ? getPublishedProjectsFromIds(sanitisePublishedProjectIds)
        : new ArrayList<PublishedProject>();

    final var campaignProjectsToPersist = new ArrayList<CampaignProject>();

    projects.forEach(project -> {
      final var campaignProject = new CampaignProject();
      campaignProject.setCampaignInformation(campaignInformation);
      campaignProject.setPublishedProject(project);

      campaignProjectsToPersist.add(campaignProject);
    });

    deleteAllCampaignProjects(campaignInformation);

    if (!campaignProjectsToPersist.isEmpty()) {
      campaignProjectRepository.saveAll(campaignProjectsToPersist);
    }
  }

  protected List<PublishedProjectView> getPublishedProjectViews(CampaignInformationForm form) {
    final var publishedProjects = getPublishedProjectsFromIds(form.getProjectsIncludedInCampaign())
        .stream()
        .sorted(Comparator.comparing(publishedProject -> publishedProject.getProjectDisplayName().toLowerCase()))
        .collect(Collectors.toList());

    return publishedProjectAccessorService.convertToPublishedProjectViews(publishedProjects);
  }

  protected List<CampaignProject> getCampaignProjects(ProjectDetail projectDetail) {
    return campaignProjectRepository.findAllByCampaignInformation_ProjectDetail(projectDetail);
  }

  protected String getPublishedProjectRestUrl() {
    return publishedProjectAccessorService.getPublishedInfrastructureProjectsRestUrl();
  }

  @Transactional
  public void deleteAllCampaignProjects(CampaignInformation campaignInformation) {
    campaignProjectRepository.deleteAllByCampaignInformation(campaignInformation);
  }

  @Transactional
  public void deleteAllCampaignProjects(ProjectDetail projectDetail) {
    campaignProjectRepository.deleteAllByCampaignInformation_ProjectDetail(projectDetail);
  }

  @Transactional
  public void copyCampaignProjectsToNewCampaign(CampaignInformation fromCampaignInformation,
                                                CampaignInformation toCampaignInformation) {
    final var existingCampaignProjects = getCampaignProjects(fromCampaignInformation.getProjectDetail());

    entityDuplicationService.duplicateEntitiesAndSetNewParent(
        existingCampaignProjects,
        toCampaignInformation,
        CampaignProject.class
    );
  }

  private List<PublishedProject> getPublishedProjectsFromIds(List<Integer> projectIds) {
    return projectIds.isEmpty()
        ? Collections.emptyList()
        : publishedProjectAccessorService.getPublishedProjectsByIdIn(projectIds);
  }

  private List<Integer> sanitisePublishedProjectIds(List<Integer> publishedProjectIds,
                                                    CampaignInformation campaignInformation) {

    // remove any duplicates in the list
    final var sanitisedPublishedProjectList = new ArrayList<>(publishedProjectIds)
        .stream()
        .distinct()
        .collect(Collectors.toList());

    // remove the project the campaign information is for. You cannot be part of a campaign with your own project.
    // Cast to Integer boxed type so remove call removes value and not item at specific index.
    final int projectId = campaignInformation.getProjectDetail().getProject().getId();
    sanitisedPublishedProjectList.remove(Integer.valueOf(projectId));

    return sanitisedPublishedProjectList;
  }
}
