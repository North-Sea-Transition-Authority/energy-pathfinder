package uk.co.ogauthority.pathfinder.service.projectmanagement.action;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectupdate.OperatorUpdateController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserActionWithDisplayOrder;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateContextService;

@Service
public class OperatorActionService {

  public static final String PROVIDE_UPDATE_ACTION_PROMPT = "Provide update";
  public static final int PROVIDE_UPDATE_ACTION_DISPLAY_ORDER = 10;

  public static final String PROVIDE_NO_UPDATE_NOTIFICATION_ACTION_PROMPT = "Confirm no changes";
  public static final int PROVIDE_NO_UPDATE_NOTIFICATION_ACTION_DISPLAY_ORDER = 20;

  private final ProjectUpdateContextService projectUpdateContextService;

  @Autowired
  public OperatorActionService(ProjectUpdateContextService projectUpdateContextService) {
    this.projectUpdateContextService = projectUpdateContextService;
  }

  public List<UserActionWithDisplayOrder> getActions(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    var actions = new ArrayList<UserActionWithDisplayOrder>();

    var projectId = projectDetail.getProject().getId();

    var updateActionsEnabled = projectUpdateContextService.canBuildContext(
        projectDetail,
        user,
        OperatorUpdateController.class
    );

    actions.add(getProvideUpdateAction(
        projectId,
        updateActionsEnabled
    ));

    actions.add(getProvideNoUpdateNotificationAction(
        projectId,
        updateActionsEnabled
    ));

    return actions;
  }

  protected UserActionWithDisplayOrder getProvideUpdateAction(Integer projectId, boolean isEnabled) {
    return new UserActionWithDisplayOrder(
        new LinkButton(
            PROVIDE_UPDATE_ACTION_PROMPT,
            ReverseRouter.route(on(OperatorUpdateController.class).startPage(
                projectId,
                null
            )),
            isEnabled,
            ButtonType.PRIMARY
        ), PROVIDE_UPDATE_ACTION_DISPLAY_ORDER);
  }

  protected UserActionWithDisplayOrder getProvideNoUpdateNotificationAction(Integer projectId, boolean isEnabled) {
    return new UserActionWithDisplayOrder(
        new LinkButton(
            PROVIDE_NO_UPDATE_NOTIFICATION_ACTION_PROMPT,
            ReverseRouter.route(on(OperatorUpdateController.class).provideNoUpdate(
                projectId,
                null,
                null
            )),
            isEnabled,
            ButtonType.SECONDARY
        ), PROVIDE_NO_UPDATE_NOTIFICATION_ACTION_DISPLAY_ORDER);
  }
}
