package uk.co.ogauthority.pathfinder.model.form.subscription;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class ManageSubscriptionFormValidatorTest {

  private ManageSubscriptionFormValidator validator;
  private ManageSubscriptionForm form;

  @BeforeEach
  public void setUp() {
    validator = new ManageSubscriptionFormValidator();
    form = new ManageSubscriptionForm();
  }

  @ParameterizedTest
  @ValueSource(strings = {"UPDATE_SUBSCRIPTION", "UNSUBSCRIBE"})
  void validate_validForm_thenNoErrors(String manageOption) {
    form.setSubscriptionManagementOption(manageOption);
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(validator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  void validate_invalidForm_thenErrors() {
    form.setSubscriptionManagementOption("Invalid option");
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(validator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("subscriptionManagementOption", Set.of("subscriptionManagementOption.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("subscriptionManagementOption", Set.of(ManageSubscriptionFormValidator.INVALID_OPTION_MESSAGE))
    );
  }
}
