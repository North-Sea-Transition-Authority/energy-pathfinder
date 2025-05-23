package uk.co.ogauthority.pathfinder.model.form.project.projectcontributor;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectContributorsFormValidatorTest {

  private final ProjectOperator projectOperator = ProjectOperatorTestUtil.getOperator();
  private final ProjectDetail detail = projectOperator.getProjectDetail();

  @Mock
  private ProjectOperatorService projectOperatorService;

  private ProjectContributorsFormValidator projectContributorsFormValidator;

  @Before
  public void setup() {
    projectContributorsFormValidator = new ProjectContributorsFormValidator(projectOperatorService);
    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(detail)).thenReturn(projectOperator);
  }

  @Test
  public void supports_whenProjectContributorsFormClass_thenTrue() {
    final var supportedClass = ProjectContributorsForm.class;

    assertThat(projectContributorsFormValidator.supports(supportedClass)).isTrue();
  }

  @Test
  public void supports_whenNotProjectContributorsFormClass_thenFalse() {
    final var nonSupportedClass = NotSupportedFormClass.class;

    assertThat(projectContributorsFormValidator.supports(nonSupportedClass)).isFalse();
  }

  @Test(expected = ActionNotAllowedException.class)
  public void validate_whenNoProjectContributorValidationHint_thenException() {
    final var form = new ProjectContributorsForm();
    final var errors = new BeanPropertyBindingResult(form, "form");
    projectContributorsFormValidator.validate(form, errors);
  }

  @Test
  public void validate_fullValidation_noOrgs_assertErrors() {
    final var form = new ProjectContributorsForm();
    form.setContributors(List.of());

    final var validationResult = validateProjectContributorsForm(form, ValidationType.FULL);
    final var resultingErrors = ValidatorTestingUtil.extractErrors(validationResult);

    assertThat(resultingErrors).containsExactly(
        entry("contributorsSelect", Set.of("contributorsSelect.notEmpty"))
    );
  }

  @Test
  public void validate_partialValidation_noOrgs_assertNoErrors() {
    final var form = new ProjectContributorsForm();
    form.setContributors(List.of());

    final var validationResult = validateProjectContributorsForm(form, ValidationType.PARTIAL);
    final var resultingErrors = ValidatorTestingUtil.extractErrors(validationResult);

    assertThat(resultingErrors).isEmpty();
  }

  @Test
  public void validate_whenProjectOperatorAddedAsContributor_thenAssertValidationErorr() {
    final var form = new ProjectContributorsForm();
    form.setContributors(List.of(projectOperator.getOrganisationGroup().getOrgGrpId()));

    Set.of(ValidationType.FULL, ValidationType.PARTIAL)
        .forEach(validationType -> {
          var validationResult = validateProjectContributorsForm(form, ValidationType.FULL);
          var resultingErrors = ValidatorTestingUtil.extractErrors(validationResult);

          assertThat(resultingErrors).containsExactly(
              entry("contributorsSelect", Set.of("contributorsSelect.notAllowed"))
          );
        });
  }

  private BindingResult validateProjectContributorsForm(ProjectContributorsForm form, ValidationType validationType) {
    final var projectContributorValidationHint = new ProjectContributorValidationHint(validationType, detail);
    final var errors = new BeanPropertyBindingResult(form, "form");

    projectContributorsFormValidator.validate(form, errors, projectContributorValidationHint);

    return errors;
  }

  static class NotSupportedFormClass {
  }
}