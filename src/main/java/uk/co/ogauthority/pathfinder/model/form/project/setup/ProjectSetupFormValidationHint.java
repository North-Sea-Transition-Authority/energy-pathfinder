package uk.co.ogauthority.pathfinder.model.form.project.setup;

import uk.co.ogauthority.pathfinder.model.enums.ValidationType;

public final class ProjectSetupFormValidationHint {
  public static final String WELLS_REQUIRED_TEXT = "Select yes if you plan to add any wells to be decommissioned to your project";
  public static final String PLATFORMS_FPSOS_REQUIRED_TEXT = "Select yes if you plan to add any platforms or " +
      "FPSOs to be decommissioned to your project";
  public static final String SUBSEA_INFRASTRUCTURE_REQUIRED_TEXT = "Select yes if you plan to add any subsea " +
      "infrastructure to be decommissioned to your project";
  public static final String INTEGRATED_RIGS_REQUIRED_TEXT = "Select yes if you plan to add any integrated rigs to your project";
  public static final String PIPELINES_REQUIRED_TEXT = "Select yes if you plan to add any pipelines to be decommissioned to your project";

  public final boolean isDecomRelated;
  public final ValidationType validationType;

  public ProjectSetupFormValidationHint(boolean isDecomRelated,
                                        ValidationType validationType) {
    this.isDecomRelated = isDecomRelated;
    this.validationType = validationType;
  }

  public boolean isDecomRelated() {
    return isDecomRelated;
  }

  public boolean decomValidationRequired() {
    return ValidationType.FULL.equals(this.validationType) && this.isDecomRelated;
  }
}
