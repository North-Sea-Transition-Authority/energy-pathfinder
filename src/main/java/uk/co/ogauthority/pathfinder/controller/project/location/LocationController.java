package uk.co.ogauthority.pathfinder.controller.project.location;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.rest.DevUkRestController;
import uk.co.ogauthority.pathfinder.model.form.project.location.LocationForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;

@Controller
@RequestMapping("/project/{projectId}/location")
public class LocationController {

  private final ProjectService projectService;

  @Autowired
  public LocationController(ProjectService projectService) {
    this.projectService = projectService;
  }

  @GetMapping()
  public ModelAndView getLocationDetails(AuthenticatedUserAccount user,
                                         @PathVariable("projectId") Integer projectId) {
    //TODO PAT-133 Fetch with context of project and user
    var details = projectService.getLatestDetail(projectId)
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("Unable to find project detail for project id  %d", projectId)));

    return new ModelAndView("project/location/location") //TODO Breadcrumbs
        .addObject("fieldsRestUrl", ReverseRouter.route(on(DevUkRestController.class).searchFields(null)))
        .addObject("form", new LocationForm());
  }
}
