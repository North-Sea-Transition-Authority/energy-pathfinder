package uk.co.ogauthority.pathfinder.service.project.projectassessment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.ManageProjectController;
import uk.co.ogauthority.pathfinder.controller.project.projectassessment.ProjectAssessmentController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectassessment.ProjectAssessment;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.projectassessment.ProjectQuality;
import uk.co.ogauthority.pathfinder.model.form.project.projectassessment.ProjectAssessmentForm;
import uk.co.ogauthority.pathfinder.model.form.project.projectassessment.ProjectAssessmentFormValidator;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.project.projectassessment.ProjectAssessmentRepository;
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

  private ProjectAssessmentService projectAssessmentService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectAssessmentService = new ProjectAssessmentService(
        projectAssessmentRepository,
        validationService,
        projectAssessmentFormValidator
    );

    when(projectAssessmentRepository.save(any(ProjectAssessment.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createProjectAssessment() {
    var form = ProjectAssessmentTestUtil.createProjectAssessmentForm();

    var projectAssessment = projectAssessmentService.createProjectAssessment(
        projectDetail,
        authenticatedUser,
        form
    );

    assertThat(projectAssessment.getProjectQuality()).isEqualTo(form.getProjectQuality());
    assertThat(projectAssessment.getReadyToBePublished()).isEqualTo(form.getReadyToBePublished());
    assertThat(projectAssessment.getUpdateRequired()).isEqualTo(form.getUpdateRequired());
    assertThat(projectAssessment.getAssessedInstant()).isNotNull();
    assertThat(projectAssessment.getAssessorWuaId()).isEqualTo(authenticatedUser.getWuaId());
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
  public void validate() {
    var form = ProjectAssessmentTestUtil.createProjectAssessmentForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    projectAssessmentService.validate(form, bindingResult);

    verify(projectAssessmentFormValidator, times(1)).validate(form, bindingResult);
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void getProjectAssessmentModelAndView() {
    var projectId = 1;
    var form = new ProjectAssessmentForm();

    var modelAndView = projectAssessmentService.getProjectAssessmentModelAndView(projectId, form);

    var model = modelAndView.getModel();
    assertThat(model).containsEntry("pageName", ProjectAssessmentController.PAGE_NAME);
    assertThat(model).containsEntry("form", form);
    assertThat(model).containsEntry("projectQualities", ProjectQuality.getAllAsMap());
    assertThat(model).containsEntry("cancelUrl", ReverseRouter.route(on(ManageProjectController.class)
        .getProject(projectId, null, null)));
  }
}
