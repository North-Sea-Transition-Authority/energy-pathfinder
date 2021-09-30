package uk.co.ogauthority.pathfinder.model.form.project.selectoperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectOperatorFormValidatorTest {

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  private final String publishableOrganisationFormFieldName = ProjectOperatorFormValidator.PUBLISHABLE_ORGANISATION_FORM_FIELD_NAME;

  private ProjectOperatorFormValidator projectOperatorFormValidator;

  @Before
  public void setup() {
    projectOperatorFormValidator = new ProjectOperatorFormValidator(portalOrganisationAccessor);
  }

  @Test
  public void supports_whenProjectOperatorForm_thenTrue() {
    final var isSupported = projectOperatorFormValidator.supports(ProjectOperatorForm.class);
    assertThat(isSupported).isTrue();
  }

  @Test
  public void supports_whenUnsupportedForm_thenFalse() {
    final var isSupported = projectOperatorFormValidator.supports(UnsupportedForm.class);
    assertThat(isSupported).isFalse();
  }

  @Test
  public void validate_whenIsPublishedAsOperatorFalseAndValidPublishableOrganisation_thenEmptyBindingResult() {

    final var validOrganisationGroupId = 100;
    final var validOrganisationUnitId = 200;

    final var projectOperatorForm = new ProjectOperatorForm();
    projectOperatorForm.setIsPublishedAsOperator(false);
    projectOperatorForm.setOperator(String.valueOf(validOrganisationGroupId));
    projectOperatorForm.setPublishableOrganisation(String.valueOf(validOrganisationUnitId));

    when(portalOrganisationAccessor.isOrganisationUnitActiveAndPartOfOrganisationGroup(
        validOrganisationGroupId,
        validOrganisationUnitId
    )).thenReturn(true);

    final var errors = getErrors(projectOperatorForm);

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  public void validate_whenIsPublishedAsOperatorFalseAndNoPublishableOrganisation_thenPopulatedBindingResult() {

    final var projectOperatorForm = new ProjectOperatorForm();
    projectOperatorForm.setIsPublishedAsOperator(false);
    projectOperatorForm.setPublishableOrganisation(null);

    final var errors = getErrors(projectOperatorForm);

    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    final var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry(
            publishableOrganisationFormFieldName,
            Set.of(String.format("%s%s", publishableOrganisationFormFieldName, FieldValidationErrorCodes.INVALID))
        )
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            publishableOrganisationFormFieldName,
            Set.of(ProjectOperatorFormValidator.MISSING_PUBLISHABLE_ORGANISATION_ERROR_MESSAGE)
        )
    );
  }

  @Test
  public void validate_whenIsPublishedAsOperatorFalseAndPublishableOrganisationNotInGroup_thenPopulatedBindingResult() {

    final var validOrganisationGroupId = 100;
    final var organisationUnitIdNotInGroup = 200;

    final var projectOperatorForm = new ProjectOperatorForm();
    projectOperatorForm.setIsPublishedAsOperator(false);
    projectOperatorForm.setOperator(String.valueOf(validOrganisationGroupId));
    projectOperatorForm.setPublishableOrganisation(String.valueOf(organisationUnitIdNotInGroup));

    when(portalOrganisationAccessor.isOrganisationUnitActiveAndPartOfOrganisationGroup(
        validOrganisationGroupId,
        organisationUnitIdNotInGroup
    )).thenReturn(false);

    final var errors = getErrors(projectOperatorForm);

    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    final var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry(
            publishableOrganisationFormFieldName,
            Set.of(String.format(
                "%s%s",
                publishableOrganisationFormFieldName,
                ProjectOperatorFormValidator.ORGANISATION_UNIT_NOT_IN_GROUP_ERROR_CODE
            ))
        )
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            publishableOrganisationFormFieldName,
            Set.of(ProjectOperatorFormValidator.ORGANISATION_UNIT_NOT_IN_GROUP_ERROR_MESSAGE)
        )
    );
  }

  @Test
  public void validate_whenIsPublishedAsOperatorFalseAndPublishableOrganisationProvideAndProjectOperatorNotProvided_thenEmptyBindingResult() {

    final var projectOperatorForm = new ProjectOperatorForm();
    projectOperatorForm.setIsPublishedAsOperator(false);
    projectOperatorForm.setOperator(null);
    projectOperatorForm.setPublishableOrganisation("123");

    final var errors = getErrors(projectOperatorForm);

    assertThat(errors.hasErrors()).isFalse();
  }

  private BindingResult getErrors(ProjectOperatorForm formToValidation) {
    final var errors = new BeanPropertyBindingResult(formToValidation, "form");
    ValidationUtils.invokeValidator(projectOperatorFormValidator, formToValidation, errors);
    return errors;
  }

  static class UnsupportedForm {}

}