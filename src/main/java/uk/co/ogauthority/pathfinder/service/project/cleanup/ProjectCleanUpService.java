package uk.co.ogauthority.pathfinder.service.project.cleanup;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;

@Service
public class ProjectCleanUpService {

  private final List<ProjectFormSectionService> projectFormSectionServices;

  @Autowired
  public ProjectCleanUpService(List<ProjectFormSectionService> projectFormSectionServices) {
    this.projectFormSectionServices = projectFormSectionServices;
  }

  /**
   * Wrapper method which will call the removeSectionData method for any ProjectFormSectionService
   * which is not shown in the task list.
   * @param projectDetail the project detail we are removing section data from
   */
  public void removeProjectSectionDataIfNotRelevant(ProjectDetail projectDetail) {
    var sectionsAllowingCleanup = projectFormSectionServices
        .stream()
        .filter(projectFormSectionService ->
            // only remove section data for sections which allow clean up
            // and are not shown in the task list
            projectFormSectionService.allowSectionDataCleanUp(projectDetail)
        )
        .collect(Collectors.toSet());

    sectionsAllowingCleanup
        .stream()
        .filter(projectFormSectionService -> !projectFormSectionService.isTaskValidForProjectDetail(projectDetail))
        .forEach(projectFormSectionService -> projectFormSectionService.removeSectionData(projectDetail));

    sectionsAllowingCleanup
        .stream()
        .filter(projectFormSectionService -> projectFormSectionService.isTaskValidForProjectDetail(projectDetail))
        .forEach(projectFormSectionService -> projectFormSectionService.removeSectionDataIfNotRelevant(projectDetail));
  }
}
