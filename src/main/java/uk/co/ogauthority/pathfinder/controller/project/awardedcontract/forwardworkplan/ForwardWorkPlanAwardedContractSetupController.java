package uk.co.ogauthority.pathfinder.controller.project.awardedcontract.forwardworkplan;

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
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.ProjectFormPageController;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.AllowProjectContributorAccess;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@AllowProjectContributorAccess
@ProjectTypeCheck(types = ProjectType.FORWARD_WORK_PLAN)
@RequestMapping("/project/{projectId}/work-plan-awarded-contracts/setup")
public class ForwardWorkPlanAwardedContractSetupController extends ProjectFormPageController {

  public static final String PAGE_NAME = "Awarded contracts";
  public static final String SETUP_TEMPLATE_PATH = "project/awardedcontract/forwardworkplan/setupForwardWorkPlanAwardedContracts";


  private final ForwardWorkPlanAwardedContractSetupService setupService;
  private final ForwardWorkPlanAwardedContractService awardedContractService;

  @Autowired
  public ForwardWorkPlanAwardedContractSetupController(BreadcrumbService breadcrumbService,
                                                       ControllerHelperService controllerHelperService,
                                                       ForwardWorkPlanAwardedContractSetupService setupService,
                                                       ForwardWorkPlanAwardedContractService awardedContractService) {
    super(breadcrumbService, controllerHelperService);
    this.setupService = setupService;
    this.awardedContractService = awardedContractService;
  }

  @GetMapping()
  public ModelAndView getAwardedContractSetup(@PathVariable("projectId") Integer projectId,
                                              ProjectContext projectContext,
                                              AuthenticatedUserAccount userAccount) {
    var projectDetail = projectContext.getProjectDetails();
    var form = setupService.getAwardedContractSetupFormFromDetail(projectDetail);
    var hasAwardedContracts = awardedContractService.hasAwardedContracts(projectDetail);
    if (Boolean.TRUE.equals(form.getHasContractToAdd()) && hasAwardedContracts) {
      return goToAwardedContractSummary(projectId);
    }
    return getAwardedContractSetupModelAndView(projectDetail, form);
  }

  @PostMapping()
  public ModelAndView saveAwardedContractSetup(@PathVariable("projectId") Integer projectId,
                                               @Valid @ModelAttribute("form")ForwardWorkPlanAwardedContractSetupForm form,
                                               BindingResult bindingResult,
                                               ProjectContext projectContext,
                                               AuthenticatedUserAccount userAccount) {
    var projectDetail = projectContext.getProjectDetails();
    setupService.validate(form, bindingResult);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getAwardedContractSetupModelAndView(projectDetail, form),
        form,
        () -> saveAndContinueContractJourney(form, projectDetail)
    );
  }

  private ModelAndView saveAndContinueContractJourney(ForwardWorkPlanAwardedContractSetupForm form,
                                                      ProjectDetail projectDetail) {
    var projectId = projectDetail.getProject().getId();
    setupService.saveAwardedContractSetup(form, projectDetail);
    if (Boolean.TRUE.equals(form.getHasContractToAdd())) {
      return goToAwardedContractSummary(projectId);
    }
      return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
  }

  private ModelAndView goToAwardedContractSummary(Integer projectId) {
    return ReverseRouter.redirect(on(ForwardWorkPlanAwardedContractSummaryController.class).viewAwardedContracts(projectId, null));
  }

  private ModelAndView getAwardedContractSetupModelAndView(ProjectDetail projectDetail,
                                                           ForwardWorkPlanAwardedContractSetupForm form) {
    var projectId = projectDetail.getProject().getId();

    var modelAndView = new ModelAndView(SETUP_TEMPLATE_PATH)
        .addObject("pageName", PAGE_NAME)
        .addObject("form", form)
        .addObject("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(projectId));

    breadcrumbService.fromTaskList(
        projectId,
        modelAndView,
        PAGE_NAME
    );

    ProjectTypeModelUtil.addProjectTypeDisplayNameAttributesToModel(modelAndView, projectDetail);

    return modelAndView;
  }
}
