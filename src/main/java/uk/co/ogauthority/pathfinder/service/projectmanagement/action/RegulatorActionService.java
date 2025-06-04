package uk.co.ogauthority.pathfinder.service.projectmanagement.action;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectarchive.ArchiveProjectController;
import uk.co.ogauthority.pathfinder.controller.projectassessment.ProjectAssessmentController;
import uk.co.ogauthority.pathfinder.controller.projecttransfer.ProjectTransferController;
import uk.co.ogauthority.pathfinder.controller.projectupdate.RegulatorUpdateController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserActionWithDisplayOrder;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentContextService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorProjectUpdateContextService;

@Service
public class RegulatorActionService {

  public static final String PROVIDE_ASSESSMENT_ACTION_PROMPT = "Provide assessment";
  public static final int PROVIDE_ASSESSMENT_ACTION_DISPLAY_ORDER = 10;

  public static final String REQUEST_UPDATE_ACTION_PROMPT = "Request update";
  public static final int REQUEST_UPDATE_ACTION_DISPLAY_ORDER = 20;

  public static final int TRANSFER_PROJECT_ACTION_DISPLAY_ORDER = 30;

  public static final int ARCHIVE_ACTION_DISPLAY_ORDER = 40;

  private final ProjectActionService projectActionService;
  private final ProjectContextService projectContextService;
  private final ProjectAssessmentContextService projectAssessmentContextService;
  private final RegulatorProjectUpdateContextService regulatorProjectUpdateContextService;

  @Autowired
  public RegulatorActionService(
      ProjectActionService projectActionService,
      ProjectContextService projectContextService,
      ProjectAssessmentContextService projectAssessmentContextService,
      RegulatorProjectUpdateContextService regulatorProjectUpdateContextService) {
    this.projectActionService = projectActionService;
    this.projectContextService = projectContextService;
    this.projectAssessmentContextService = projectAssessmentContextService;
    this.regulatorProjectUpdateContextService = regulatorProjectUpdateContextService;
  }

  public List<UserActionWithDisplayOrder> getActions(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    var actions = new ArrayList<UserActionWithDisplayOrder>();

    var projectId = projectDetail.getProject().getId();

    var canAssess = projectAssessmentContextService.canBuildContext(
        projectDetail,
        user,
        ProjectAssessmentController.class
    );
    if (canAssess) {
      actions.add(getProvideAssessmentAction(projectId));
    }

    var canRequestUpdate = regulatorProjectUpdateContextService.canBuildContext(
        projectDetail,
        user,
        RegulatorUpdateController.class
    );
    if (canRequestUpdate) {
      actions.add(getRequestUpdateAction(projectId));
    }

    var canTransfer = projectContextService.canBuildContext(
        projectDetail,
        user,
        ProjectTransferController.class
    );
    if (canTransfer) {
      actions.add(getTransferProjectAction(projectDetail));
    }

    var canArchive = projectContextService.canBuildContext(
        projectDetail,
        user,
        ArchiveProjectController.class
    );
    if (canArchive) {
      actions.add(projectActionService.getArchiveAction(projectDetail, ARCHIVE_ACTION_DISPLAY_ORDER));
    }

    return actions;
  }

  protected UserActionWithDisplayOrder getProvideAssessmentAction(Integer projectId) {
    return new UserActionWithDisplayOrder(
        new LinkButton(
            PROVIDE_ASSESSMENT_ACTION_PROMPT,
            ReverseRouter.route(on(ProjectAssessmentController.class).getProjectAssessment(projectId, null, null)),
            true,
            ButtonType.PRIMARY
        ), PROVIDE_ASSESSMENT_ACTION_DISPLAY_ORDER);
  }

  protected UserActionWithDisplayOrder getRequestUpdateAction(Integer projectId) {
    return new UserActionWithDisplayOrder(
        new LinkButton(
            REQUEST_UPDATE_ACTION_PROMPT,
            ReverseRouter.route(on(RegulatorUpdateController.class).getRequestUpdate(projectId, null, null)),
            true,
            ButtonType.SECONDARY
        ), REQUEST_UPDATE_ACTION_DISPLAY_ORDER);
  }

  protected UserActionWithDisplayOrder getTransferProjectAction(ProjectDetail projectDetail) {
    return new UserActionWithDisplayOrder(
        new LinkButton(
            String.format("Change %s operator/developer", projectDetail.getProjectType().getLowercaseDisplayName()),
            ReverseRouter.route(on(ProjectTransferController.class).getTransferProject(
                projectDetail.getProject().getId(),
                null,
                null
            )),
            true,
            ButtonType.SECONDARY
        ), TRANSFER_PROJECT_ACTION_DISPLAY_ORDER);
  }
}
