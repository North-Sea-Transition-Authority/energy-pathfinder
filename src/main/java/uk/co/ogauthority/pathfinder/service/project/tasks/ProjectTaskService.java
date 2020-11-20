package uk.co.ogauthority.pathfinder.service.project.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.GeneralPurposeProjectTask;

@Service
public class ProjectTaskService {

  private final ApplicationContext applicationContext;

  @Autowired
  public ProjectTaskService(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  /**
   * Helper which asks Spring to provide the project task service if its available.
   */
  private ProjectFormSectionService getTaskService(GeneralPurposeProjectTask projectTask) {
    if (projectTask.getServiceClass() == null) {
      throw new IllegalStateException(String.format("Project task doesn't have service class specified: %s",
          projectTask.toString()));
    }

    return applicationContext.getBean(projectTask.getServiceClass());
  }

  /**
   * A task can be shown for a project detail if the service specific checks are met.
   */
  public boolean canShowTask(GeneralPurposeProjectTask projectTask, ProjectDetail detail) {
    return getTaskService(projectTask).canShowInTaskList(detail);
  }

  /**
   * Return true when all questions answered under a task are valid for a project detail.
   */
  public boolean isTaskComplete(GeneralPurposeProjectTask projectTask, ProjectDetail detail) {
    return getTaskService(projectTask).isComplete(detail);
  }


}
