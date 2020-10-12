package uk.co.ogauthority.pathfinder.model.form.project.upcomingtender;

import java.time.LocalDate;
import java.util.ArrayList;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.AfterDateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.DateHint;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;

public final class UpcomingTenderValidationHint {

  public static final String TOO_MANY_FILES_ERROR_MESSAGE = "You can only provide one tender document";
  public static final FormInputLabel ESTIMATED_TENDER_LABEL = new FormInputLabel("estimated tender date");
  public static final String DATE_ERROR_LABEL = DateHint.TODAY_DATE_LABEL;
  private static final Integer FILE_UPLOAD_LIMIT = 1;

  private final ValidationType validationType;
  private final AfterDateHint estimatedTenderDateHint;

  public UpcomingTenderValidationHint(ValidationType validationType) {
    this.validationType = validationType;
    this.estimatedTenderDateHint = new AfterDateHint(
        ESTIMATED_TENDER_LABEL,
        LocalDate.now(),
        DATE_ERROR_LABEL
    );
  }

  public Integer getFileUploadLimit() {
    return FILE_UPLOAD_LIMIT;
  }

  public Object[] getEstimatedTenderDateHint() {
    var hints = new ArrayList<>();
    hints.add(estimatedTenderDateHint);
    DateInputValidator.addEmptyDateAcceptableHint(validationType, hints);
    return hints.toArray();
  }
}
