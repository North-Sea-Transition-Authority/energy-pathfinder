package uk.co.ogauthority.pathfinder.service.project.tasks;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.controller.project.CancelDraftProjectVersionController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.service.project.cancellation.CancelDraftProjectVersionService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;

@Service
public class TaskListService {

  public static final String TASK_LIST_TEMPLATE_PATH = "project/taskList";

  private final TaskListGroupsService taskListGroupsService;

  private final ServiceProperties serviceProperties;

  private final CancelDraftProjectVersionService cancelDraftProjectVersionService;

  private final WebUserAccountService webUserAccountService;

  @Autowired
  public TaskListService(TaskListGroupsService taskListGroupsService,
                         ServiceProperties serviceProperties,
                         CancelDraftProjectVersionService cancelDraftProjectVersionService,
                         WebUserAccountService webUserAccountService) {
    this.taskListGroupsService = taskListGroupsService;
    this.serviceProperties = serviceProperties;
    this.cancelDraftProjectVersionService = cancelDraftProjectVersionService;
    this.webUserAccountService = webUserAccountService;
  }

  public ModelAndView getTaskListModelAndView(ProjectDetail detail, Set<UserToProjectRelationship> relationships) {
    var ownerEmail = webUserAccountService.getWebUserAccount(detail.getCreatedByWua())
        .map(WebUserAccount::getEmailAddress)
        .orElse("");
    var taskListGroups = taskListGroupsService.getTaskListGroups(detail, relationships);
    var modelAndView = new ModelAndView(TASK_LIST_TEMPLATE_PATH)
        .addObject("isUpdate", !detail.isFirstVersion())
        .addObject("hasTaskListGroups", !taskListGroups.isEmpty())
        .addObject("groups", taskListGroups)
        .addObject("cancelDraftUrl", ReverseRouter.route(on(CancelDraftProjectVersionController.class)
            .getCancelDraft(detail.getProject().getId(), null, null))
        )
        .addObject("taskListPageHeading", getTaskListPageHeading(detail))
        .addObject("isCancellable", cancelDraftProjectVersionService.isCancellable(detail))
        .addObject("canDisplayEmail", !ownerEmail.isBlank())
        .addObject("ownerEmail", ownerEmail);

    ProjectTypeModelUtil.addProjectTypeDisplayNameAttributesToModel(modelAndView, detail);

    return modelAndView;
  }

  private String getTaskListPageHeading(ProjectDetail projectDetail) {
    return (ProjectService.isInfrastructureProject(projectDetail))
        ? String.format("%s %s", serviceProperties.getServiceName(), ProjectService.getProjectTypeDisplayNameLowercase(projectDetail))
        : ProjectService.getProjectTypeDisplayName(projectDetail);
  }
}
