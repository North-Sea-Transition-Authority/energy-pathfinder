package uk.co.ogauthority.pathfinder.service.project.overview;

import java.util.Set;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;

@Service
public class OverviewFormSectionService implements ProjectFormSectionService {

  @Override
  public boolean isComplete(ProjectDetail detail) {
    return true;
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail, Set<UserToProjectRelationship> userToProjectRelationships) {
    return userToProjectRelationships.contains(UserToProjectRelationship.CONTRIBUTOR);
  }

  @Override
  public boolean isTaskValidForProjectDetail(ProjectDetail detail) {
    return true;
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {
    //Do nothing because this is just a summary of data from other sections
  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return Set.of(ProjectType.INFRASTRUCTURE, ProjectType.FORWARD_WORK_PLAN);
  }
}
