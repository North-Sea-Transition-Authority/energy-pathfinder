package uk.co.ogauthority.pathfinder.service.project;

import java.util.Set;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;

@Service
public class TestProjectFormSectionService implements ProjectFormSectionService {

  @Override
  public boolean isComplete(ProjectDetail detail) {
    return false;
  }

  @Override
  public boolean isTaskValidForProjectDetail(ProjectDetail detail) {
    return false;
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {

  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return null;
  }
}