package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.WorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.controller.rest.WorkPlanUpcomingTenderRestController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.WorkPlanUpcomingTenderForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Service
public class WorkPlanUpcomingTenderService implements ProjectFormSectionService {

  public static final String TEMPLATE_PATH = "project/workplanupcomingtender/workPlanUpcomingTenderFormSummary";

  private final BreadcrumbService breadcrumbService;
  private final FunctionService functionService;

  @Autowired
  public WorkPlanUpcomingTenderService(BreadcrumbService breadcrumbService,
                                       FunctionService functionService) {
    this.breadcrumbService = breadcrumbService;
    this.functionService = functionService;
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
