package uk.co.ogauthority.pathfinder.service.projectupdate;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.controller.projectupdate.OperatorUpdateController;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.ProvideNoUpdateForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class OperatorProjectUpdateService {

  public static final String START_PAGE_TEMPLATE_PATH = "projectupdate/startPage";
  public static final String PROVIDE_NO_UPDATE_TEMPLATE_PATH = "projectupdate/confirmNoUpdate";

  private final ValidationService validationService;
  private final BreadcrumbService breadcrumbService;

  @Autowired
  public OperatorProjectUpdateService(ValidationService validationService,
                                      BreadcrumbService breadcrumbService) {
    this.validationService = validationService;
    this.breadcrumbService = breadcrumbService;
  }

  public BindingResult validate(ProvideNoUpdateForm form, BindingResult bindingResult) {
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  public ModelAndView getProjectUpdateModelAndView(Integer projectId) {
    return new ModelAndView(START_PAGE_TEMPLATE_PATH)
        .addObject("startActionUrl", ReverseRouter.route(on(OperatorUpdateController.class).startUpdate(projectId, null, null)));
  }

  public ModelAndView getProjectProvideNoUpdateModelAndView(Integer projectId, ProvideNoUpdateForm form) {
    var modelAndView = new ModelAndView(PROVIDE_NO_UPDATE_TEMPLATE_PATH)
        .addObject("form", form)
        .addObject("confirmActionUrl", ReverseRouter.route(on(OperatorUpdateController.class)
            .provideNoUpdate(projectId, null, null, null, null)))
        .addObject("cancelUrl", ReverseRouter.route(on(ManageProjectController.class).getProject(projectId, null, null, null)));
    breadcrumbService.fromManageProject(projectId, modelAndView, OperatorUpdateController.NO_UPDATE_REQUIRED_PAGE_NAME);
    return modelAndView;
  }
}
