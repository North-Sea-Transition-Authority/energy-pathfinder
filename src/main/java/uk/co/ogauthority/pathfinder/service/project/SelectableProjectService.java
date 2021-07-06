package uk.co.ogauthority.pathfinder.service.project;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.SelectableProject;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.repository.project.SelectableProjectRepository;

@Service
public class SelectableProjectService {

  private final SelectableProjectRepository selectableProjectRepository;

  @Autowired
  public SelectableProjectService(SelectableProjectRepository selectableProjectRepository) {
    this.selectableProjectRepository = selectableProjectRepository;
  }

  public List<SelectableProject> getSelectableProjectsByIdIn(List<Integer> selectableProjectIds) {
    return selectableProjectRepository.findAllByProjectIdIn(selectableProjectIds);
  }

  public List<SelectableProject> getPublishedSelectableProjects(String searchTerm, ProjectType projectType) {
    return selectableProjectRepository
        .findAllPublishedProjectsByProjectDisplayNameOrOperatorGroupNameContainingIgnoreCase(
            searchTerm,
            projectType
        );
  }
}
