package uk.co.ogauthority.pathfinder.service.projectupdate;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.controller.projectupdate.OperatorUpdateController;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.NoUpdateNotification;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.ProjectUpdate;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.projectupdate.ProjectUpdateType;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.ProvideNoUpdateForm;
import uk.co.ogauthority.pathfinder.model.view.submission.ProjectNoUpdateSubmissionSummaryView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.projectupdate.NoUpdateNotificationRepository;
import uk.co.ogauthority.pathfinder.service.email.RegulatorEmailService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.service.project.submission.ProjectSubmissionSummaryViewService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectHeaderSummaryService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@RunWith(MockitoJUnitRunner.class)
public class OperatorProjectUpdateServiceTest {

  private static final int PROJECT_ID = 1;

  @Mock
  private ProjectUpdateService projectUpdateService;

  @Mock
  private RegulatorUpdateRequestService regulatorUpdateRequestService;

  @Mock
  private NoUpdateNotificationRepository noUpdateNotificationRepository;

  @Mock
  private ProjectSubmissionSummaryViewService projectSubmissionSummaryViewService;

  @Mock
  private ProjectHeaderSummaryService projectHeaderSummaryService;

  @Mock
  private ValidationService validationService;

  @Mock
  private BreadcrumbService breadcrumbService;

  @Mock
  private RegulatorEmailService regulatorEmailService;

  private OperatorProjectUpdateService operatorProjectUpdateService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    operatorProjectUpdateService = new OperatorProjectUpdateService(
        projectUpdateService,
        regulatorUpdateRequestService,
        noUpdateNotificationRepository,
        projectSubmissionSummaryViewService,
        projectHeaderSummaryService,
        regulatorEmailService,
        validationService,
        breadcrumbService
    );

    when(noUpdateNotificationRepository.save(any(NoUpdateNotification.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void validate() {
    var form = new ProvideNoUpdateForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    operatorProjectUpdateService.validate(form, bindingResult);

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void startUpdate_whenUpdateRequested() {
    when(regulatorUpdateRequestService.hasUpdateBeenRequested(projectDetail)).thenReturn(true);

    operatorProjectUpdateService.startUpdate(projectDetail, authenticatedUser);

    verify(projectUpdateService, times(1)).startUpdate(projectDetail, authenticatedUser, ProjectUpdateType.REGULATOR_REQUESTED);
  }

  @Test
  public void startUpdate_whenNoUpdateRequested() {
    when(regulatorUpdateRequestService.hasUpdateBeenRequested(projectDetail)).thenReturn(false);

    operatorProjectUpdateService.startUpdate(projectDetail, authenticatedUser);

    verify(projectUpdateService, times(1)).startUpdate(projectDetail, authenticatedUser, ProjectUpdateType.OPERATOR_INITIATED);
  }

  @Test
  public void createNoUpdateNotification() {
    var projectUpdate = new ProjectUpdate();

    when(projectUpdateService.startUpdate(projectDetail, projectDetail.getStatus(), authenticatedUser, ProjectUpdateType.OPERATOR_INITIATED)).thenReturn(
        projectUpdate
    );

    var form = new ProvideNoUpdateForm();
    form.setSupplyChainReason("Test supply chain reason");
    form.setRegulatorReason("Test regulator reason");

    var noUpdateNotification = operatorProjectUpdateService.createNoUpdateNotification(
        projectDetail,
        authenticatedUser,
        form
    );

    verify(projectUpdateService, times(1)).startUpdate(projectDetail, projectDetail.getStatus(), authenticatedUser, ProjectUpdateType.OPERATOR_INITIATED);

    assertThat(noUpdateNotification.getProjectUpdate()).isEqualTo(projectUpdate);
    assertThat(noUpdateNotification.getSupplyChainReason()).isEqualTo(form.getSupplyChainReason());
    assertThat(noUpdateNotification.getRegulatorReason()).isEqualTo(form.getRegulatorReason());

    verify(noUpdateNotificationRepository, times(1)).save(noUpdateNotification);
    verify(regulatorEmailService, times(1)).sendNoUpdateNotificationEmail(projectDetail, noUpdateNotification.getRegulatorReason());
  }

  @Test
  public void createNoUpdateNotification_emailSent() {
    var projectUpdate = new ProjectUpdate();

    when(projectUpdateService.startUpdate(projectDetail, projectDetail.getStatus(), authenticatedUser, ProjectUpdateType.OPERATOR_INITIATED)).thenReturn(
        projectUpdate
    );

    var form = new ProvideNoUpdateForm();
    form.setSupplyChainReason("Test supply chain reason");
    form.setRegulatorReason("Test regulator reason");

    var noUpdateNotification = operatorProjectUpdateService.createNoUpdateNotification(
        projectDetail,
        authenticatedUser,
        form
    );

    verify(regulatorEmailService, times(1)).sendNoUpdateNotificationEmail(projectDetail, noUpdateNotification.getRegulatorReason());
  }

  @Test
  public void createNoUpdateNotification_emailNoRegulatorReason() {
    var projectUpdate = new ProjectUpdate();

    when(projectUpdateService.startUpdate(projectDetail, projectDetail.getStatus(), authenticatedUser, ProjectUpdateType.OPERATOR_INITIATED)).thenReturn(
        projectUpdate
    );

    var form = new ProvideNoUpdateForm();
    form.setSupplyChainReason("Test supply chain reason");
    form.setRegulatorReason(null);

    var noUpdateNotification = operatorProjectUpdateService.createNoUpdateNotification(
        projectDetail,
        authenticatedUser,
        form
    );

    verify(regulatorEmailService, times(1)).sendNoUpdateNotificationEmail(projectDetail, noUpdateNotification.getSupplyChainReason());
  }

  @Test
  public void getNoUpdateNotificationByUpdateToDetail_whenFound_thenReturn() {
    var noUpdateNotification = new NoUpdateNotification();

    when(noUpdateNotificationRepository.findByProjectUpdate_ToDetail(projectDetail)).thenReturn(Optional.of(noUpdateNotification));

    assertThat(operatorProjectUpdateService.getNoUpdateNotificationByUpdateToDetail(projectDetail)).contains(noUpdateNotification);
  }

  @Test
  public void getNoUpdateNotificationByUpdateToDetail_whenFound_thenEmptyOptionalReturned() {
    when(noUpdateNotificationRepository.findByProjectUpdate_ToDetail(projectDetail)).thenReturn(Optional.empty());

    assertThat(operatorProjectUpdateService.getNoUpdateNotificationByUpdateToDetail(projectDetail)).isEmpty();
  }

  @Test
  public void getProjectUpdateModelAndView() {
    var modelAndView = operatorProjectUpdateService.getProjectUpdateModelAndView(projectDetail);

    assertThat(modelAndView.getViewName()).isEqualTo(OperatorProjectUpdateService.START_PAGE_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("startActionUrl", ReverseRouter.route(on(OperatorUpdateController.class)
            .startUpdate(projectDetail.getProject().getId(), null, null))
        ),
        entry(
            ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR,
            projectDetail.getProjectType().getDisplayName()
        ),
        entry(
            ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR,
            projectDetail.getProjectType().getLowercaseDisplayName()
        )
    );
  }

  @Test
  public void getProjectProvideNoUpdateModelAndView() {
    var form = new ProvideNoUpdateForm();
    var projectHeaderHtml = "html";

    when(projectHeaderSummaryService.getProjectHeaderHtml(projectDetail, authenticatedUser)).thenReturn(projectHeaderHtml);

    var modelAndView = operatorProjectUpdateService.getProjectProvideNoUpdateModelAndView(
        projectDetail,
        authenticatedUser,
        form
    );

    assertThat(modelAndView.getViewName()).isEqualTo(OperatorProjectUpdateService.PROVIDE_NO_UPDATE_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("projectHeaderHtml", projectHeaderHtml),
        entry("form", form),
        entry("cancelUrl", ReverseRouter.route(on(ManageProjectController.class).getProject(PROJECT_ID, null, null, null)))
    );

    verify(breadcrumbService, times(1)).fromManageProject(
        projectDetail,
        modelAndView,
        OperatorUpdateController.NO_UPDATE_REQUIRED_PAGE_NAME
    );
  }

  @Test
  public void getProjectProvideNoUpdateConfirmationModelAndView() {
    var projectNoUpdateSummaryView = new ProjectNoUpdateSubmissionSummaryView("test", "time", "user");

    when(projectSubmissionSummaryViewService.getProjectNoUpdateSubmissionSummaryView(projectDetail)).thenReturn(projectNoUpdateSummaryView);

    var modelAndView = operatorProjectUpdateService.getProjectProvideNoUpdateConfirmationModelAndView(projectDetail);

    assertThat(modelAndView.getViewName()).isEqualTo(OperatorProjectUpdateService.PROVIDE_NO_UPDATE_CONFIRMATION_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("projectNoUpdateSummaryView", projectNoUpdateSummaryView),
        entry("workAreaUrl", ControllerUtils.getWorkAreaUrl()),
        entry("feedbackUrl", ControllerUtils.getFeedbackUrl(projectDetail.getId())),
        entry(
            ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR,
            projectDetail.getProjectType().getDisplayName()
        ),
        entry(
            ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR,
            projectDetail.getProjectType().getLowercaseDisplayName()
        )
    );
  }

  @Test
  public void confirmNoUpdateExistsForProjectDetail_whenExists() {
    var update = new ProjectUpdate();
    when(projectUpdateService.getByToDetail(projectDetail)).thenReturn(Optional.of(update));
    when(noUpdateNotificationRepository.existsByProjectUpdate(update)).thenReturn(true);
    Assertions.assertDoesNotThrow(() -> operatorProjectUpdateService.confirmNoUpdateExistsForProjectDetail(projectDetail));
  }

  @Test(expected = AccessDeniedException.class)
  public void confirmNoUpdateExistsForProjectDetail_noUpdateExists() {
    when(projectUpdateService.getByToDetail(projectDetail)).thenReturn(Optional.empty());
    operatorProjectUpdateService.confirmNoUpdateExistsForProjectDetail(projectDetail);
  }

  @Test(expected = AccessDeniedException.class)
  public void confirmNoUpdateExistsForProjectDetail_noNoUpdateNotificationExists() {
    var update = new ProjectUpdate();
    when(projectUpdateService.getByToDetail(projectDetail)).thenReturn(Optional.of(update));
    when(noUpdateNotificationRepository.existsByProjectUpdate(update)).thenReturn(false);
    operatorProjectUpdateService.confirmNoUpdateExistsForProjectDetail(projectDetail);
  }
}
