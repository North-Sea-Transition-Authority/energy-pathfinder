package uk.co.ogauthority.pathfinder.service.projectmanagement.action;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectassessment.ProjectAssessmentController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserActionWithDisplayOrder;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentContextService;

@Service
public class RegulatorActionService {

  public static final String PROVIDE_ASSESSMENT_ACTION_PROMPT = "Provide assessment";
  public static final int PROVIDE_ASSESSMENT_ACTION_DISPLAY_ORDER = 10;

  private final ProjectContextService projectContextService;
  private final ProjectAssessmentContextService projectAssessmentContextService;

  @Autowired
  public RegulatorActionService(
      ProjectContextService projectContextService,
      ProjectAssessmentContextService projectAssessmentContextService) {
    this.projectContextService = projectContextService;
    this.projectAssessmentContextService = projectAssessmentContextService;
  }

  public List<UserActionWithDisplayOrder> getActions(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    var actions = new ArrayList<UserActionWithDisplayOrder>();

    actions.add(getProvideAssessmentAction(
        projectDetail,
        projectAssessmentContextService.canBuildContext(
            projectDetail,
            user,
            projectContextService.getProjectStatusesForClass(ProjectAssessmentController.class),
            projectContextService.getProjectPermissionsForClass(ProjectAssessmentController.class)
        )
    ));

    return actions;
  }

  protected UserActionWithDisplayOrder getProvideAssessmentAction(ProjectDetail projectDetail,
                                                                  boolean isEnabled) {
    return new UserActionWithDisplayOrder(
        new LinkButton(
            PROVIDE_ASSESSMENT_ACTION_PROMPT,
            ReverseRouter.route(on(ProjectAssessmentController.class).getProjectAssessment(
                projectDetail.getProject().getId(),
                null
            )),
            isEnabled,
            ButtonType.PRIMARY
        ), PROVIDE_ASSESSMENT_ACTION_DISPLAY_ORDER);
  }
}
