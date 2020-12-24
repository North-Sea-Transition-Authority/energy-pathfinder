package uk.co.ogauthority.pathfinder.service.projectmanagement.action;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.projectarchive.ArchiveProjectController;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserActionWithDisplayOrder;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

@Service
public class ProjectActionService {

  public static final String ARCHIVE_ACTION_PROMPT = "Archive project";

  protected UserActionWithDisplayOrder getArchiveAction(Integer projectId, int displayOrder, boolean isEnabled) {
    return new UserActionWithDisplayOrder(
        new LinkButton(
            ARCHIVE_ACTION_PROMPT,
            ReverseRouter.route(on(ArchiveProjectController.class).getArchiveProject(
                projectId,
                null,
                null
            )),
            isEnabled,
            ButtonType.SECONDARY
        ), displayOrder);
  }
}
