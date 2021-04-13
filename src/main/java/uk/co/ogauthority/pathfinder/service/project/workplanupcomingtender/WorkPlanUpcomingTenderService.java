package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.WorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;

@Service
public class WorkPlanUpcomingTenderService implements ProjectFormSectionService {

  public static final String TEMPLATE_PATH = "project/workplanupcomingtender/workPlanUpcomingTenderFormSummary";

  private final BreadcrumbService breadcrumbService;

  @Autowired
  public WorkPlanUpcomingTenderService(BreadcrumbService breadcrumbService) {
    this.breadcrumbService = breadcrumbService;
  }

  public ModelAndView getUpcomingTendersModelAndView(Integer projectId) {
    var modelAndView = new ModelAndView(TEMPLATE_PATH)
        .addObject("pageName", WorkPlanUpcomingTenderController.PAGE_NAME);
    breadcrumbService.fromTaskList(projectId, modelAndView, WorkPlanUpcomingTenderController.PAGE_NAME);
    return modelAndView;
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
    //TODO method will be implemented with PAT-470
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {
    //TODO method will be implemented with PAT-535
  }
}
