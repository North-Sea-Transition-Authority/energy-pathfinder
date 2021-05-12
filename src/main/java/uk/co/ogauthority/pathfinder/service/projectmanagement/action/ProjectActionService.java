package uk.co.ogauthority.pathfinder.service.projectmanagement.action;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.projectarchive.ArchiveProjectController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserActionWithDisplayOrder;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

@Service
public class ProjectActionService {

  protected UserActionWithDisplayOrder getArchiveAction(ProjectDetail projectDetail, int displayOrder) {
    return new UserActionWithDisplayOrder(
        new LinkButton(
            String.format("Archive %s", projectDetail.getProjectType().getLowercaseDisplayName()),
            ReverseRouter.route(on(ArchiveProjectController.class).getArchiveProject(
                projectDetail.getProject().getId(),
                null,
                null
            )),
            true,
            ButtonType.SECONDARY
        ), displayOrder);
  }
}
