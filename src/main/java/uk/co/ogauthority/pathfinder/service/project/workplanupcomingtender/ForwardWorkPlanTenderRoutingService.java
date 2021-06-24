package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanTenderSetup;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class ForwardWorkPlanTenderRoutingService {

  private final ForwardWorkPlanUpcomingTenderModelService workPlanUpcomingTenderModelService;
  private final ForwardWorkPlanTenderSetupService forwardWorkPlanTenderSetupService;
  private final ForwardWorkPlanUpcomingTenderService workPlanUpcomingTenderService;
  private final ForwardWorkPlanUpcomingTenderSummaryService workPlanUpcomingTenderSummaryService;
  private final ForwardWorkPlanTenderCompletionService forwardWorkPlanTenderCompletionService;

  @Autowired
  public ForwardWorkPlanTenderRoutingService(
      ForwardWorkPlanUpcomingTenderModelService workPlanUpcomingTenderModelService,
      ForwardWorkPlanTenderSetupService forwardWorkPlanTenderSetupService,
      ForwardWorkPlanUpcomingTenderService workPlanUpcomingTenderService,
      ForwardWorkPlanUpcomingTenderSummaryService workPlanUpcomingTenderSummaryService,
      ForwardWorkPlanTenderCompletionService forwardWorkPlanTenderCompletionService
  ) {
    this.workPlanUpcomingTenderModelService = workPlanUpcomingTenderModelService;
    this.forwardWorkPlanTenderSetupService = forwardWorkPlanTenderSetupService;
    this.workPlanUpcomingTenderService = workPlanUpcomingTenderService;
    this.workPlanUpcomingTenderSummaryService = workPlanUpcomingTenderSummaryService;
    this.forwardWorkPlanTenderCompletionService = forwardWorkPlanTenderCompletionService;
  }

  public ModelAndView getUpcomingTenderSetupRoute(ProjectDetail projectDetail) {

    final var setupForm = forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupFormFromDetail(projectDetail);

    final var upcomingTenders = workPlanUpcomingTenderService.getUpcomingTendersForDetail(projectDetail);

    // if user has indicated they have tenders to add and some tenders have been added, redirect to tender summary page
    if (BooleanUtils.isTrue(setupForm.getHasTendersToAdd()) && !upcomingTenders.isEmpty()) {
      return ReverseRouter.redirect(on(ForwardWorkPlanUpcomingTenderController.class).viewUpcomingTenders(
          projectDetail.getProject().getId(),
          null
      ));
    } else {
      // otherwise keep them on the setup page
      return workPlanUpcomingTenderModelService.getUpcomingTenderSetupModelAndView(
          projectDetail,
          setupForm
      );
    }
  }

  public ModelAndView getPostSaveUpcomingTenderSetupRoute(ForwardWorkPlanTenderSetup forwardWorkPlanTenderSetup,
                                                          ProjectDetail projectDetail) {

    final var projectId = projectDetail.getProject().getId();

    if (BooleanUtils.isTrue(forwardWorkPlanTenderSetup.getHasTendersToAdd())) {
      return ReverseRouter.redirect(on(ForwardWorkPlanUpcomingTenderController.class).addUpcomingTender(projectId, null));
    } else {
      return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
    }
  }

  public ModelAndView getViewUpcomingTendersRoute(ProjectDetail projectDetail) {

    final var tenderViews = workPlanUpcomingTenderSummaryService.getSummaryViews(projectDetail);

    final var form = forwardWorkPlanTenderCompletionService.getForwardWorkPlanTenderCompletionFormFromDetail(
        projectDetail
    );

    if (tenderViews.isEmpty()) {
      forwardWorkPlanTenderCompletionService.resetHasOtherTendersToAdd(form, projectDetail);
      return ReverseRouter.redirect(on(ForwardWorkPlanUpcomingTenderController.class).getUpcomingTenderSetup(
          projectDetail.getProject().getId(),
          null,
          null
      ));
    } else {
      // if user previously indicated yes, reset the answer so they
      // are prompted to answer again
      if (BooleanUtils.isTrue(form.getHasOtherTendersToAdd())) {
        form.setHasOtherTendersToAdd(null);
      }

      return workPlanUpcomingTenderModelService.getViewUpcomingTendersModelAndView(
          projectDetail,
          tenderViews,
          ValidationResult.NOT_VALIDATED,
          form,
          ReverseRouter.emptyBindingResult()
      );
    }
  }

  public ModelAndView getPostSaveUpcomingTendersRoute(ForwardWorkPlanTenderSetup forwardWorkPlanTenderSetup,
                                                      ProjectDetail projectDetail) {

    final var projectId = projectDetail.getProject().getId();

    return BooleanUtils.isTrue(forwardWorkPlanTenderSetup.getHasOtherTendersToAdd())
        ? ReverseRouter.redirect(on(ForwardWorkPlanUpcomingTenderController.class).addUpcomingTender(projectId, null))
        : ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
  }

  public ModelAndView getAddUpcomingTenderRoute(ProjectDetail projectDetail) {

    final var setupForm = forwardWorkPlanTenderSetupService.getForwardWorkPlanTenderSetupFormFromDetail(
        projectDetail
    );

    if (BooleanUtils.isTrue(setupForm.getHasTendersToAdd())) {
      return workPlanUpcomingTenderModelService.getUpcomingTenderFormModelAndView(
          projectDetail,
          new ForwardWorkPlanUpcomingTenderForm()
      );
    } else {
      return ReverseRouter.redirect(on(ForwardWorkPlanUpcomingTenderController.class).getUpcomingTenderSetup(
          projectDetail.getProject().getId(),
          null,
          null
      ));
    }
  }
}