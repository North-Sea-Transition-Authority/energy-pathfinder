package uk.co.ogauthority.pathfinder.model.form.subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.subscription.RelationToPathfinder;

@Component
public class SubscribeFormValidator implements SmartValidator {

  public static final String MISSING_SUBSCRIBE_REASON_ERROR = "Enter a reason for subscribing";
  public static final String MISSING_FIELD_STAGES_ERROR = "Select at least one project type to be updated on";
  public static final String INVALID_FIELD_STAGE_ERROR = "Select a valid energy project type";

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (SubscribeForm) target;

    if (RelationToPathfinder.OTHER.equals(form.getRelationToPathfinder())) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "subscribeReason",
          "subscribeReason.invalid",
          MISSING_SUBSCRIBE_REASON_ERROR
      );
    }

    validateFieldStages(form, errors);
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  private void validateFieldStages(SubscribeForm form, Errors errors) {
    if (Boolean.FALSE.equals(form.getInterestedInAllProjects())) {
      ValidationUtils.rejectIfEmpty(
          errors,
          "fieldStages",
          "fieldStages.invalid",
          MISSING_FIELD_STAGES_ERROR
      );

      var fieldStages = form.getFieldStages();
      if (Objects.nonNull(fieldStages)) {
        List<String> invalidFieldStages = new ArrayList<>();
        fieldStages.forEach(
            fieldStageString -> {
              try {
                FieldStage.valueOf(fieldStageString);
              } catch (IllegalArgumentException e) {
                invalidFieldStages.add(fieldStageString);
              }
            }
        );

        if (!invalidFieldStages.isEmpty()) {
          errors.rejectValue(
              "fieldStages",
              "fieldStages.invalid",
              INVALID_FIELD_STAGE_ERROR);
        }
      }
    }
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(SubscribeForm.class);
  }
}
