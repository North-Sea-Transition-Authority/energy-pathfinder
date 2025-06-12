package uk.co.ogauthority.pathfinder.model.form.project.setup;

import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStageSubCategory;

public record ProjectSetupFormValidationHint(
    FieldStage fieldStage,
    FieldStageSubCategory fieldStageSubCategory,
    ValidationType validationType
) {}
