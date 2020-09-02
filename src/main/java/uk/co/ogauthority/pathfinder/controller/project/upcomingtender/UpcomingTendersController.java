package uk.co.ogauthority.pathfinder.controller.project.upcomingtender;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/tenders")
public class UpcomingTendersController {

  public static final String PAGE_NAME = "Upcoming tenders";
  public static final String PAGE_NAME_SINGULAR = "Upcoming tender";

  private final BreadcrumbService breadcrumbService;
  private final ControllerHelperService controllerHelperService;
  private final UpcomingTenderService upcomingTenderService;


  @Autowired
  public UpcomingTendersController(BreadcrumbService breadcrumbService,
                                   ControllerHelperService controllerHelperService,
                                   UpcomingTenderService upcomingTenderService) {
    this.breadcrumbService = breadcrumbService;
    this.controllerHelperService = controllerHelperService;
    this.upcomingTenderService = upcomingTenderService;
  }

  @GetMapping
  public ModelAndView viewTenders(@PathVariable("projectId") Integer projectId,
                                  ProjectContext projectContext) {
    return getViewTendersModelAndView(projectId, projectContext);
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
          return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
        }
    );
  }

  private ModelAndView getViewTendersModelAndView(Integer projectId, ProjectContext projectContext) {
    var modelAndView = new ModelAndView("project/upcomingtender/tenderSummary")
        .addObject("addTenderUrl", ReverseRouter.route(on(UpcomingTendersController.class).addUpcomingTender(projectId, null)));
    breadcrumbService.fromTaskList(projectId, modelAndView, PAGE_NAME);
    return modelAndView;
  }

  private ModelAndView getUpcomingTenderModelAndView(Integer projectId, UpcomingTenderForm form) {
    var modelAndView = new ModelAndView("project/upcomingtender/upcomingTender")
        .addObject("tenderRestUrl", SearchSelectorService.route(on(TenderFunctionRestController.class).searchTenderFunctions(null)))
        .addObject("form", form)
        .addObject("contractBands",
            Stream.of(ContractBand.values()).sorted(Comparator.comparing(ContractBand::getDisplayOrder)).collect(
                Collectors.toList()));
    breadcrumbService.fromUpcomingTenders(projectId, modelAndView, PAGE_NAME_SINGULAR);
    return modelAndView;
  }
}
