package uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.infrastructure;

public final class InfrastructureCollaborationOpportunityValidationHint {

  public static final String TOO_MANY_FILES_ERROR_MESSAGE = "You can only provide one opportunity document";
  private static final Integer FILE_UPLOAD_LIMIT = 1;

  public Integer getFileUploadLimit() {
    return FILE_UPLOAD_LIMIT;
  }

}
