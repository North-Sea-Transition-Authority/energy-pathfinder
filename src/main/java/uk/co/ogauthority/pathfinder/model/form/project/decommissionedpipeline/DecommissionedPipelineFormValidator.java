package uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate.MinMaxDateInputValidator;
import uk.co.ogauthority.pathfinder.service.pipeline.PipelineService;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class DecommissionedPipelineFormValidator implements SmartValidator {

  static final String PIPELINE_FIELD_ID = "pipeline";
  static final String INVALID_PIPELINE_ERROR_CODE = "isNotSelectable";
  static final String INVALID_PIPELINE_ERROR_MESSAGE = "Select a valid pipeline";

  private final MinMaxDateInputValidator minMaxDateInputValidator;

  private final PipelineService pipelineService;

  @Autowired
  public DecommissionedPipelineFormValidator(MinMaxDateInputValidator minMaxDateInputValidator,
                                             PipelineService pipelineService) {
    this.minMaxDateInputValidator = minMaxDateInputValidator;
    this.pipelineService = pipelineService;
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors, @NonNull Object... validationHints) {
    var form = (DecommissionedPipelineForm) target;

    var decommissionedPipelineValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(DecommissionedPipelineValidationHint.class))
        .map(DecommissionedPipelineValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected DecommissionedPipelineValidationHint to be provided")
        );

    ValidationUtil.invokeNestedValidator(
        errors,
        minMaxDateInputValidator,
        "decommissioningDate",
        form.getDecommissioningDate(),
        decommissionedPipelineValidationHint.getDecommissioningDateHints()
    );

    if (
        StringUtils.isNotBlank(form.getPipeline())
            && !pipelineService.isPipelineSelectable(Integer.parseInt(form.getPipeline()))
    ) {
      errors.rejectValue(
          PIPELINE_FIELD_ID,
          String.format("%s.%s", PIPELINE_FIELD_ID, INVALID_PIPELINE_ERROR_CODE),
          INVALID_PIPELINE_ERROR_MESSAGE
      );
    }

  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(DecommissionedPipelineForm.class);
  }
}
