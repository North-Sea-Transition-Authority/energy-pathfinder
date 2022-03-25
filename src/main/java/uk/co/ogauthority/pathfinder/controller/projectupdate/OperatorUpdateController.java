package uk.co.ogauthority.pathfinder.controller.projectupdate;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsEventCategory;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsService;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsUtils;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.ProvideNoUpdateForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.projectupdate.OperatorProjectUpdateContext;
import uk.co.ogauthority.pathfinder.service.projectupdate.OperatorProjectUpdateService;

@Controller
@ProjectStatusCheck(status = {ProjectStatus.QA, ProjectStatus.PUBLISHED})
@ProjectFormPagePermissionCheck(permissions = ProjectPermission.PROVIDE_UPDATE)
@ProjectTypeCheck(types = { ProjectType.INFRASTRUCTURE, ProjectType.FORWARD_WORK_PLAN })
@RequestMapping("/project/{projectId}")
public class OperatorUpdateController {

  public static final String NO_UPDATE_REQUIRED_PAGE_NAME = "No changes required";

  private final OperatorProjectUpdateService operatorProjectUpdateService;
  private final ControllerHelperService controllerHelperService;
  private final AnalyticsService analyticsService;

  @Autowired
  public OperatorUpdateController(OperatorProjectUpdateService operatorProjectUpdateService,
                                  ControllerHelperService controllerHelperService,
                                  AnalyticsService analyticsService) {
    this.operatorProjectUpdateService = operatorProjectUpdateService;
    this.controllerHelperService = controllerHelperService;
    this.analyticsService = analyticsService;
  }

  @GetMapping("/start-update")
  public ModelAndView startPage(@PathVariable("projectId") Integer projectId,
                                OperatorProjectUpdateContext operatorProjectUpdateContext) {
    return operatorProjectUpdateService.getProjectUpdateModelAndView(operatorProjectUpdateContext.getProjectDetails());
  }

  @PostMapping("/start-update")
  public ModelAndView startUpdate(@PathVariable("projectId") Integer projectId,
                                  OperatorProjectUpdateContext operatorProjectUpdateContext,
                                  AuthenticatedUserAccount user) {
    operatorProjectUpdateService.startUpdate(operatorProjectUpdateContext.getProjectDetails(), user);
    return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
  }

  @GetMapping("/no-update-required")
  public ModelAndView provideNoUpdate(@PathVariable("projectId") Integer projectId,
                                      OperatorProjectUpdateContext operatorProjectUpdateContext,
                                      AuthenticatedUserAccount user) {
    return operatorProjectUpdateService.getProjectProvideNoUpdateModelAndView(
        operatorProjectUpdateContext.getProjectDetails(),
        user,
        new ProvideNoUpdateForm()
    );
  }

  @PostMapping("/no-update-required")
  public ModelAndView saveNoUpdate(@PathVariable("projectId") Integer projectId,
                                   @Valid @ModelAttribute("form") ProvideNoUpdateForm form,
                                   BindingResult bindingResult,
                                   OperatorProjectUpdateContext operatorProjectUpdateContext,
                                   AuthenticatedUserAccount user,
                                   @CookieValue(name = AnalyticsUtils.GA_CLIENT_ID_COOKIE_NAME, required = false) Optional<String> analyticsClientId) {
    bindingResult = operatorProjectUpdateService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        operatorProjectUpdateService.getProjectProvideNoUpdateModelAndView(operatorProjectUpdateContext.getProjectDetails(), user, form),
        form,
        () -> {

          operatorProjectUpdateService.createNoUpdateNotification(
              operatorProjectUpdateContext.getProjectDetails(),
              user,
              form
          );

          analyticsService.sendAnalyticsEvent(analyticsClientId, AnalyticsEventCategory.NO_CHANGE_UPDATE_SUBMITTED,
              Map.of("project_type", operatorProjectUpdateContext.getProjectDetails().getProjectType().name()));

          return ReverseRouter.redirect(on(OperatorUpdateController.class).provideNoUpdateConfirmation(projectId, null));

        }
    );
  }

  @GetMapping("/no-update-required/confirmation")
  public ModelAndView provideNoUpdateConfirmation(@PathVariable("projectId") Integer projectId,
                                                  OperatorProjectUpdateContext operatorProjectUpdateContext) {
    operatorProjectUpdateService.confirmNoUpdateExistsForProjectDetail(operatorProjectUpdateContext.getProjectDetails());
    return operatorProjectUpdateService.getProjectProvideNoUpdateConfirmationModelAndView(operatorProjectUpdateContext.getProjectDetails());
  }
}
