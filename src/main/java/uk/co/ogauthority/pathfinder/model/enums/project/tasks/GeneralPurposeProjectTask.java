package uk.co.ogauthority.pathfinder.model.enums.project.tasks;


import uk.co.ogauthority.pathfinder.controller.project.ProjectFormPageController;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;

public interface GeneralPurposeProjectTask {
  /**
   * Returns the class of a service which should control project specific task behaviour.
   */
  Class<? extends ProjectFormSectionService> getServiceClass();

  /**
   * Returns the class of a controller that defines general purpose task restrictions.
   */
  Class<?extends ProjectFormPageController> getControllerClass();

  int getDisplayOrder();

  String getDisplayName();

  String getTaskLandingPageRoute(Project project);

}
