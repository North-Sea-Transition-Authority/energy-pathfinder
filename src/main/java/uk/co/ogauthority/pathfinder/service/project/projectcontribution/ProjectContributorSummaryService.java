package uk.co.ogauthority.pathfinder.service.project.projectcontribution;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.projectcontributor.ProjectContributorsView;
import uk.co.ogauthority.pathfinder.repository.project.projectcontributor.ProjectContributorRepository;

@Service
public class ProjectContributorSummaryService {

  private final ProjectContributorRepository projectContributorRepository;

  @Autowired
  public ProjectContributorSummaryService(
      ProjectContributorRepository projectContributorRepository) {
    this.projectContributorRepository = projectContributorRepository;
  }

  public ProjectContributorsView getProjectContributorsView(ProjectDetail detail) {
    return getProjectContributorsView(detail.getProject(), detail.getVersion());
  }

  public ProjectContributorsView getProjectContributorsView(Project project, int version) {
    var organisationGroupNames = projectContributorRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
            project, version)
        .stream()
        .map(projectContributor -> projectContributor.getContributionOrganisationGroup().getName())
        .sorted()
        .collect(Collectors.toList());

    return new ProjectContributorsView(organisationGroupNames);
  }
}
