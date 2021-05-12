package uk.co.ogauthority.pathfinder.service.projectassessment;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectassessment.ProjectAssessment;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.projectassessment.ProjectAssessmentForm;
import uk.co.ogauthority.pathfinder.model.form.projectassessment.ProjectAssessmentFormValidator;
import uk.co.ogauthority.pathfinder.model.form.projectassessment.ProjectAssessmentValidationHint;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.projectassessment.ProjectAssessmentRepository;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectHeaderSummaryService;
import uk.co.ogauthority.pathfinder.service.projectpublishing.ProjectPublishingService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorUpdateRequestService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectAssessmentTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectAssessmentServiceTest {

  @Mock
  private ProjectAssessmentRepository projectAssessmentRepository;

  @Mock
  private ValidationService validationService;

  @Mock
  private ProjectAssessmentFormValidator projectAssessmentFormValidator;

  @Mock
  private BreadcrumbService breadcrumbService;

  @Mock
  private RegulatorUpdateRequestService regulatorUpdateRequestService;

  @Mock
  private ProjectPublishingService projectPublishingService;

  @Mock
  private ProjectHeaderSummaryService projectHeaderSummaryService;

  private ProjectAssessmentService projectAssessmentService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectAssessmentService = new ProjectAssessmentService(
        projectAssessmentRepository,
        validationService,
        projectAssessmentFormValidator,
        breadcrumbService,
        regulatorUpdateRequestService,
        projectPublishingService,
        projectHeaderSummaryService
    );

    when(projectAssessmentRepository.save(any(ProjectAssessment.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createProjectAssessment_whenReadyToBePublished() {
    var form = ProjectAssessmentTestUtil.createProjectAssessmentForm();

    form.setReadyToBePublished(true);

    var projectAssessment = projectAssessmentService.createProjectAssessment(
        projectDetail,
        authenticatedUser,
        form
    );

    assertThat(projectAssessment.getReadyToBePublished()).isEqualTo(form.getReadyToBePublished());
    assertThat(projectAssessment.getUpdateRequired()).isEqualTo(form.getUpdateRequired());
    assertThat(projectAssessment.getAssessedInstant()).isNotNull();
    assertThat(projectAssessment.getAssessorWuaId()).isEqualTo(authenticatedUser.getWuaId());

    verify(projectPublishingService, times(1)).publishProject(projectDetail, authenticatedUser);
  }

  @Test
  public void createProjectAssessment_whenNotReadyToBePublished() {
    var form = ProjectAssessmentTestUtil.createProjectAssessmentForm();

    form.setReadyToBePublished(false);

    var projectAssessment = projectAssessmentService.createProjectAssessment(
        projectDetail,
        authenticatedUser,
        form
    );

    assertThat(projectAssessment.getReadyToBePublished()).isEqualTo(form.getReadyToBePublished());
    assertThat(projectAssessment.getUpdateRequired()).isEqualTo(form.getUpdateRequired());
    assertThat(projectAssessment.getAssessedInstant()).isNotNull();
    assertThat(projectAssessment.getAssessorWuaId()).isEqualTo(authenticatedUser.getWuaId());

    verify(projectPublishingService, never()).publishProject(projectDetail, authenticatedUser);
  }

  @Test
  public void getProjectAssessment_whenExists_thenReturn() {
    var projectAssessment = ProjectAssessmentTestUtil.createProjectAssessment();

    when(projectAssessmentRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.of(projectAssessment)
    );

    var result = projectAssessmentService.getProjectAssessment(projectDetail);

    assertThat(result).contains(projectAssessment);
  }

  @Test
  public void getProjectAssessment_whenNotFound_thenReturnEmpty() {
    when(projectAssessmentRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.empty()
    );

    var result = projectAssessmentService.getProjectAssessment(projectDetail);

    assertThat(result).isEmpty();
  }

  @Test
  public void hasProjectBeenAssessed_whenNotAssessed() {
    when(projectAssessmentRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.empty()
    );

    assertThat(projectAssessmentService.hasProjectBeenAssessed(projectDetail)).isFalse();
  }

  @Test
  public void hasProjectBeenAssessed_whenAssessed() {
    when(projectAssessmentRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.of(ProjectAssessmentTestUtil.createProjectAssessment())
    );

    assertThat(projectAssessmentService.hasProjectBeenAssessed(projectDetail)).isTrue();
  }

  @Test
  public void validate() {
    var form = ProjectAssessmentTestUtil.createProjectAssessmentForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    projectAssessmentService.validate(form, bindingResult, projectDetail);

    verify(projectAssessmentFormValidator, times(1)).validate(eq(form), eq(bindingResult), any(ProjectAssessmentValidationHint.class));
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void getProjectAssessmentModelAndView_whenCanRequestUpdate() {

    final var canRequestUpdate = true;
    var projectHeaderHtml = "html";

    when(projectHeaderSummaryService.getProjectHeaderHtml(projectDetail, authenticatedUser)).thenReturn(projectHeaderHtml);
    when(regulatorUpdateRequestService.canRequestUpdate(projectDetail)).thenReturn(canRequestUpdate);

    var form = new ProjectAssessmentForm();

    var modelAndView = projectAssessmentService.getProjectAssessmentModelAndView(projectDetail, authenticatedUser, form);

    final var pageHeading = getPageHeading(projectDetail);

    assertModelProperties(
        modelAndView,
        pageHeading,
        projectHeaderHtml,
        canRequestUpdate,
        form,
        projectDetail
    );
  }

  @Test
  public void getProjectAssessmentModelAndView_whenCanNotRequestUpdate() {

    final var canRequestUpdate = false;
    final var projectHeaderHtml = "html";

    when(projectHeaderSummaryService.getProjectHeaderHtml(projectDetail, authenticatedUser)).thenReturn(projectHeaderHtml);
    when(regulatorUpdateRequestService.canRequestUpdate(projectDetail)).thenReturn(canRequestUpdate);

    var form = new ProjectAssessmentForm();

    var modelAndView = projectAssessmentService.getProjectAssessmentModelAndView(projectDetail, authenticatedUser, form);

    final var pageHeading = getPageHeading(projectDetail);

    assertModelProperties(
        modelAndView,
        pageHeading,
        projectHeaderHtml,
        canRequestUpdate,
        form,
        projectDetail
    );
  }

  private String getPageHeading(ProjectDetail projectDetail) {
    return String.format("%s assessment", projectDetail.getProjectType().getDisplayName());
  }

  private void assertModelProperties(ModelAndView modelAndView,
                                     String pageHeading,
                                     String projectHeaderHtml,
                                     boolean canRequestUpdate,
                                     ProjectAssessmentForm form,
                                     ProjectDetail projectDetail) {
    assertThat(modelAndView.getModel()).containsExactly(
        entry("pageName", pageHeading),
        entry("projectHeaderHtml", projectHeaderHtml),
        entry("canRequestUpdate", canRequestUpdate),
        entry("form", form),
        entry("cancelUrl", ReverseRouter.route(on(ManageProjectController.class).getProject(
            projectDetail.getProject().getId(),
            null,
            null,
            null
        ))),
        entry(ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR, projectDetail.getProjectType().getDisplayName()),
        entry(ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR, projectDetail.getProjectType().getLowercaseDisplayName())
    );
    verify(breadcrumbService, times(1)).fromManageProject(
        projectDetail,
        modelAndView,
        pageHeading
    );
  }
}
