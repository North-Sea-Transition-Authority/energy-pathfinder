package uk.co.ogauthority.pathfinder.controller.project.upcomingtender;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.rest.TenderFunctionRestController;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.view.UpcomingTenderView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderSummaryService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/tenders")
public class UpcomingTendersController {

  public static final String PAGE_NAME = "Upcoming tenders";
  public static final String PAGE_NAME_SINGULAR = "Upcoming tender";
  public static final String REMOVE_PAGE_NAME = "Remove upcoming tender";

  private final BreadcrumbService breadcrumbService;
  private final ControllerHelperService controllerHelperService;
  private final UpcomingTenderService upcomingTenderService;
  private final UpcomingTenderSummaryService upcomingTenderSummaryService;


  @Autowired
  public UpcomingTendersController(BreadcrumbService breadcrumbService,
                                   ControllerHelperService controllerHelperService,
                                   UpcomingTenderService upcomingTenderService,
                                   UpcomingTenderSummaryService upcomingTenderSummaryService) {
    this.breadcrumbService = breadcrumbService;
    this.controllerHelperService = controllerHelperService;
    this.upcomingTenderService = upcomingTenderService;
    this.upcomingTenderSummaryService = upcomingTenderSummaryService;
  }

  @GetMapping
  public ModelAndView viewTenders(@PathVariable("projectId") Integer projectId,
                                  ProjectContext projectContext) {
    return getViewTendersModelAndView(
        projectId,
        projectContext,
        upcomingTenderSummaryService.getSummaryViews(projectContext.getProjectDetails()),
        ValidationResult.NOT_VALIDATED
    );
  }

  @PostMapping
  public ModelAndView saveTenders(@PathVariable("projectId") Integer projectId,
                                  ProjectContext projectContext) {
    var tenderViews = upcomingTenderSummaryService.getValidatedSummaryViews(
        projectContext.getProjectDetails()
    );
    //TODO This type of check could be turned into a method in the ControllerHelperService if we keep using it for
    // Summaries / validation that doesn't rely on a binding result
    var validationResult = tenderViews.stream().anyMatch(utv -> !utv.isValid())
        ? ValidationResult.INVALID
        : ValidationResult.VALID;

    if (validationResult.equals(ValidationResult.INVALID)) {
      return getViewTendersModelAndView(
          projectId,
          projectContext,
          tenderViews,
          validationResult
      );
    }

    return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
  }

  @GetMapping("/upcoming-tender")
  public ModelAndView addUpcomingTender(@PathVariable("projectId") Integer projectId,
                                        ProjectContext projectContext) {
    return getUpcomingTenderModelAndView(projectId, new UpcomingTenderForm());
  }


  @PostMapping("/upcoming-tender")
  public ModelAndView saveUpcomingTender(@PathVariable("projectId") Integer projectId,
                                        @Valid @ModelAttribute("form") UpcomingTenderForm form,
                                        BindingResult bindingResult,
                                        ValidationType validationType,
                                        ProjectContext projectContext) {
    bindingResult = upcomingTenderService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getUpcomingTenderModelAndView(projectId, form),
        form,
        () -> {
          upcomingTenderService.createUpcomingTender(projectContext.getProjectDetails(), form);
          return ReverseRouter.redirect(on(UpcomingTendersController.class).viewTenders(projectId, null));
        }
    );
  }

  @GetMapping("/upcoming-tender/{upcomingTenderId}/edit")
  public ModelAndView editUpcomingTender(@PathVariable("projectId") Integer projectId,
                                         @PathVariable("upcomingTenderId") Integer upcomingTenderId,
                                         ProjectContext projectContext) {
    var upcomingTender = upcomingTenderService.getOrError(upcomingTenderId);
    return getUpcomingTenderModelAndView(projectId, upcomingTenderService.getForm(upcomingTender));
  }

  @PostMapping("/upcoming-tender/{upcomingTenderId}/edit")
  public ModelAndView updateUpcomingTender(@PathVariable("projectId") Integer projectId,
                                           @PathVariable("upcomingTenderId") Integer upcomingTenderId,
                                           @Valid @ModelAttribute("form") UpcomingTenderForm form,
                                           BindingResult bindingResult,
                                           ValidationType validationType,
                                           ProjectContext projectContext) {
    var upcomingTender = upcomingTenderService.getOrError(upcomingTenderId);
    bindingResult = upcomingTenderService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getUpcomingTenderModelAndView(projectId, form),
        form,
        () -> {
          upcomingTenderService.updateUpcomingTender(upcomingTender, form);
          return ReverseRouter.redirect(on(UpcomingTendersController.class).viewTenders(projectId, null));
        }
    );
  }

  @GetMapping("/upcoming-tender/{upcomingTenderId}/delete/{displayOrder}")
  public ModelAndView deleteUpcomingTenderConfirm(@PathVariable("projectId") Integer projectId,
                                                  @PathVariable("upcomingTenderId") Integer upcomingTenderId,
                                                  @PathVariable("displayOrder") Integer displayOrder,
                                                  ProjectContext projectContext) {
    var upcomingTender = upcomingTenderService.getOrError(upcomingTenderId);

    var modelAndView = new ModelAndView("project/upcomingtender/removeUpcomingTender")
          .addObject("view", upcomingTenderSummaryService.getUpcomingTenderView(upcomingTender, displayOrder))
          .addObject("cancelUrl", ReverseRouter.route(on(UpcomingTendersController.class).viewTenders(projectId, null)));
    breadcrumbService.fromUpcomingTenders(projectId, modelAndView, REMOVE_PAGE_NAME);
    return modelAndView;
  }

  @PostMapping("/upcoming-tender/{upcomingTenderId}/delete/{displayOrder}")
  public ModelAndView deleteUpcomingTender(@PathVariable("projectId") Integer projectId,
                                           @PathVariable("upcomingTenderId") Integer upcomingTenderId,
                                           @PathVariable("displayOrder") Integer displayOrder,
                                           ProjectContext projectContext) {
    var upcomingTender = upcomingTenderService.getOrError(upcomingTenderId);
    upcomingTenderService.delete(upcomingTender);
    return ReverseRouter.redirect(on(UpcomingTendersController.class).viewTenders(projectId, null));
  }

  private ModelAndView getViewTendersModelAndView(
      Integer projectId,
      ProjectContext projectContext,
      List<UpcomingTenderView> tenderViews,
      ValidationResult validationResult
  ) {
    var modelAndView = new ModelAndView("project/upcomingtender/tenderSummary")
        .addObject("addTenderUrl", ReverseRouter.route(on(UpcomingTendersController.class).addUpcomingTender(projectId, null)))
        .addObject("tenderViews", tenderViews)
        .addObject("isValid", validationResult.equals(ValidationResult.VALID))
        .addObject("errorSummary",
            validationResult.equals(ValidationResult.INVALID)
              ? upcomingTenderSummaryService.getErrors(tenderViews)
              : null
        )
        .addObject("backToTaskListUrl", ReverseRouter.route(on(TaskListController.class).viewTaskList(projectId, null)));
    breadcrumbService.fromTaskList(projectId, modelAndView, PAGE_NAME);
    return modelAndView;
  }

  private ModelAndView getUpcomingTenderModelAndView(Integer projectId, UpcomingTenderForm form) {
    var modelAndView = new ModelAndView("project/upcomingtender/upcomingTender")
        .addObject("tenderRestUrl", SearchSelectorService.route(on(TenderFunctionRestController.class).searchTenderFunctions(null)))
        .addObject("form", form)
        .addObject("preSelectedFunction", upcomingTenderService.getPreSelectedFunction(form))
        .addObject("contractBands", ContractBand.getAllAsMap());
    breadcrumbService.fromUpcomingTenders(projectId, modelAndView, PAGE_NAME_SINGULAR);
    return modelAndView;
  }
}
