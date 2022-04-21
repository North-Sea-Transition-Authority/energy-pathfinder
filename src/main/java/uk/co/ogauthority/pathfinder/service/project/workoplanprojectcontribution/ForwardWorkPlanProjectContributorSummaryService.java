package uk.co.ogauthority.pathfinder.service.project.workoplanprojectcontribution;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.workplanprojectcontributor.ForwardWorkPlanProjectContributorsView;
import uk.co.ogauthority.pathfinder.repository.project.projectcontributor.ProjectContributorRepository;
import uk.co.ogauthority.pathfinder.repository.project.workplanprojectcontributor.ForwardWorkPlanContributorDetailsRepository;

@Service
public class ForwardWorkPlanProjectContributorSummaryService {

  private final ForwardWorkPlanContributorDetailsRepository forwardWorkPlanContributorDetailsRepository;
  private final ProjectContributorRepository projectContributorRepository;

  @Autowired
  public ForwardWorkPlanProjectContributorSummaryService(
      ForwardWorkPlanContributorDetailsRepository forwardWorkPlanContributorDetailsRepository,
      ProjectContributorRepository projectContributorRepository) {
    this.forwardWorkPlanContributorDetailsRepository = forwardWorkPlanContributorDetailsRepository;
    this.projectContributorRepository = projectContributorRepository;
  }

  public ForwardWorkPlanProjectContributorsView getProjectContributorsView(ProjectDetail detail) {
    return getProjectContributorsView(detail.getProject(), detail.getVersion());
  }

  public ForwardWorkPlanProjectContributorsView getProjectContributorsView(Project project, int version) {
    var forwardWorkPlanProjectContributor =
        forwardWorkPlanContributorDetailsRepository.findByProjectDetail_ProjectAndProjectDetail_Version(
            project,
            version
        );

    return forwardWorkPlanProjectContributor
        .map(forwardWorkPlanContributorDetails -> new ForwardWorkPlanProjectContributorsView(
            getContributingOrganisationGroupNames(project, version),
            forwardWorkPlanContributorDetails.getHasProjectContributors()
        ))
        .orElse(new ForwardWorkPlanProjectContributorsView(List.of(), null));
  }

  private List<String> getContributingOrganisationGroupNames(Project project, int version) {
    return projectContributorRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
            project,
            version
        )
        .stream()
        .map(projectContributor -> projectContributor.getContributionOrganisationGroup().getName())
        .collect(Collectors.toList());
  }
}
