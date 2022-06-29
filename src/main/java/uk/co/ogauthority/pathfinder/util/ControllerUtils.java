package uk.co.ogauthority.pathfinder.util;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsController;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.accessibility.AccessibilityStatementController;
import uk.co.ogauthority.pathfinder.controller.analytics.CookiesController;
import uk.co.ogauthority.pathfinder.controller.contact.ContactInformationController;
import uk.co.ogauthority.pathfinder.controller.feedback.FeedbackController;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.setup.ProjectSetupController;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
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

  public static String getProjectManagementUrl(Integer projectId) {
    return ReverseRouter.route(on(ManageProjectController.class).getProject(projectId, null, null, null));
  }

  public static String getWorkAreaUrl() {
    return ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null, null));
  }

  public static String getContactUrl() {
    return getContactUrl(false);
  }

  public static String getContactUrl(boolean opensInNewTab) {
    return ReverseRouter.route(on(ContactInformationController.class).getContactInformation(opensInNewTab));
  }

  public static String getAccessibilityStatementUrl() {
    return ReverseRouter.route(on(AccessibilityStatementController.class).getAccessibilityStatement());
  }

  public static String getFeedbackUrl(Integer projectDetailId) {
    return ReverseRouter.route(on(FeedbackController.class).getFeedback(Optional.of(projectDetailId), null));
  }

  public static String getFeedbackUrl() {
    return ReverseRouter.route(on(FeedbackController.class).getFeedback(Optional.empty(), null));
  }

  public static String getCookiesUrl() {
    return ReverseRouter.route(on(CookiesController.class).getCookiePreferences());
  }

  public static String getAnalyticsMeasurementUrl() {
    return ReverseRouter.route(on(AnalyticsController.class)
        .collectAnalyticsEvent(null, Optional.empty()));
  }

}