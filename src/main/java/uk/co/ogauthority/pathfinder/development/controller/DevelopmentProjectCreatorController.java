package uk.co.ogauthority.pathfinder.development.controller;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.rest.OrganisationGroupRestController;
import uk.co.ogauthority.pathfinder.development.DevelopmentProjectCreatorForm;
import uk.co.ogauthority.pathfinder.development.service.DevelopmentProjectCreatorSchedulerService;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Controller
@RequestMapping("/dev")
@Profile("development")
public class DevelopmentProjectCreatorController {

  private final PortalOrganisationAccessor portalOrganisationAccessor;
  private final DevelopmentProjectCreatorSchedulerService projectCreatorSchedulerService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public DevelopmentProjectCreatorController(PortalOrganisationAccessor portalOrganisationAccessor,
                                             DevelopmentProjectCreatorSchedulerService projectCreatorSchedulerService,
                                             ControllerHelperService controllerHelperService) {
    this.portalOrganisationAccessor = portalOrganisationAccessor;
    this.projectCreatorSchedulerService = projectCreatorSchedulerService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping("/create-projects")
  public ModelAndView createProjects(AuthenticatedUserAccount user) {
    return getCreateProjectsModelAndView(new DevelopmentProjectCreatorForm());
  }

  @PostMapping("/create-projects")
  public ModelAndView createProjects(AuthenticatedUserAccount user,
                                     @Valid @ModelAttribute("form") DevelopmentProjectCreatorForm form,
                                     BindingResult bindingResult
  ) {
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getCreateProjectsModelAndView(form),
        form,
        () -> {
          projectCreatorSchedulerService.scheduleProjectCreation(
              form,
              user,
              portalOrganisationAccessor.getOrganisationGroupOrError(Integer.valueOf(form.getOrganisationGroup()))
          );

          return new ModelAndView("development/developmentCreateProjectsConfirmation")
              .addObject("workAreaUrl",
                  ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null, null))
              )
              .addObject("createProjectsUrl",
                  ReverseRouter.route(on(DevelopmentProjectCreatorController.class).createProjects(null))
              );
        }
    );
  }

  private ModelAndView getCreateProjectsModelAndView(DevelopmentProjectCreatorForm form) {
    return new ModelAndView("development/developmentCreateProjects")
        .addObject("form", form)
        .addObject("operatorsRestUrl",
            SearchSelectorService.route(on(OrganisationGroupRestController.class).searchOrganisations(null))
        )
        .addObject("statuses", ProjectStatus.getAllAsMap())
        .addObject("cancelUrl",
            ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null, null))
        );
  }

}
