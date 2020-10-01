package uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities;

import java.time.LocalDate;
import java.util.ArrayList;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.AfterDateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.DateHint;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;

public final class CollaborationOpportunityValidationHint {

  public static final String TOO_MANY_FILES_ERROR_MESSAGE = "You can only provide one opportunity document";
  public static final FormInputLabel ESTIMATED_SERVICE_LABEL = new FormInputLabel("estimated service date");
  public static final String DATE_ERROR_LABEL = DateHint.TODAY_DATE_LABEL;
  private static final Integer FILE_UPLOAD_LIMIT = 1;

  private final ValidationType validationType;
  private final AfterDateHint estimatedServiceDateHint;

  public CollaborationOpportunityValidationHint(ValidationType validationType) {
    this.validationType = validationType;

    this.estimatedServiceDateHint = new AfterDateHint(ESTIMATED_SERVICE_LABEL, LocalDate.now(), DATE_ERROR_LABEL);
  }

  public Object[] getEstimatedServiceDateHints() {
    var hints = new ArrayList<>();
    hints.add(estimatedServiceDateHint);
    DateInputValidator.addEmptyDateAcceptableHint(validationType, hints);
    return hints.toArray();
  }

  public Integer getFileUploadLimit() {
    return FILE_UPLOAD_LIMIT;
  }

}
