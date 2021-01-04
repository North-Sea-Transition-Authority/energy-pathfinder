package uk.co.ogauthority.pathfinder.model.form.projecttransfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectTransferTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTransferFormValidatorTest {

  private ProjectTransferFormValidator projectTransferFormValidator;

  private final PortalOrganisationGroup portalOrganisationGroup = ProjectOperatorTestUtil.ORG_GROUP;

  @Before
  public void setup() {
    projectTransferFormValidator = new ProjectTransferFormValidator();
  }

  /**
   * Ensure that the validator doesn't break or add any errors when the new organisation group
   * is null. The purpose of the validator is to add errors when there is a new organisation group
   * provided and it is the same as the existing one. Validation for the new organisation group not being
   * null is handled by the @NotNull annotation on the form.
   */
  @Test
  public void validate_whenNoNewOrganisationGroup_thenNoErrors() {
    var form = ProjectTransferTestUtil.createProjectTransferForm();
    form.setNewOrganisationGroup(null);
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectTransferValidationHint = new ProjectTransferValidationHint(portalOrganisationGroup);

    ValidationUtils.invokeValidator(projectTransferFormValidator, form, errors, projectTransferValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenDifferentNewOrganisationGroup_thenNoErrors() {
    var form = ProjectTransferTestUtil.createProjectTransferForm();
    form.setNewOrganisationGroup("2");
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectTransferValidationHint = new ProjectTransferValidationHint(portalOrganisationGroup);

    ValidationUtils.invokeValidator(projectTransferFormValidator, form, errors, projectTransferValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenSameNewOrganisationGroup_thenError() {
    var form = ProjectTransferTestUtil.createProjectTransferForm();
    form.setNewOrganisationGroup(Integer.toString(portalOrganisationGroup.getOrgGrpId()));
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectTransferValidationHint = new ProjectTransferValidationHint(portalOrganisationGroup);

    ValidationUtils.invokeValidator(projectTransferFormValidator, form, errors, projectTransferValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("newOrganisationGroup", Set.of("newOrganisationGroup" + FieldValidationErrorCodes.INVALID.getCode()))
    );
    assertThat(fieldErrorMessages).containsExactly(
        entry("newOrganisationGroup", Set.of(ProjectTransferFormValidator.SAME_OPERATOR_ERROR_MESSAGE))
    );
  }
}
