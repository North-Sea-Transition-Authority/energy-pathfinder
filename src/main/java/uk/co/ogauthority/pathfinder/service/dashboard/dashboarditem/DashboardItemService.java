package uk.co.ogauthority.pathfinder.service.dashboard.dashboarditem;

import java.util.Map;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.dashboard.DashboardProjectItemView;

/**
 * Interface to signify a service is responsible for providing
 * dashboard item view classes for a project type.
 */
public interface DashboardItemService {

  /**
   * Get the project type that this implementation supports.
   * @return the project type this implementation supports
   */
  ProjectType getSupportedProjectType();

  /**
   * The dashboard item view that represents this project type.
   * @param dashboardProjectItem the dashboard project item to base the view object from
   * @return the dashboard view object that represents projects of the supported type
   */
  DashboardProjectItemView getDashboardProjectItemView(DashboardProjectItem dashboardProjectItem);

  /**
   * Get the template path for the template that determines how this implementations dashboard item
   * should be displayed.
   * @return the template path to the template deciding how this dashboard item should be displayed
   */
  String getTemplatePath();

  /**
   * Get the model required by the template associated with this implementation to render.
   * @param dashboardProjectItem The dashboard project item we are processing
   * @return the model allowing the template associated with this dashboard item implementation to render
   */
  Map<String, Object> getTemplateModel(DashboardProjectItem dashboardProjectItem);
}
