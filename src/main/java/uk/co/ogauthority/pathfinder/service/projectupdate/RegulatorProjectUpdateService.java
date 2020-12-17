package uk.co.ogauthority.pathfinder.service.projectupdate;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.controller.projectupdate.RegulatorUpdateController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorRequestedUpdate;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.projectupdate.ProjectUpdateType;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.RequestUpdateForm;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.RequestUpdateFormValidator;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.RequestUpdateValidationHint;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.projectupdate.RegulatorRequestedUpdateRepository;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectHeaderSummaryService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class RegulatorProjectUpdateService {

  public static final String REQUEST_UPDATE_TEMPLATE_PATH = "projectupdate/requestUpdate";

  private final RegulatorRequestedUpdateRepository regulatorRequestedUpdateRepository;
  private final ProjectUpdateService projectUpdateService;
  private final ProjectHeaderSummaryService projectHeaderSummaryService;
  private final RequestUpdateFormValidator requestUpdateFormValidator;
  private final ValidationService validationService;
  private final BreadcrumbService breadcrumbService;

  @Autowired
  public RegulatorProjectUpdateService(
      RegulatorRequestedUpdateRepository regulatorRequestedUpdateRepository,
      ProjectUpdateService projectUpdateService,
      ProjectHeaderSummaryService projectHeaderSummaryService,
      RequestUpdateFormValidator requestUpdateFormValidator,
      ValidationService validationService,
      BreadcrumbService breadcrumbService) {
    this.regulatorRequestedUpdateRepository = regulatorRequestedUpdateRepository;
    this.projectUpdateService = projectUpdateService;
    this.projectHeaderSummaryService = projectHeaderSummaryService;
    this.requestUpdateFormValidator = requestUpdateFormValidator;
    this.validationService = validationService;
    this.breadcrumbService = breadcrumbService;
  }

  public BindingResult validate(RequestUpdateForm form, BindingResult bindingResult) {
    requestUpdateFormValidator.validate(form, bindingResult, new RequestUpdateValidationHint());
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  @Transactional
  public RegulatorRequestedUpdate startRegulatorRequestedUpdate(ProjectDetail projectDetail,
                                                                RequestUpdateForm form,
                                                                AuthenticatedUserAccount requestedByUser) {
    var projectUpdate = projectUpdateService.startUpdate(projectDetail, requestedByUser, ProjectUpdateType.REGULATOR_REQUESTED);
    var regulatorRequestedUpdate = new RegulatorRequestedUpdate();
    regulatorRequestedUpdate.setProjectUpdate(projectUpdate);
    regulatorRequestedUpdate.setUpdateReason(form.getUpdateReason());
    regulatorRequestedUpdate.setDeadlineDate(form.getDeadlineDate().createDateOrNull());
    regulatorRequestedUpdate.setRequestedByWuaId(requestedByUser.getWuaId());
    regulatorRequestedUpdate.setRequestedInstant(Instant.now());
    return regulatorRequestedUpdateRepository.save(regulatorRequestedUpdate);
  }

  public ModelAndView getRequestUpdateModelAndView(ProjectDetail projectDetail, AuthenticatedUserAccount user, RequestUpdateForm form) {
    var projectId = projectDetail.getProject().getId();
    var modelAndView = new ModelAndView(REQUEST_UPDATE_TEMPLATE_PATH)
        .addObject("projectHeaderHtml", projectHeaderSummaryService.getProjectHeaderHtml(projectDetail, user))
        .addObject("form", form)
        .addObject("startActionUrl", ReverseRouter.route(on(RegulatorUpdateController.class)
            .requestUpdate(projectId, null, null, null, null)))
        .addObject("cancelUrl", ReverseRouter.route(on(ManageProjectController.class).getProject(projectId, null, null, null)));
    breadcrumbService.fromManageProject(projectId, modelAndView, RegulatorUpdateController.REQUEST_UPDATE_PAGE_NAME);
    return modelAndView;
  }
}
