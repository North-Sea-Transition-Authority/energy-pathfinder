package uk.co.ogauthority.pathfinder.util.file;

import org.springframework.validation.Errors;
import uk.co.ogauthority.pathfinder.model.form.forminput.file.UploadMultipleFilesWithDescriptionForm;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;

public class FileUploadUtil {

  private FileUploadUtil() {
    throw new IllegalStateException("FileUploadUtil is a utility class and should not be instantiated");
  }

  public static void validateMaxFileLimit(UploadMultipleFilesWithDescriptionForm uploadForm,
                                          Errors errors,
                                          int maxFileCount,
                                          String limitExceededMessage) {

    if (uploadForm.getUploadedFileWithDescriptionForms().size() > maxFileCount) {
      errors.rejectValue(
          "uploadedFileWithDescriptionForms",
          "uploadedFileWithDescriptionForms" + FieldValidationErrorCodes.EXCEEDED_MAXIMUM_FILE_UPLOAD_LIMIT.getCode(),
          limitExceededMessage
      );
    }

  }
}
