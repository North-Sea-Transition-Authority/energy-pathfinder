package uk.co.ogauthority.pathfinder.model.form.projecttransfer;

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
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorFormValidator;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectTransferTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTransferFormValidatorTest {

  private final String publishableOrganisationFormFieldName = ProjectOperatorFormValidator.PUBLISHABLE_ORGANISATION_FORM_FIELD_NAME;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  private ProjectTransferFormValidator projectTransferFormValidator;

  private final PortalOrganisationGroup currentPortalOrganisationGroup = ProjectOperatorTestUtil.ORG_GROUP;

  @Before
  public void setup() {
    final var projectOperatorFormValidator = new ProjectOperatorFormValidator(portalOrganisationAccessor);
    projectTransferFormValidator = new ProjectTransferFormValidator(projectOperatorFormValidator);
  }

  /**
   * Ensure that the validator doesn't break or add any errors when the new organisation group
   * is null. The purpose of the validator is to add errors when there is a new organisation group
   * provided and it is the same as the existing one. Validation for the new organisation group not being
   * null is handled by the @NotNull annotation on the form.
   */
  @Test
  public void validate_whenNoNewOrganisationGroup_thenNoErrors() {

    final var form = ProjectTransferTestUtil.createProjectTransferForm();
    form.setNewOrganisationGroup(null);

    final var errors = getErrors(form);

    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenDifferentNewOrganisationGroup_thenNoErrors() {

    final var form = ProjectTransferTestUtil.createProjectTransferForm();
    form.setNewOrganisationGroup("2");

    final var errors = getErrors(form);

    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenSameNewOrganisationGroup_thenError() {

    final var form = ProjectTransferTestUtil.createProjectTransferForm();
    form.setNewOrganisationGroup(Integer.toString(currentPortalOrganisationGroup.getOrgGrpId()));

    final var errors = getErrors(form);

    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    final var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("newOrganisationGroup", Set.of("newOrganisationGroup" + FieldValidationErrorCodes.INVALID.getCode()))
    );
    assertThat(fieldErrorMessages).containsExactly(
        entry("newOrganisationGroup", Set.of(ProjectTransferFormValidator.SAME_OPERATOR_ERROR_MESSAGE))
    );
  }

  @Test
  public void validate_whenIsPublishedAsOperatorFalseAndValidPublishableOrganisation_thenEmptyBindingResult() {

    final var validOrganisationGroupId = 100;
    final var validOrganisationUnitId = 200;

    final var projectTransferForm = ProjectTransferTestUtil.createProjectTransferForm();
    projectTransferForm.setNewOrganisationGroup(String.valueOf(validOrganisationGroupId));
    projectTransferForm.setIsPublishedAsOperator(false);
    projectTransferForm.setPublishableOrganisation(String.valueOf(validOrganisationUnitId));

    when(portalOrganisationAccessor.isOrganisationUnitActiveAndPartOfOrganisationGroup(
        validOrganisationGroupId,
        validOrganisationUnitId
    )).thenReturn(true);

    final var errors = getErrors(projectTransferForm);

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  public void validate_whenIsPublishedAsOperatorFalseAndNoPublishableOrganisation_thenPopulatedBindingResult() {

    final var projectTransferForm = ProjectTransferTestUtil.createProjectTransferForm();
    projectTransferForm.setIsPublishedAsOperator(false);
    projectTransferForm.setPublishableOrganisation(null);
    // ensure different new organisation group to avoid validation error
    projectTransferForm.setNewOrganisationGroup(String.valueOf(currentPortalOrganisationGroup.getOrgGrpId() + 1));

    final var errors = getErrors(projectTransferForm);

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

    final var projectTransferForm = ProjectTransferTestUtil.createProjectTransferForm();
    projectTransferForm.setIsPublishedAsOperator(false);
    projectTransferForm.setNewOrganisationGroup(String.valueOf(validOrganisationGroupId));
    projectTransferForm.setPublishableOrganisation(String.valueOf(organisationUnitIdNotInGroup));

    when(portalOrganisationAccessor.isOrganisationUnitActiveAndPartOfOrganisationGroup(
        validOrganisationGroupId,
        organisationUnitIdNotInGroup
    )).thenReturn(false);

    final var errors = getErrors(projectTransferForm);

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

    final var projectTransferForm = ProjectTransferTestUtil.createProjectTransferForm();
    projectTransferForm.setIsPublishedAsOperator(false);
    projectTransferForm.setNewOrganisationGroup(null);
    projectTransferForm.setPublishableOrganisation("123");

    final var errors = getErrors(projectTransferForm);

    assertThat(errors.hasErrors()).isFalse();
  }

  private BindingResult getErrors(ProjectTransferForm formToValidation) {
    final var errors = new BeanPropertyBindingResult(formToValidation, "form");
    final var projectTransferValidationHint = new ProjectTransferValidationHint(currentPortalOrganisationGroup);
    ValidationUtils.invokeValidator(projectTransferFormValidator, formToValidation, errors, projectTransferValidationHint);
    return errors;
  }
}
