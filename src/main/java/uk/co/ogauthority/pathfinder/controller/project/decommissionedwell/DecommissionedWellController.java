package uk.co.ogauthority.pathfinder.controller.project.decommissionedwell;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

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
import uk.co.ogauthority.pathfinder.controller.project.ProjectFormPageController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.rest.DecommissionedWellRestController;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.InputEntryType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.Quarter;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedwell.DecommissionedWellForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.decommissionedwell.DecommissionedWellService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/wells")
public class DecommissionedWellController extends ProjectFormPageController {

  public static final String TASK_LIST_NAME = "Wells";
  public static final String SUMMARY_PAGE_NAME = "Wells to be decommissioned";
  public static final String FORM_PAGE_NAME = "Wells";

  private final DecommissionedWellService decommissionedWellService;

  @Autowired
  public DecommissionedWellController(BreadcrumbService breadcrumbService,
                                      ControllerHelperService controllerHelperService,
                                      DecommissionedWellService decommissionedWellService) {
    super(breadcrumbService, controllerHelperService);
    this.decommissionedWellService = decommissionedWellService;
  }

  @GetMapping
  public ModelAndView viewWellsToBeDecommissioned(@PathVariable("projectId") Integer projectId,
                                                  ProjectContext projectContext) {
    return getDecommissionedWellsSummary(projectId);
  }

  @GetMapping("/wells-to-be-decommissioned")
  public ModelAndView addWellsToBeDecommissioned(@PathVariable("projectId") Integer projectId,
                                                 ProjectContext projectContext) {
    return getWellsToBeDecommissionedModelAndView(projectId, new DecommissionedWellForm());
  }

  @PostMapping("/wells-to-be-decommissioned")
  public ModelAndView createWellsToBeDecommissioned(@PathVariable("projectId") Integer projectId,
                                                    @Valid @ModelAttribute("form") DecommissionedWellForm form,
                                                    BindingResult bindingResult,
                                                    ValidationType validationType,
                                                    ProjectContext projectContext) {
    bindingResult = decommissionedWellService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getWellsToBeDecommissionedModelAndView(projectId, form),
        form,
        () -> {
          decommissionedWellService.createDecommissionedWell(form, projectContext.getProjectDetails());
          return getDecommissionedWellsSummaryRedirect(projectId);
        }
    );
  }

  @GetMapping("/wells-to-be-decommissioned/{decommissionedWellId}/edit")
  public ModelAndView getWellsToBeDecommissioned(@PathVariable("projectId") Integer projectId,
                                                 @PathVariable("decommissionedWellId") Integer decommissionedWellId,
                                                 ProjectContext projectContext) {
    var form = decommissionedWellService.getForm(
        decommissionedWellId,
        projectContext.getProjectDetails()
    );
    return getWellsToBeDecommissionedModelAndView(projectId, form);
  }

  @PostMapping("/wells-to-be-decommissioned/{decommissionedWellId}/edit")
  public ModelAndView updateWellsToBeDecommissioned(@PathVariable("projectId") Integer projectId,
                                                    @PathVariable("decommissionedWellId") Integer decommissionedWellId,
                                                    @Valid @ModelAttribute("form") DecommissionedWellForm form,
                                                    BindingResult bindingResult,
                                                    ValidationType validationType,
                                                    ProjectContext projectContext) {
    bindingResult = decommissionedWellService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getWellsToBeDecommissionedModelAndView(projectId, form),
        form,
        () -> {
          decommissionedWellService.updateDecommissionedWell(
              decommissionedWellId,
              projectContext.getProjectDetails(),
              form
          );
          return getDecommissionedWellsSummaryRedirect(projectId);
        }
    );
  }

  private ModelAndView getDecommissionedWellsSummary(Integer projectId) {
    var modelAndView = new ModelAndView("project/decommissionedwell/decommissionedWellSummary")
        .addObject("pageName", SUMMARY_PAGE_NAME)
        .addObject("addDecommissionedWellUrl",
            ReverseRouter.route(on(DecommissionedWellController.class).addWellsToBeDecommissioned(projectId, null))
        )
        .addObject("projectSetupUrl", ControllerUtils.getProjectSetupUrl(projectId));

    breadcrumbService.fromTaskList(projectId, modelAndView, TASK_LIST_NAME);

    return modelAndView;
  }

  private ModelAndView getWellsToBeDecommissionedModelAndView(Integer projectId, DecommissionedWellForm form) {
    var modelAndView = new ModelAndView("project/decommissionedwell/decommissionedWell")
        .addObject("form", form)
        .addObject("pageName", FORM_PAGE_NAME)
        .addObject("preSelectedType", decommissionedWellService.getPreSelectedType(form))
        .addObject("typeRestUrl", getTypeRestUrl())
        .addObject("quarters", Quarter.getAllAsMap())
        .addObject("plugAbandonmentDateTypes", InputEntryType.getAllAsMap())
        .addObject("preSelectedOperationalStatus",
            decommissionedWellService.getPreSelectedOperationalStatus(form)
        )
        .addObject("operationalStatusRestUrl", getOperationalStatusRestUrl())
        .addObject("preSelectedMechanicalStatus",
            decommissionedWellService.getPreSelectedMechanicalStatus(form)
        )
        .addObject("mechanicalStatusRestUrl", getMechanicalStatusRestUrl());

    breadcrumbService.fromDecommissionedWells(projectId, modelAndView, FORM_PAGE_NAME);

    return modelAndView;
  }

  private ModelAndView getDecommissionedWellsSummaryRedirect(Integer projectId) {
    return ReverseRouter.redirect(on(DecommissionedWellController.class).viewWellsToBeDecommissioned(
        projectId,
        null
    ));
  }

  private String getTypeRestUrl() {
    return SearchSelectorService.route(on(DecommissionedWellRestController.class).searchTypes(null));
  }

  private String getOperationalStatusRestUrl() {
    return SearchSelectorService.route(on(DecommissionedWellRestController.class).searchOperationalStatuses(null));
  }

  private String getMechanicalStatusRestUrl() {
    return SearchSelectorService.route(on(DecommissionedWellRestController.class).searchMechanicalStatuses(null));
  }

}
