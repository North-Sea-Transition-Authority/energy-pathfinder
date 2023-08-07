package uk.co.ogauthority.pathfinder.controller.project.awardedcontract.forwardworkplan;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.AllowProjectContributorAccess;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.AwardContractController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSummaryForm;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSummaryService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@AllowProjectContributorAccess
@ProjectTypeCheck(types = ProjectType.FORWARD_WORK_PLAN)
@RequestMapping("/project/{projectId}/work-plan-awarded-contracts/summary")
public class ForwardWorkPlanAwardedContractSummaryController extends AwardContractController {

  private final ForwardWorkPlanAwardedContractSummaryService awardedContractSummaryService;
  private final ValidationService validationService;

  public ForwardWorkPlanAwardedContractSummaryController(BreadcrumbService breadcrumbService,
                                                         ControllerHelperService controllerHelperService,
                                                         ProjectSectionItemOwnershipService projectSectionItemOwnershipService,
                                                         ForwardWorkPlanAwardedContractSummaryService awardedContractSummaryService,
                                                         ValidationService validationService) {
    super(breadcrumbService, controllerHelperService, projectSectionItemOwnershipService);
    this.awardedContractSummaryService = awardedContractSummaryService;
    this.validationService = validationService;
  }

  @GetMapping
  public ModelAndView viewAwardedContracts(@PathVariable("projectId") Integer projectId,
                                           ProjectContext projectContext) {
    var projectDetail = projectContext.getProjectDetails();
    var awardedContractViews = awardedContractSummaryService.getAwardedContractViews(projectDetail);
    var form = awardedContractSummaryService.getForm(projectDetail);
    return getForwardWorkPlanAwardedContractsSummaryModelAndView(projectId, awardedContractViews, form, projectDetail);
  }

  @PostMapping
  public ModelAndView saveAwardedContractSummary(@PathVariable("projectId") Integer projectId,
                                                 @Valid @ModelAttribute("form") ForwardWorkPlanAwardedContractSummaryForm form,
                                                 BindingResult bindingResult,
                                                 ProjectContext projectContext) {
    bindingResult = validationService.validate(form, bindingResult, ValidationType.FULL);
    var projectDetail = projectContext.getProjectDetails();
    var awardedContractViews = awardedContractSummaryService.getAwardedContractViews(projectDetail);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getForwardWorkPlanAwardedContractsSummaryModelAndView(projectId, awardedContractViews, form, projectDetail),
        form,
        () -> saveAwardedContractSummary(form, projectDetail, projectId)
    );
  }

  private ModelAndView saveAwardedContractSummary(ForwardWorkPlanAwardedContractSummaryForm form,
                                                  ProjectDetail projectDetail,
                                                  Integer projectId) {
    awardedContractSummaryService.saveAwardedContractSummary(form, projectDetail);
    if (Boolean.TRUE.equals(form.getHasOtherContractsToAdd())) {
      return ReverseRouter.redirect(on(ForwardWorkPlanAwardedContractController.class)
          .addAwardedContract(projectId, null));
    }
    return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
  }

  private ModelAndView getForwardWorkPlanAwardedContractsSummaryModelAndView(Integer projectId,
                                                                             List<ForwardWorkPlanAwardedContractView> awardedContractViews,
                                                                             ForwardWorkPlanAwardedContractSummaryForm form,
                                                                             ProjectDetail projectDetail) {
    var modelAndView = new ModelAndView("project/awardedcontract/forwardworkplan/forwardWorkPlanAwardedContractFormSummary")
        .addObject("pageTitle", PAGE_NAME)
        .addObject("awardedContractViews", awardedContractViews)
        .addObject("addAwardedContractUrl",
            ReverseRouter.route(on(ForwardWorkPlanAwardedContractController.class).addAwardedContract(projectId, null))
        )
        .addObject("backToTaskListUrl",
            ControllerUtils.getBackToTaskListUrl(projectId)
        )
        .addObject("form", form)
        .addObject("projectTypeDisplayNameLowercase", projectDetail.getProjectType().getLowercaseDisplayName());
    breadcrumbService.fromTaskList(projectId, modelAndView, PAGE_NAME);
    return modelAndView;
  }
}
