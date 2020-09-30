package uk.co.ogauthority.pathfinder.model.form.forminput.file;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import uk.co.ogauthority.pathfinder.model.form.validation.MandatoryUploadValidation;

public abstract class UploadMultipleFilesWithDescriptionForm {

  @Valid
  // MandatoryUploadValidation implies that the list requires at least one element
  @NotEmpty(groups = {MandatoryUploadValidation.class}, message = "You must upload at least one file")
  List<UploadFileWithDescriptionForm> uploadedFileWithDescriptionForms;

  public UploadMultipleFilesWithDescriptionForm() {
    this.uploadedFileWithDescriptionForms = new ArrayList<>();
  }

  public List<UploadFileWithDescriptionForm> getUploadedFileWithDescriptionForms() {
    return uploadedFileWithDescriptionForms;
  }

  public void setUploadedFileWithDescriptionForms(
      List<UploadFileWithDescriptionForm> uploadedFileWithDescriptionForms) {
    this.uploadedFileWithDescriptionForms = uploadedFileWithDescriptionForms;
  }
}
