package uk.co.ogauthority.pathfinder.service.projectupdate;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.controller.projectupdate.RegulatorUpdateController;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.RequestUpdateForm;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.RequestUpdateFormValidator;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.RequestUpdateValidationHint;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.projectupdate.RegulatorUpdateRequestRepository;
import uk.co.ogauthority.pathfinder.service.email.OperatorEmailService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectHeaderSummaryService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class RegulatorUpdateRequestService {

  public static final String REQUEST_UPDATE_TEMPLATE_PATH = "projectupdate/requestUpdate";

  private final RegulatorUpdateRequestRepository regulatorUpdateRequestRepository;
  private final ProjectUpdateService projectUpdateService;
  private final ProjectHeaderSummaryService projectHeaderSummaryService;
  private final RequestUpdateFormValidator requestUpdateFormValidator;
  private final OperatorEmailService operatorEmailService;
  private final ValidationService validationService;
  private final BreadcrumbService breadcrumbService;

  @Autowired
  public RegulatorUpdateRequestService(
      RegulatorUpdateRequestRepository regulatorUpdateRequestRepository,
      ProjectUpdateService projectUpdateService,
      ProjectHeaderSummaryService projectHeaderSummaryService,
      RequestUpdateFormValidator requestUpdateFormValidator,
      OperatorEmailService operatorEmailService,
      ValidationService validationService,
      BreadcrumbService breadcrumbService) {
    this.regulatorUpdateRequestRepository = regulatorUpdateRequestRepository;
    this.projectUpdateService = projectUpdateService;
    this.projectHeaderSummaryService = projectHeaderSummaryService;
    this.requestUpdateFormValidator = requestUpdateFormValidator;
    this.operatorEmailService = operatorEmailService;
    this.validationService = validationService;
    this.breadcrumbService = breadcrumbService;
  }

  public BindingResult validate(RequestUpdateForm form, BindingResult bindingResult) {
    requestUpdateFormValidator.validate(form, bindingResult, new RequestUpdateValidationHint());
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  @Transactional
  public RegulatorUpdateRequest requestUpdate(ProjectDetail projectDetail,
                                              RequestUpdateForm form,
                                              AuthenticatedUserAccount requestedByUser) {

    var regulatorUpdateRequest = new RegulatorUpdateRequest();
    regulatorUpdateRequest.setProjectDetail(projectDetail);
    regulatorUpdateRequest.setUpdateReason(form.getUpdateReason());
    regulatorUpdateRequest.setDeadlineDate(form.getDeadlineDate().createDateOrNull());
    regulatorUpdateRequest.setRequestedByWuaId(requestedByUser.getWuaId());
    regulatorUpdateRequest.setRequestedInstant(Instant.now());
    regulatorUpdateRequest = regulatorUpdateRequestRepository.save(regulatorUpdateRequest);

    operatorEmailService.sendUpdateRequestedEmail(
        projectDetail,
        form.getUpdateReason(),
        form.getDeadlineDate().createDateOrNull()
    );

    return regulatorUpdateRequest;
  }

  public boolean hasUpdateBeenRequested(ProjectDetail projectDetail) {
    return regulatorUpdateRequestRepository.existsByProjectDetail(projectDetail);
  }

  public Optional<RegulatorUpdateRequest> getUpdateRequest(ProjectDetail projectDetail) {
    return regulatorUpdateRequestRepository.findByProjectDetail(projectDetail);
  }

  public boolean canRequestUpdate(ProjectDetail projectDetail) {
    return !projectUpdateService.isUpdateInProgress(projectDetail.getProject())
        && !hasUpdateBeenRequested(projectDetail);
  }

  /** Returns the update request reason if one exists for the provided project version
   * and an empty string if it does not.
   * @param project we are finding the request reason from.
   * @param version of the project.
   * @return update request reason or an empty string.
   */
  public String getUpdateRequestReason(Project project, Integer version) {
    return regulatorUpdateRequestRepository.findByProjectDetail_projectAndProjectDetail_Version(project, version)
        .map(RegulatorUpdateRequest::getUpdateReason)
        .orElse("");
  }

  public ModelAndView getRequestUpdateModelAndView(ProjectDetail projectDetail,AuthenticatedUserAccount user, RequestUpdateForm form) {
    var projectId = projectDetail.getProject().getId();
    var modelAndView = new ModelAndView(REQUEST_UPDATE_TEMPLATE_PATH)
        .addObject("projectHeaderHtml", projectHeaderSummaryService.getProjectHeaderHtml(projectDetail, user))
        .addObject("form", form)
        .addObject("startActionUrl", ReverseRouter.route(on(RegulatorUpdateController.class)
            .requestUpdate(projectId, null, null, null, null)))
        .addObject("cancelUrl", ReverseRouter.route(on(ManageProjectController.class).getProject(projectId, null, null, null)));

    breadcrumbService.fromManageProject(
        projectDetail,
        modelAndView,
        RegulatorUpdateController.REQUEST_UPDATE_PAGE_NAME
    );

    return modelAndView;
  }
}
