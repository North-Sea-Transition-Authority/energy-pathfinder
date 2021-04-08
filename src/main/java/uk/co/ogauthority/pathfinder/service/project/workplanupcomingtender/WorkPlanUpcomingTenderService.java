package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.WorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;

@Service
public class WorkPlanUpcomingTenderService implements ProjectFormSectionService {

  public static final String TEMPLATE_PATH = "project/workplanupcomingtender/workPlanUpcomingTender";

  @Autowired
  public WorkPlanUpcomingTenderService() {

  }

  public ModelAndView getUpcomingTendersModelAndView(Integer projectId) {
    var modelAndView = new ModelAndView(TEMPLATE_PATH)
        .addObject("pagename", WorkPlanUpcomingTenderController.PAGE_NAME);
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

  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {

  }
}
