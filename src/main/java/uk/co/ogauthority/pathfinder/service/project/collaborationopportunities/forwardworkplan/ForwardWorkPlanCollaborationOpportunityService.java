package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;

@Service
public class ForwardWorkPlanCollaborationOpportunityService implements ProjectFormSectionService {

  @Override
  public boolean isComplete(ProjectDetail detail) {
    return false;
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail) {
    return ProjectService.isForwardWorkPlanProject(detail);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {

  }
}
