package uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities;

import uk.co.ogauthority.pathfinder.model.enums.ValidationType;

public final class CollaborationOpportunityValidationHint {

  public static final String TOO_MANY_FILES_ERROR_MESSAGE = "You can only provide one opportunity document";
  private static final Integer FILE_UPLOAD_LIMIT = 1;

  private final ValidationType validationType;

  public CollaborationOpportunityValidationHint(ValidationType validationType) {
    this.validationType = validationType;
  }

  public Integer getFileUploadLimit() {
    return FILE_UPLOAD_LIMIT;
  }

}
