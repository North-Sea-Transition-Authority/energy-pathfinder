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
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateContextService;

@Service
public class OperatorActionService {

  public static final String PROVIDE_UPDATE_ACTION_PROMPT = "Provide update";
  public static final int PROVIDE_UPDATE_ACTION_DISPLAY_ORDER = 10;

  private final ProjectContextService projectContextService;
  private final ProjectUpdateContextService projectUpdateContextService;

  @Autowired
  public OperatorActionService(
      ProjectContextService projectContextService,
      ProjectUpdateContextService projectUpdateContextService) {
    this.projectContextService = projectContextService;
    this.projectUpdateContextService = projectUpdateContextService;
  }

  public List<UserActionWithDisplayOrder> getActions(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    var actions = new ArrayList<UserActionWithDisplayOrder>();

    actions.add(getProvideUpdateAction(
        projectDetail,
        projectUpdateContextService.canBuildContext(
            projectDetail,
            user,
            projectContextService.getProjectStatusesForClass(OperatorUpdateController.class),
            projectContextService.getProjectPermissionsForClass(OperatorUpdateController.class)
        )
    ));

    return actions;
  }

  protected UserActionWithDisplayOrder getProvideUpdateAction(ProjectDetail projectDetail, boolean isEnabled) {
    return new UserActionWithDisplayOrder(
        new LinkButton(
            PROVIDE_UPDATE_ACTION_PROMPT,
            ReverseRouter.route(on(OperatorUpdateController.class).startPage(
                projectDetail.getProject().getId(),
                null
            )),
            isEnabled,
            ButtonType.PRIMARY
        ), PROVIDE_UPDATE_ACTION_DISPLAY_ORDER);
  }
}
