package uk.co.ogauthority.pathfinder.service.project;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.rest.PublishedProjectRestController;
import uk.co.ogauthority.pathfinder.model.entity.project.PublishedProject;
import uk.co.ogauthority.pathfinder.model.entity.project.PublishedProjectView;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.repository.project.PublishedProjectRepository;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Service
public class PublishedProjectAccessorService {

  private final SearchSelectorService searchSelectorService;
  private final PublishedProjectRepository publishedProjectRepository;

  @Autowired
  public PublishedProjectAccessorService(SearchSelectorService searchSelectorService,
                                         PublishedProjectRepository publishedProjectRepository) {
    this.searchSelectorService = searchSelectorService;
    this.publishedProjectRepository = publishedProjectRepository;
  }

  public List<RestSearchItem> searchProjectsWithDisplayNameContaining(String searchTerm, ProjectType projectType) {
    return searchSelectorService.search(
        searchTerm,
        findByProjectDisplayNameAndProjectType(searchTerm, projectType)
    );
  }

  public String getPublishedInfrastructureProjectsRestUrl() {
    return SearchSelectorService.route(on(PublishedProjectRestController.class)
        .searchPublishedInfrastructureProjects(null));
  }

  public List<PublishedProject> getPublishedProjectsByIdIn(List<Integer> publishedProjectIds) {
    return publishedProjectRepository.findAllByProjectIdIn(publishedProjectIds);
  }

  public List<PublishedProjectView> convertToPublishedProjectViews(List<PublishedProject> publishedProjects) {
    return publishedProjects
        .stream()
        .map(this::getPublishedProjectView)
        .collect(Collectors.toList());
  }

  private PublishedProjectView getPublishedProjectView(PublishedProject publishedProject) {
    final var publishedProjectView = new PublishedProjectView();
    publishedProjectView.setProjectId(publishedProject.getProjectId());
    publishedProjectView.setDisplayName(publishedProject.getProjectDisplayName());
    //TODO PAT-584, actually validate if they are still valid
    publishedProjectView.setValid(true);
    return publishedProjectView;
  }

  private List<PublishedProject> findByProjectDisplayNameAndProjectType(String projectDisplayName,
                                                                        ProjectType projectType) {
    return publishedProjectRepository.findAllByProjectDisplayNameContainingIgnoreCaseAndProjectTypeOrderByProjectDisplayName(
        projectDisplayName,
        projectType
    );
  }
}
