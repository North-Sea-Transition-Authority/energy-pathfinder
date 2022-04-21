package uk.co.ogauthority.pathfinder.service.project.workoplanprojectcontribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.ogauthority.pathfinder.controller.project.workplanprojectcontributor.ForwardWorkPlanProjectContributorsController;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanprojectcontribution.ForwardWorkPlanContributorDetails;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorsForm;
import uk.co.ogauthority.pathfinder.model.form.project.workoplanprojectcontribution.ForwardWorkPlanProjectContributorsForm;
import uk.co.ogauthority.pathfinder.model.form.project.workoplanprojectcontribution.ForwardWorkPlanProjectContributorsFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.workplanprojectcontributor.ForwardWorkPlanContributorDetailsRepository;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.projectcontribution.ProjectContributorsCommonService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanProjectContributorManagementServiceTest {

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Mock
  private ProjectContributorsCommonService projectContributorsCommonService;

  @Mock
  private ForwardWorkPlanContributorDetailsRepository forwardWorkPlanContributorDetailsRepository;

  @Mock
  private BreadcrumbService breadcrumbService;

  @Mock
  private ValidationService validationService;

  @Mock
  private ForwardWorkPlanProjectContributorsFormValidator forwardWorkPlanProjectContributorsFormValidator;

  private ForwardWorkPlanProjectContributorManagementService forwardWorkPlanProjectContributorManagementService;

  @Before
  public void setup() {
    forwardWorkPlanProjectContributorManagementService = new ForwardWorkPlanProjectContributorManagementService(
        projectContributorsCommonService,
        forwardWorkPlanContributorDetailsRepository,
        breadcrumbService,
        validationService,
        forwardWorkPlanProjectContributorsFormValidator
    );
  }

  @Test
  public void getProjectContributorsFormModelAndView_verifyMethodCall() {
    var form = new ForwardWorkPlanProjectContributorsForm();

    forwardWorkPlanProjectContributorManagementService.getProjectContributorsFormModelAndView(
        form,
        detail,
        List.of()
    );

    verify(projectContributorsCommonService, times(1)).setModelAndViewCommonObjects(
        any(),
        eq(detail),
        eq(form),
        eq(ForwardWorkPlanProjectContributorsController.PAGE_NAME),
        eq(List.of())
    );

    verify(breadcrumbService, times(1)).fromTaskList(
        eq(detail.getProject().getId()),
        any(),
        eq(ForwardWorkPlanProjectContributorsController.TASK_LIST_NAME)
    );
  }

  @Test
  public void getForm_verifyMethodCall() {
    var forwardWorkPlanProjectContributor = new ForwardWorkPlanContributorDetails(detail, false);

    when(forwardWorkPlanContributorDetailsRepository.findByProjectDetail(detail))
        .thenReturn(Optional.of(forwardWorkPlanProjectContributor));

    forwardWorkPlanProjectContributorManagementService.getForm(detail);

    verify(projectContributorsCommonService, times(1))
        .setContributorsInForm((ForwardWorkPlanProjectContributorsForm) any(), eq(detail));
  }

  @Test
  public void saveForwardWorkPlanProjectContributors_hasProjectContributors_verifyMethodCalls() {
    var form = new ForwardWorkPlanProjectContributorsForm();
    form.setHasProjectContributors(true);
    form.setContributors(List.of(1, 2));

    forwardWorkPlanProjectContributorManagementService.saveForwardWorkPlanProjectContributors(form, detail);

    verify(forwardWorkPlanContributorDetailsRepository, times(1)).save(
        (ForwardWorkPlanContributorDetails) any()
    );
    verify(projectContributorsCommonService, times(1)).saveProjectContributors(form, detail);

    verify(projectContributorsCommonService, never()).deleteProjectContributors(detail);
  }

  @Test
  public void saveForwardWorkPlanProjectContributors_hasNoProjectContributors_verifyMethodCalls() {
    var form = new ForwardWorkPlanProjectContributorsForm();
    form.setHasProjectContributors(false);

    forwardWorkPlanProjectContributorManagementService.saveForwardWorkPlanProjectContributors(form, detail);

    verify(forwardWorkPlanContributorDetailsRepository, times(1)).save(
        (ForwardWorkPlanContributorDetails) any()
    );
    verify(projectContributorsCommonService, never()).saveProjectContributors(form, detail);

    verify(projectContributorsCommonService, times(1)).deleteProjectContributors(detail);
  }

  @Test
  public void saveForwardWorkPlanProjectContributors_nullFields_verifyNoMethodCalls() {
    var form = new ForwardWorkPlanProjectContributorsForm();

    forwardWorkPlanProjectContributorManagementService.saveForwardWorkPlanProjectContributors(form, detail);

    verify(forwardWorkPlanContributorDetailsRepository, never()).save(
        (ForwardWorkPlanContributorDetails) any()
    );
    verify(projectContributorsCommonService, never()).saveProjectContributors(form, detail);

    verify(projectContributorsCommonService, never()).deleteProjectContributors(detail);
  }

  @Test
  public void validate_verifyMethodCall() {
    var form = new ForwardWorkPlanProjectContributorsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    forwardWorkPlanProjectContributorManagementService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );
    verify(forwardWorkPlanProjectContributorsFormValidator, times(1))
        .validate(eq(form), eq(bindingResult), any());
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void validate_partial() {
    var form = new ForwardWorkPlanProjectContributorsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    forwardWorkPlanProjectContributorManagementService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void isValid_noError_assertTrue() {
    var form = new ForwardWorkPlanProjectContributorsForm();
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    when(validationService.validate(any(), any(), (ValidationType) any())).thenReturn(bindingResult);

    assertThat(forwardWorkPlanProjectContributorManagementService.isValid(detail, ValidationType.FULL)).isTrue();
  }

  @Test
  public void isValid_error_assertFalse() {
    var form = new ProjectContributorsForm();
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(validationService.validate(any(), any(), (ValidationType) any())).thenReturn(bindingResult);

    assertThat(forwardWorkPlanProjectContributorManagementService.isValid(detail, ValidationType.FULL)).isFalse();
  }

  @Test
  public void getForwardProjectContributorForDetailOrError_exist_verifyMethodCall() {
    var forwardProjectContributor = new ForwardWorkPlanContributorDetails();
    when(forwardWorkPlanContributorDetailsRepository.findByProjectDetail(detail))
        .thenReturn(Optional.of(forwardProjectContributor));

    assertThat(forwardWorkPlanProjectContributorManagementService.getForwardProjectContributorForDetailOrError(detail))
        .isEqualTo(forwardProjectContributor);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getForwardProjectContributorForDetailOrError_doesNotExist_expectException() {
    when(forwardWorkPlanContributorDetailsRepository.findByProjectDetail(detail))
        .thenReturn(Optional.empty());

    forwardWorkPlanProjectContributorManagementService.getForwardProjectContributorForDetailOrError(detail);
  }

  @Test
  public void removeForwardProjectContributorsForDetail_verifyMethodCalls() {
    forwardWorkPlanProjectContributorManagementService.removeForwardProjectContributorsForDetail(detail);

    verify(forwardWorkPlanContributorDetailsRepository, times(1)).deleteByProjectDetail(detail);
    verify(projectContributorsCommonService, times(1)).deleteProjectContributors(detail);
  }
}