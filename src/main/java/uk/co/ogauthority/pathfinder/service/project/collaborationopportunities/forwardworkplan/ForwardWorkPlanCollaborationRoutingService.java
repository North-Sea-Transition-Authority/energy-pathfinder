package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan.ForwardWorkPlanCollaborationOpportunityController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetup;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class ForwardWorkPlanCollaborationRoutingService {

  private final ForwardWorkPlanCollaborationOpportunityService collaborationOpportunityService;
  private final ForwardWorkPlanCollaborationOpportunityModelService collaborationOpportunityModelService;
  private final ForwardWorkPlanCollaborationSetupService collaborationSetupService;
  private final ForwardWorkPlanCollaborationOpportunitiesSummaryService collaborationOpportunitiesSummaryService;
  private final ForwardWorkPlanCollaborationCompletionService collaborationCompletionService;

  @Autowired
  public ForwardWorkPlanCollaborationRoutingService(
      ForwardWorkPlanCollaborationOpportunityService collaborationOpportunityService,
      ForwardWorkPlanCollaborationOpportunityModelService collaborationOpportunityModelService,
      ForwardWorkPlanCollaborationSetupService collaborationSetupService,
      ForwardWorkPlanCollaborationOpportunitiesSummaryService collaborationOpportunitiesSummaryService,
      ForwardWorkPlanCollaborationCompletionService collaborationCompletionService
  ) {
    this.collaborationOpportunityService = collaborationOpportunityService;
    this.collaborationOpportunityModelService = collaborationOpportunityModelService;
    this.collaborationSetupService = collaborationSetupService;
    this.collaborationOpportunitiesSummaryService = collaborationOpportunitiesSummaryService;
    this.collaborationCompletionService = collaborationCompletionService;
  }

  public ModelAndView getCollaborationOpportunitySetupRoute(ProjectDetail projectDetail) {

    final var setupForm = collaborationSetupService.getCollaborationSetupFormFromDetail(projectDetail);

    final var collaborationOpportunities = collaborationOpportunityService.getOpportunitiesForDetail(projectDetail);

    // if user has indicated they have collaborations to add and some collaborations have been added, redirect to summary page
    if (BooleanUtils.isTrue(setupForm.getHasCollaborationsToAdd()) && !collaborationOpportunities.isEmpty()) {
      return ReverseRouter.redirect(on(ForwardWorkPlanCollaborationOpportunityController.class).viewCollaborationOpportunities(
          projectDetail.getProject().getId(),
          null
      ));
    } else {
      // otherwise keep them on the setup page
      return collaborationOpportunityModelService.getCollaborationSetupModelAndView(
          projectDetail,
          setupForm
      );
    }
  }

  public ModelAndView getPostSaveUpcomingCollaborationsSetupRoute(
      ForwardWorkPlanCollaborationSetup forwardWorkPlanCollaborationSetup,
      ProjectDetail projectDetail
  ) {

    final var projectId = projectDetail.getProject().getId();

    if (BooleanUtils.isTrue(forwardWorkPlanCollaborationSetup.getHasCollaborationToAdd())) {
      return ReverseRouter.redirect(on(ForwardWorkPlanCollaborationOpportunityController.class)
          .addCollaborationOpportunity(projectId, null));
    } else {
      return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
    }
  }

  public ModelAndView getViewCollaborationsRoute(ProjectDetail projectDetail) {

    final var collaborationViews = collaborationOpportunitiesSummaryService.getSummaryViews(projectDetail);

    final var completionForm = collaborationCompletionService.getForwardWorkPlanCollaborationCompletionFormFromDetail(
        projectDetail
    );

    if (collaborationViews.isEmpty()) {

      collaborationCompletionService.resetHasOtherCollaborationsToAdd(completionForm, projectDetail);

      return ReverseRouter.redirect(on(ForwardWorkPlanCollaborationOpportunityController.class).getCollaborationOpportunitySetup(
          projectDetail.getProject().getId(),
          null,
          null
      ));
    } else {

      // if user previously indicated yes, reset the answer so they
      // are prompted to answer again
      if (BooleanUtils.isTrue(completionForm.getHasOtherCollaborationsToAdd())) {
        completionForm.setHasOtherCollaborationsToAdd(null);
      }

      return collaborationOpportunityModelService.getViewCollaborationOpportunitiesModelAndView(
          projectDetail,
          ValidationResult.NOT_VALIDATED,
          collaborationViews,
          completionForm,
          ReverseRouter.emptyBindingResult()
      );
    }
  }

  public ModelAndView getAddCollaborationOpportunityRoute(ModelAndView fileUploadModelAndView,
                                                          ForwardWorkPlanCollaborationOpportunityForm form,
                                                          ProjectDetail projectDetail) {

    final var setupForm = collaborationSetupService.getCollaborationSetupFormFromDetail(
        projectDetail
    );

    final var projectId = projectDetail.getProject().getId();

    if (BooleanUtils.isTrue(setupForm.getHasCollaborationsToAdd())) {
      return collaborationOpportunityModelService.getCollaborationOpportunityModelAndView(
          fileUploadModelAndView,
          form,
          projectId
      );
    } else {
      return ReverseRouter.redirect(on(ForwardWorkPlanCollaborationOpportunityController.class).getCollaborationOpportunitySetup(
          projectId,
          null,
          null
      ));
    }
  }

  public ModelAndView getPostSaveCollaborationsRoute(ForwardWorkPlanCollaborationSetup forwardWorkPlanCollaborationSetup,
                                                     ProjectDetail projectDetail) {

    final var projectId = projectDetail.getProject().getId();

    return BooleanUtils.isTrue(forwardWorkPlanCollaborationSetup.getHasOtherCollaborationToAdd())
        ? ReverseRouter.redirect(on(ForwardWorkPlanCollaborationOpportunityController.class).addCollaborationOpportunity(projectId, null))
        : ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
  }

}
