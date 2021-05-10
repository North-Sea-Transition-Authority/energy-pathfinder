package uk.co.ogauthority.pathfinder.service.project;

import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;

public class ProjectTypeModelUtil {

  public static final String PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR = "projectTypeDisplayName";
  public static final String PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR = "projectTypeDisplayNameLowercase";

  private ProjectTypeModelUtil() {
    throw new IllegalStateException("ProjectTypeModelUtil is a utility class and should not be instantiated");
  }

  /**
   * Utility method to add the sentence and lower case display names for the project type associated with projectDetail
   * to the provide modelAndView.
   * @param modelAndView The model and view to add the attributes to
   * @param projectDetail The project detail to determine the project type for
   */
  public static void addProjectTypeDisplayNameAttributesToModel(
      ModelAndView modelAndView,
      ProjectDetail projectDetail
  ) {
    modelAndView.addObject(
        PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR,
        ProjectService.getProjectTypeDisplayName(projectDetail)
    );
    modelAndView.addObject(
        PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR,
        ProjectService.getProjectTypeDisplayNameLowercase(projectDetail)
    );
  }

}
