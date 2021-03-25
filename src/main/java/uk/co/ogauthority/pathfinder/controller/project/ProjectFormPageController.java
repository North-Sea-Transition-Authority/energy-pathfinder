package uk.co.ogauthority.pathfinder.controller.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;

@Controller
public abstract class ProjectFormPageController {

  protected final BreadcrumbService breadcrumbService;
  protected final ControllerHelperService controllerHelperService;

  @Autowired
  public ProjectFormPageController(BreadcrumbService breadcrumbService,
                                   ControllerHelperService controllerHelperService) {
    this.breadcrumbService = breadcrumbService;
    this.controllerHelperService = controllerHelperService;
  }
}
