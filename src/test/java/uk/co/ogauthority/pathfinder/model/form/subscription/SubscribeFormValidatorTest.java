package uk.co.ogauthority.pathfinder.model.form.subscription;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.enums.subscription.RelationToPathfinder;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class SubscribeFormValidatorTest {

  private SubscribeFormValidator subscribeFormValidator;

  @Before
  public void setup() {
    subscribeFormValidator = new SubscribeFormValidator();
  }

  @Test
  public void validate_whenRelationToPathfinderNull_thenNoErrors() {
    var form = new SubscribeForm();
    form.setRelationToPathfinder(null);
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(subscribeFormValidator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenRelationToPathfinderNotOther_thenNoErrors() {
    var form = new SubscribeForm();
    form.setRelationToPathfinder(RelationToPathfinder.OPERATOR);
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(subscribeFormValidator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenRelationToPathfinderOtherAndSubscribeReasonNotNull_thenNoErrors() {
    var form = new SubscribeForm();
    form.setRelationToPathfinder(RelationToPathfinder.OTHER);
    form.setSubscribeReason("Test subscribe reason");
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(subscribeFormValidator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenRelationToPathfinderOtherAndSubscribeReasonNull_thenErrors() {
    var form = new SubscribeForm();
    form.setRelationToPathfinder(RelationToPathfinder.OTHER);
    form.setSubscribeReason(null);
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(subscribeFormValidator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("subscribeReason", Set.of("subscribeReason.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("subscribeReason", Set.of(SubscribeFormValidator.MISSING_SUBSCRIBE_REASON_ERROR))
    );
  }
}
