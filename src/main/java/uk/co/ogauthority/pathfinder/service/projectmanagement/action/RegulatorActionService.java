package uk.co.ogauthority.pathfinder.service.projectmanagement.action;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectassessment.ProjectAssessmentController;
import uk.co.ogauthority.pathfinder.controller.projectupdate.RegulatorUpdateController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserActionWithDisplayOrder;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentContextService;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateContextService;

@Service
public class RegulatorActionService {

  public static final String PROVIDE_ASSESSMENT_ACTION_PROMPT = "Provide assessment";
  public static final int PROVIDE_ASSESSMENT_ACTION_DISPLAY_ORDER = 10;

  public static final String REQUEST_UPDATE_ACTION_PROMPT = "Request update";
  public static final int REQUEST_UPDATE_ACTION_DISPLAY_ORDER = 20;

  private final ProjectAssessmentContextService projectAssessmentContextService;
  private final ProjectUpdateContextService projectUpdateContextService;

  @Autowired
  public RegulatorActionService(ProjectAssessmentContextService projectAssessmentContextService,
                                ProjectUpdateContextService projectUpdateContextService) {
    this.projectAssessmentContextService = projectAssessmentContextService;
    this.projectUpdateContextService = projectUpdateContextService;
  }

  public List<UserActionWithDisplayOrder> getActions(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    var actions = new ArrayList<UserActionWithDisplayOrder>();

    var projectId = projectDetail.getProject().getId();

    actions.add(getProvideAssessmentAction(
        projectId,
        projectAssessmentContextService.canBuildContext(
            projectDetail,
            user,
            ProjectAssessmentController.class
        )
    ));

    actions.add(getRequestUpdateAction(
        projectId,
        projectUpdateContextService.canBuildContext(
            projectDetail,
            user,
            RegulatorUpdateController.class
        )
    ));

    return actions;
  }

  protected UserActionWithDisplayOrder getProvideAssessmentAction(Integer projectId, boolean isEnabled) {
    return new UserActionWithDisplayOrder(
        new LinkButton(
            PROVIDE_ASSESSMENT_ACTION_PROMPT,
            ReverseRouter.route(on(ProjectAssessmentController.class).getProjectAssessment(projectId, null, null)),
            isEnabled,
            ButtonType.PRIMARY
        ), PROVIDE_ASSESSMENT_ACTION_DISPLAY_ORDER);
  }

  protected UserActionWithDisplayOrder getRequestUpdateAction(Integer projectId, boolean isEnabled) {
    return new UserActionWithDisplayOrder(
        new LinkButton(
            REQUEST_UPDATE_ACTION_PROMPT,
            ReverseRouter.route(on(RegulatorUpdateController.class).getRequestUpdate(projectId, null, null)),
            isEnabled,
            ButtonType.SECONDARY
        ), REQUEST_UPDATE_ACTION_DISPLAY_ORDER);
  }
}
