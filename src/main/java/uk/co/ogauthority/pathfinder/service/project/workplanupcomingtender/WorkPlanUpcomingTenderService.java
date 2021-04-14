package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.WorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.controller.rest.WorkPlanUpcomingTenderRestController;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.WorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.WorkPlanUpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.WorkPlanUpcomingTenderFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.WorkPlanUpcomingTenderValidationHint;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.project.workplanupcomingtender.WorkPlanUpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class WorkPlanUpcomingTenderService implements ProjectFormSectionService {

  public static final String TEMPLATE_PATH = "project/workplanupcomingtender/workPlanUpcomingTenderFormSummary";

  private final BreadcrumbService breadcrumbService;
  private final FunctionService functionService;
  private final ValidationService validationService;
  private final WorkPlanUpcomingTenderFormValidator workPlanUpcomingTenderFormValidator;
  private final WorkPlanUpcomingTenderRepository workPlanUpcomingTenderRepository;
  private final SearchSelectorService searchSelectorService;


  @Autowired
  public WorkPlanUpcomingTenderService(BreadcrumbService breadcrumbService,
                                       FunctionService functionService,
                                       ValidationService validationService,
                                       WorkPlanUpcomingTenderFormValidator workPlanUpcomingTenderFormValidator,
                                       WorkPlanUpcomingTenderRepository workPlanUpcomingTenderRepository,
                                       SearchSelectorService searchSelectorService) {
    this.breadcrumbService = breadcrumbService;
    this.functionService = functionService;
    this.validationService = validationService;
    this.workPlanUpcomingTenderFormValidator = workPlanUpcomingTenderFormValidator;
    this.workPlanUpcomingTenderRepository = workPlanUpcomingTenderRepository;
    this.searchSelectorService = searchSelectorService;
  }

  public ModelAndView getUpcomingTendersModelAndView(Integer projectId) {
    var modelAndView = new ModelAndView(TEMPLATE_PATH)
        .addObject("pageName", WorkPlanUpcomingTenderController.PAGE_NAME)
        .addObject("addUpcomingTenderUrl",
            ReverseRouter.route(on(WorkPlanUpcomingTenderController.class).addUpcomingTender(projectId, null)));
    breadcrumbService.fromTaskList(projectId, modelAndView, WorkPlanUpcomingTenderController.PAGE_NAME);
    return modelAndView;
  }

  public ModelAndView getViewUpcomingTendersModelAndView(ProjectDetail projectDetail,
                                                         WorkPlanUpcomingTenderForm form) {
    var modelAndView = new ModelAndView("project/workplanupcomingtender/workPlanUpcomingTender")
        .addObject("pageNameSingular", WorkPlanUpcomingTenderController.PAGE_NAME_SINGULAR)
        .addObject("form", form)
        .addObject("departmentTenderRestUrl", SearchSelectorService.route(
            on(WorkPlanUpcomingTenderRestController.class).searchTenderDepartments(null)
        ));
    breadcrumbService.fromWorkPlanUpcomingTenders(projectDetail.getProject().getId(), modelAndView,
        WorkPlanUpcomingTenderController.PAGE_NAME_SINGULAR);
    return modelAndView;
  }

  public List<RestSearchItem> findDepartmentTenderLikeWithManualEntry(String searchTerm) {
    return functionService.findFunctionsLikeWithManualEntry(searchTerm, FunctionType.WORK_PLAN_UPCOMING_TENDER);
  }

  public BindingResult validate(WorkPlanUpcomingTenderForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    workPlanUpcomingTenderFormValidator.validate(form, bindingResult, new WorkPlanUpcomingTenderValidationHint(validationType));
    return validationService.validate(form, bindingResult, validationType);
  }

  @Transactional
  public WorkPlanUpcomingTender createUpcomingTender(ProjectDetail detail,
                                                     WorkPlanUpcomingTenderForm form) {
    var upcomingTender = new WorkPlanUpcomingTender(detail);
    setCommonFields(upcomingTender, form);
    upcomingTender = workPlanUpcomingTenderRepository.save(upcomingTender);

    return upcomingTender;
  }

  private void setCommonFields(WorkPlanUpcomingTender upcomingTender, WorkPlanUpcomingTenderForm form) {

    searchSelectorService.mapSearchSelectorFormEntryToEntity(
        form.getDepartmentType(),
        Function.values(),
        upcomingTender::setManualDepartmentType,
        upcomingTender::setDepartmentType
    );

    upcomingTender.setDescriptionOfWork(form.getDescriptionOfWork());
    upcomingTender.setEstimatedTenderDate(form.getEstimatedTenderDate().createDateOrNull());

    var contactDetailsForm = form.getContactDetail();
    upcomingTender.setContactName(contactDetailsForm.getName());
    upcomingTender.setPhoneNumber(contactDetailsForm.getPhoneNumber());
    upcomingTender.setJobTitle(contactDetailsForm.getJobTitle());
    upcomingTender.setEmailAddress(contactDetailsForm.getEmailAddress());
  }

  public WorkPlanUpcomingTender getOrError(Integer upcomingTenderId) {
    return workPlanUpcomingTenderRepository.findById(upcomingTenderId).orElseThrow(() ->
        new PathfinderEntityNotFoundException(
            String.format("Unable to find tender with id: %s", upcomingTenderId)
        )
    );
  }

  @Override
  public boolean isComplete(ProjectDetail detail) {
    return false;
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail) {
    return ProjectService.isForwardWorkPlanProject(detail);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {

  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {

  }
}
