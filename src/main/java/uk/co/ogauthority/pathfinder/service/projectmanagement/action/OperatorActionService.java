package uk.co.ogauthority.pathfinder.service.projectmanagement.action;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectarchive.ArchiveProjectController;
import uk.co.ogauthority.pathfinder.controller.projectupdate.OperatorUpdateController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserActionWithDisplayOrder;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.projectupdate.OperatorProjectUpdateContextService;

@Service
public class OperatorActionService {

  public static final String PROVIDE_UPDATE_ACTION_PROMPT = "Provide update";
  public static final int PROVIDE_UPDATE_ACTION_DISPLAY_ORDER = 10;

  public static final String PROVIDE_NO_UPDATE_NOTIFICATION_ACTION_PROMPT = "Confirm no changes";
  public static final int PROVIDE_NO_UPDATE_NOTIFICATION_ACTION_DISPLAY_ORDER = 20;

  public static final int ARCHIVE_ACTION_DISPLAY_ORDER = 30;

  private final ProjectActionService projectActionService;
  private final ProjectContextService projectContextService;
  private final OperatorProjectUpdateContextService operatorProjectUpdateContextService;

  @Autowired
  public OperatorActionService(
      ProjectActionService projectActionService,
      ProjectContextService projectContextService,
      OperatorProjectUpdateContextService operatorProjectUpdateContextService) {
    this.projectActionService = projectActionService;
    this.projectContextService = projectContextService;
    this.operatorProjectUpdateContextService = operatorProjectUpdateContextService;
  }

  public List<UserActionWithDisplayOrder> getActions(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    var actions = new ArrayList<UserActionWithDisplayOrder>();

    var projectId = projectDetail.getProject().getId();

    var canUpdate = operatorProjectUpdateContextService.canBuildContext(
        projectDetail,
        user,
        OperatorUpdateController.class
    );
    if (canUpdate) {
      actions.add(getProvideUpdateAction(projectId));
      actions.add(getProvideNoUpdateNotificationAction(projectId));
    }

    var canArchive = projectContextService.canBuildContext(
        projectDetail,
        user,
        ArchiveProjectController.class
    );
    if (canArchive) {
      actions.add(projectActionService.getArchiveAction(
          projectId,
          ARCHIVE_ACTION_DISPLAY_ORDER
      ));
    }

    return actions;
  }

  protected UserActionWithDisplayOrder getProvideUpdateAction(Integer projectId) {
    return new UserActionWithDisplayOrder(
        new LinkButton(
            PROVIDE_UPDATE_ACTION_PROMPT,
            ReverseRouter.route(on(OperatorUpdateController.class).startPage(
                projectId,
                null
            )),
            true,
            ButtonType.PRIMARY
        ), PROVIDE_UPDATE_ACTION_DISPLAY_ORDER);
  }

  protected UserActionWithDisplayOrder getProvideNoUpdateNotificationAction(Integer projectId) {
    return new UserActionWithDisplayOrder(
        new LinkButton(
            PROVIDE_NO_UPDATE_NOTIFICATION_ACTION_PROMPT,
            ReverseRouter.route(on(OperatorUpdateController.class).provideNoUpdate(
                projectId,
                null,
                null
            )),
            true,
            ButtonType.SECONDARY
        ), PROVIDE_NO_UPDATE_NOTIFICATION_ACTION_DISPLAY_ORDER);
  }
}
