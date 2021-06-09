package uk.co.ogauthority.pathfinder.service.projectupdate;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.controller.projectupdate.RegulatorUpdateController;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.RequestUpdateForm;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.RequestUpdateFormValidator;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.projectupdate.RegulatorUpdateRequestRepository;
import uk.co.ogauthority.pathfinder.service.email.OperatorEmailService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectHeaderSummaryService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class RegulatorUpdateRequestServiceTest {

  @Mock
  private RegulatorUpdateRequestRepository regulatorUpdateRequestRepository;

  @Mock
  private ProjectUpdateService projectUpdateService;

  @Mock
  private ProjectHeaderSummaryService projectHeaderSummaryService;

  @Mock
  private RequestUpdateFormValidator requestUpdateFormValidator;

  @Mock
  private OperatorEmailService operatorEmailService;

  @Mock
  private ValidationService validationService;

  @Mock
  private BreadcrumbService breadcrumbService;

  private RegulatorUpdateRequestService regulatorUpdateRequestService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final Project project = projectDetail.getProject();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    regulatorUpdateRequestService = new RegulatorUpdateRequestService(
        regulatorUpdateRequestRepository,
        projectUpdateService,
        projectHeaderSummaryService,
        requestUpdateFormValidator,
        operatorEmailService,
        validationService,
        breadcrumbService
    );

    when(regulatorUpdateRequestRepository.save(any(RegulatorUpdateRequest.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void validate() {
    var form = new RequestUpdateForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    regulatorUpdateRequestService.validate(form, bindingResult);

    verify(requestUpdateFormValidator, times(1)).validate(eq(form), eq(bindingResult), any());
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void requestUpdate() {
    var form = new RequestUpdateForm();
    form.setUpdateReason("Test update reason");
    form.setDeadlineDate(new ThreeFieldDateInput(LocalDate.now().plusMonths(1)));

    var regulatorRequestedUpdate = regulatorUpdateRequestService.requestUpdate(projectDetail, form, authenticatedUser);

    assertThat(regulatorRequestedUpdate.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(regulatorRequestedUpdate.getUpdateReason()).isEqualTo(form.getUpdateReason());
    assertThat(regulatorRequestedUpdate.getDeadlineDate()).isEqualTo(form.getDeadlineDate().createDateOrNull());
    assertThat(regulatorRequestedUpdate.getRequestedByWuaId()).isEqualTo(authenticatedUser.getWuaId());
    assertThat(regulatorRequestedUpdate.getRequestedInstant()).isNotNull();

    verify(regulatorUpdateRequestRepository, times(1)).save(regulatorRequestedUpdate);
    verify(operatorEmailService, times(1)).sendUpdateRequestedEmail(projectDetail, form.getUpdateReason(), form.getDeadlineDate().createDateOrNull());
  }

  @Test
  public void hasUpdateBeenRequested_whenExists_thenTrue() {
    when(regulatorUpdateRequestRepository.existsByProjectDetail(projectDetail)).thenReturn(true);

    assertThat(regulatorUpdateRequestService.hasUpdateBeenRequested(projectDetail)).isTrue();
  }

  @Test
  public void hasUpdateBeenRequested_whenNotExists_thenFalse() {
    when(regulatorUpdateRequestRepository.existsByProjectDetail(projectDetail)).thenReturn(false);

    assertThat(regulatorUpdateRequestService.hasUpdateBeenRequested(projectDetail)).isFalse();
  }

  @Test
  public void getUpdateRequest_whenFound_thenReturn() {
    var regulatorUpdateRequest = new RegulatorUpdateRequest();

    when(regulatorUpdateRequestRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.of(regulatorUpdateRequest));

    assertThat(regulatorUpdateRequestService.getUpdateRequest(projectDetail)).contains(regulatorUpdateRequest);
  }

  @Test
  public void getUpdateRequest_whenNotFound_thenEmptyOptionalReturned() {
    when(regulatorUpdateRequestRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.empty());

    assertThat(regulatorUpdateRequestService.getUpdateRequest(projectDetail)).isEmpty();
  }

  @Test
  public void canRequestUpdate_whenUpdateInProgress_thenFalse() {
    when(projectUpdateService.isUpdateInProgress(project)).thenReturn(true);

    assertThat(regulatorUpdateRequestService.canRequestUpdate(projectDetail)).isFalse();
  }

  @Test
  public void canRequestUpdate_whenUpdateRequested_thenFalse() {
    when(regulatorUpdateRequestRepository.existsByProjectDetail(projectDetail)).thenReturn(true);

    assertThat(regulatorUpdateRequestService.canRequestUpdate(projectDetail)).isFalse();
  }

  @Test
  public void canRequestUpdate_whenUpdateNotInProgressAndUpdateNotRequested_thenTrue() {
    when(projectUpdateService.isUpdateInProgress(project)).thenReturn(false);
    when(regulatorUpdateRequestRepository.existsByProjectDetail(projectDetail)).thenReturn(false);

    assertThat(regulatorUpdateRequestService.canRequestUpdate(projectDetail)).isTrue();
  }

  @Test
  public void getUpdateRequestReason_whenFound_thenReturn() {
    var updateReason = "test";
    var regulatorUpdateRequest = new RegulatorUpdateRequest();
    regulatorUpdateRequest.setUpdateReason(updateReason);

    when(regulatorUpdateRequestRepository.findByProjectDetail_projectAndProjectDetail_Version(
        project,
        projectDetail.getVersion()))
        .thenReturn(Optional.of(regulatorUpdateRequest));

    assertThat(regulatorUpdateRequestService.getUpdateRequestReason(
        project,
        projectDetail.getVersion()))
        .isEqualTo(updateReason);  }

  @Test
  public void getUpdateRequestReason_whenNotFound_thenEmptyStringReturned() {
    when(regulatorUpdateRequestRepository.findByProjectDetail_projectAndProjectDetail_Version(
        projectDetail.getProject(),
        projectDetail.getVersion()))
        .thenReturn(Optional.empty());

    assertThat(regulatorUpdateRequestService.getUpdateRequestReason(
        projectDetail.getProject(),
        projectDetail.getVersion()))
        .isEmpty();
  }

  @Test
  public void getRequestUpdateModelAndView() {
    var form = new RequestUpdateForm();
    var projectHeaderHtml = "html";

    when(projectHeaderSummaryService.getProjectHeaderHtml(projectDetail, authenticatedUser)).thenReturn(projectHeaderHtml);

    var modelAndView = regulatorUpdateRequestService.getRequestUpdateModelAndView(
        projectDetail,
        authenticatedUser,
        form
    );

    assertThat(modelAndView.getViewName()).isEqualTo(RegulatorUpdateRequestService.REQUEST_UPDATE_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("projectHeaderHtml", projectHeaderHtml),
        entry("form", form),
        entry("startActionUrl", ReverseRouter.route(on(RegulatorUpdateController.class)
            .requestUpdate(project.getId(), null, null, null, null))),
        entry("cancelUrl", ReverseRouter.route(on(ManageProjectController.class).getProject(project.getId(), null, null, null)))
    );

    verify(breadcrumbService, times(1)).fromManageProject(
        projectDetail,
        modelAndView,
        RegulatorUpdateController.REQUEST_UPDATE_PAGE_NAME
    );
  }
}
