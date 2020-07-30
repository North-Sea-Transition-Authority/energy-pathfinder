package uk.co.ogauthority.pathfinder.service.navigation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

@Service
public class BreadcrumbService {

  public void fromWorkArea(ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, workArea(), thisPage);
  }

  private Map<String, String> workArea() {
    Map<String, String> breadcrumbs = new LinkedHashMap<>();
    breadcrumbs.put(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), "Work area");
    return breadcrumbs;
  }

  public void fromTaskList(Integer projectId, ModelAndView modelAndView, String thisPage) {
    addAttrs(modelAndView, taskList(projectId), thisPage);
  }

  private Map<String, String> taskList(Integer projectId) {
    var map = workArea();
    String route = ReverseRouter.route(on(TaskListController.class).viewTaskList(projectId));
    map.put(route, "Task list");
    return map;
  }

  private void addAttrs(ModelAndView modelAndView, Map<String, String> breadcrumbs, String currentPage) {
    modelAndView.addObject("crumbList", breadcrumbs);
    modelAndView.addObject("currentPage", currentPage);
  }
}
