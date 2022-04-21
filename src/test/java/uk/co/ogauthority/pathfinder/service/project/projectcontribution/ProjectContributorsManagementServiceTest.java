package uk.co.ogauthority.pathfinder.service.project.projectcontribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.ogauthority.pathfinder.controller.project.projectcontributor.ProjectContributorsController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorsForm;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorsFormValidator;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectContributorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectContributorsManagementServiceTest {

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Mock
  private BreadcrumbService breadcrumbService;

  @Mock
  private ValidationService validationService;

  @Mock
  private ProjectContributorsCommonService projectContributorsCommonService;

  @Mock
  private ProjectContributorsFormValidator projectContributorsFormValidator;

  private ProjectContributorsManagementService projectContributorsManagementService;

  @Before
  public void setup() {
    projectContributorsManagementService = new ProjectContributorsManagementService(
        breadcrumbService,
        validationService,
        projectContributorsCommonService,
        projectContributorsFormValidator);
  }

  @Test
  public void getProjectContributorsFormModelAndView_verifyMethodCalls() {
    var form = new ProjectContributorsForm();

    projectContributorsManagementService.getProjectContributorsFormModelAndView(form, detail, List.of());

    verify(projectContributorsCommonService, times(1)).setModelAndViewCommonObjects(
        any(),
        eq(detail),
        eq(form),
        eq(ProjectContributorsController.FORM_PAGE_NAME),
        eq(List.of())
    );

    verify(breadcrumbService, times(1)).fromTaskList(
        eq(detail.getProject().getId()),
        any(),
        eq(ProjectContributorsController.TASK_LIST_NAME)
    );
  }

  @Test
  public void saveProjectContributors_verifyMethodCalls() {
    var form = new ProjectContributorsForm();
    form.setContributors(List.of(1, 2, 3));

    projectContributorsManagementService.saveProjectContributors(form, detail);

    verify(projectContributorsCommonService, times(1)).saveProjectContributors(form, detail);
  }

  @Test
  public void validate_full() {
    var form = new ProjectContributorsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    projectContributorsManagementService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );
    verify(projectContributorsFormValidator, times(1))
        .validate(eq(form), eq(bindingResult), any());
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void validate_partial() {
    var form = new ProjectContributorsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    projectContributorsManagementService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void isValid_noError_assertTrue() {
    var form = new ProjectContributorsForm();
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    when(validationService.validate(any(), any(), (ValidationType) any())).thenReturn(bindingResult);

    assertThat(projectContributorsManagementService.isValid(detail, ValidationType.FULL)).isTrue();
  }

  @Test
  public void isValid_error_assertFalse() {
    var form = new ProjectContributorsForm();
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(validationService.validate(any(), any(), (ValidationType) any())).thenReturn(bindingResult);

    assertThat(projectContributorsManagementService.isValid(detail, ValidationType.FULL)).isFalse();
  }

  @Test
  public void getForm_verifyMethodCall() {
    projectContributorsManagementService.getForm(detail);

    verify(projectContributorsCommonService, times(1))
        .setContributorsInForm((ProjectContributorsForm) any(), eq(detail));
  }


  @Test
  public void removeProjectContributorsForDetail_removeSavedContributors() {
    projectContributorsManagementService.removeProjectContributorsForDetail(detail);

    verify(projectContributorsCommonService, times(1)).deleteProjectContributors(detail);
  }
}