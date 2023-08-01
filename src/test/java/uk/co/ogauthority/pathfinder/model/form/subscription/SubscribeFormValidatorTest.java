package uk.co.ogauthority.pathfinder.model.form.subscription;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.subscription.RelationToPathfinder;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class SubscribeFormValidatorTest {

  private SubscribeFormValidator subscribeFormValidator;
  private SubscribeForm form;

  @BeforeEach
  public void setup() {
    subscribeFormValidator = new SubscribeFormValidator();
    form = new SubscribeForm();
  }

  @Test
  void validate_whenRelationToPathfinderNull_thenNoErrors() {
    form.setRelationToPathfinder(null);
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(subscribeFormValidator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  void validate_whenRelationToPathfinderNotOther_thenNoErrors() {
    form.setRelationToPathfinder(RelationToPathfinder.OPERATOR);
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(subscribeFormValidator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  void validate_whenRelationToPathfinderOtherAndSubscribeReasonNotNull_thenNoErrors() {
    form.setRelationToPathfinder(RelationToPathfinder.OTHER);
    form.setSubscribeReason("Test subscribe reason");
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(subscribeFormValidator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  void validate_whenRelationToPathfinderOtherAndSubscribeReasonNull_thenErrors() {
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

  @Test
  void validate_whenInterestedInAllProjects_thenNoErrors() {
    form.setInterestedInAllProjects(true);
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(subscribeFormValidator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  void validate_whenNotInterestedInAllProjectsAndNoFieldStages_thenErrors() {
    form.setInterestedInAllProjects(false);
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(subscribeFormValidator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("fieldStages", Set.of("fieldStages.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("fieldStages", Set.of(SubscribeFormValidator.MISSING_FIELD_STAGES_ERROR))
    );
  }

  @Test
  void validate_whenNotInterestedInAllProjectsAndHasFieldStages_thenNoErrors() {
    form.setInterestedInAllProjects(false);
    form.setFieldStages(List.of(FieldStage.DEVELOPMENT, FieldStage.CARBON_CAPTURE_AND_STORAGE));
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(subscribeFormValidator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }
}
