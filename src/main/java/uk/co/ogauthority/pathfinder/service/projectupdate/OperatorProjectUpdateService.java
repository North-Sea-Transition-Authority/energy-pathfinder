package uk.co.ogauthority.pathfinder.service.projectupdate;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.controller.projectupdate.OperatorUpdateController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.NoUpdateNotification;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.ProjectUpdate;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.projectupdate.ProjectUpdateType;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.ProvideNoUpdateForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.projectupdate.NoUpdateNotificationRepository;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectHeaderSummaryService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class OperatorProjectUpdateService {

  public static final String START_PAGE_TEMPLATE_PATH = "projectupdate/startPage";
  public static final String PROVIDE_NO_UPDATE_TEMPLATE_PATH = "projectupdate/noUpdate";
  public static final String PROVIDE_NO_UPDATE_CONFIRMATION_TEMPLATE_PATH = "projectupdate/noUpdateConfirmation";

  private final ProjectUpdateService projectUpdateService;
  private final NoUpdateNotificationRepository noUpdateNotificationRepository;
  private final ProjectNoUpdateSummaryViewService projectNoUpdateSummaryViewService;
  private final ProjectHeaderSummaryService projectHeaderSummaryService;
  private final ValidationService validationService;
  private final BreadcrumbService breadcrumbService;

  @Autowired
  public OperatorProjectUpdateService(
      ProjectUpdateService projectUpdateService,
      NoUpdateNotificationRepository noUpdateNotificationRepository,
      ProjectNoUpdateSummaryViewService projectNoUpdateSummaryViewService,
      ProjectHeaderSummaryService projectHeaderSummaryService,
      ValidationService validationService,
      BreadcrumbService breadcrumbService) {
    this.projectUpdateService = projectUpdateService;
    this.noUpdateNotificationRepository = noUpdateNotificationRepository;
    this.projectNoUpdateSummaryViewService = projectNoUpdateSummaryViewService;
    this.projectHeaderSummaryService = projectHeaderSummaryService;
    this.validationService = validationService;
    this.breadcrumbService = breadcrumbService;
  }

  public BindingResult validate(ProvideNoUpdateForm form, BindingResult bindingResult) {
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  public ProjectUpdate startUpdate(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    return projectUpdateService.startUpdate(projectDetail, user, ProjectUpdateType.OPERATOR_INITIATED);
  }

  @Transactional
  public NoUpdateNotification createNoUpdateNotification(ProjectDetail projectDetail,
                                                         AuthenticatedUserAccount user,
                                                         String reasonNoUpdateRequired) {
    var projectUpdate = projectUpdateService.startUpdate(
        projectDetail,
        projectDetail.getStatus(),
        user,
        ProjectUpdateType.OPERATOR_INITIATED
    );
    var noUpdateNotification = new NoUpdateNotification();
    noUpdateNotification.setProjectUpdate(projectUpdate);
    noUpdateNotification.setReasonNoUpdateRequired(reasonNoUpdateRequired);
    return noUpdateNotificationRepository.save(noUpdateNotification);
  }

  public ModelAndView getProjectUpdateModelAndView(Integer projectId) {
    return new ModelAndView(START_PAGE_TEMPLATE_PATH)
        .addObject("startActionUrl", ReverseRouter.route(on(OperatorUpdateController.class).startUpdate(projectId, null, null)));
  }

  public ModelAndView getProjectProvideNoUpdateModelAndView(ProjectDetail projectDetail,
                                                            AuthenticatedUserAccount user,
                                                            ProvideNoUpdateForm form) {
    var projectId = projectDetail.getProject().getId();
    var modelAndView = new ModelAndView(PROVIDE_NO_UPDATE_TEMPLATE_PATH)
        .addObject("projectHeaderHtml", projectHeaderSummaryService.getProjectHeaderHtml(projectDetail, user))
        .addObject("form", form)
        .addObject("cancelUrl", ReverseRouter.route(on(ManageProjectController.class).getProject(projectId, null, null, null)));
    breadcrumbService.fromManageProject(projectId, modelAndView, OperatorUpdateController.NO_UPDATE_REQUIRED_PAGE_NAME);
    return modelAndView;
  }

  public ModelAndView getProjectProvideNoUpdateConfirmationModelAndView(ProjectDetail projectDetail) {
    return new ModelAndView(PROVIDE_NO_UPDATE_CONFIRMATION_TEMPLATE_PATH)
        .addObject("projectNoUpdateSummaryView", projectNoUpdateSummaryViewService.getProjectNoUpdateSummaryView(projectDetail))
        .addObject("workAreaUrl", ReverseRouter.route(on(WorkAreaController.class)
            .getWorkArea(null, null)));
  }
}
