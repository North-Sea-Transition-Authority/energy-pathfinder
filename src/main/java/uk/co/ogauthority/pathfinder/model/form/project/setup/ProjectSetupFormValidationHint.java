package uk.co.ogauthority.pathfinder.model.form.project.setup;

import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;

public final class ProjectSetupFormValidationHint {

  public final FieldStage fieldStage;
  public final ValidationType validationType;

  public ProjectSetupFormValidationHint(FieldStage fieldStage,
                                        ValidationType validationType) {
    this.fieldStage = fieldStage;
    this.validationType = validationType;
  }

  public FieldStage getFieldStage() {
    return fieldStage;
  }

  public ValidationType getValidationType() {
    return validationType;
  }
}
