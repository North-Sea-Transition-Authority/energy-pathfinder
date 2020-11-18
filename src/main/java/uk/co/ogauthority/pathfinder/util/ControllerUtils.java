package uk.co.ogauthority.pathfinder.util;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.setup.ProjectSetupController;
import uk.co.ogauthority.pathfinder.model.Checkable;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

/**
 * Utility class to provide useful methods for controllers.
 */
public class ControllerUtils {

  private ControllerUtils() {
    throw new AssertionError();
  }

  public static Map<String, String> asCheckboxMap(List<? extends Checkable> items) {
    return items.stream()
        .sorted(Comparator.comparing(Checkable::getDisplayOrder))
        .collect(Collectors.toMap(Checkable::getIdentifier, Checkable::getDisplayName, (x,y) -> y, LinkedHashMap::new));
  }

  public static String getBackToTaskListUrl(Integer projectId) {
    return ReverseRouter.route(on(TaskListController.class).viewTaskList(projectId, null));
  }

  public static String getProjectSetupUrl(Integer projectId) {
    return ReverseRouter.route(on(ProjectSetupController.class).getProjectSetup(projectId, null));
  }
}
