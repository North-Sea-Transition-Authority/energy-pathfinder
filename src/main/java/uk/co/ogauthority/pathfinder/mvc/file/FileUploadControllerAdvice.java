package uk.co.ogauthority.pathfinder.mvc.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.co.ogauthority.pathfinder.config.file.FileUploadProperties;
import uk.co.ogauthority.pathfinder.controller.file.FileUploadController;

@ControllerAdvice(annotations = FileUploadController.class)
public class FileUploadControllerAdvice {

  private final FileUploadProperties fileUploadProperties;

  @Autowired
  public FileUploadControllerAdvice(FileUploadProperties fileUploadProperties) {
    this.fileUploadProperties = fileUploadProperties;
  }

  /**
   * As part of a ControllerAdvice these properties will be added in any request's Model accepted by controllers
   * with the @FileUploadFrontendController.
   * @param model the Model to which the file upload properties will be added to
   */
  @ModelAttribute
  public void addCommonModelAttributes(Model model) {
    String fileUploadAllowedExtensions = String.join(",", fileUploadProperties.getAllowedExtensions());
    model.addAttribute("fileuploadAllowedExtensions", fileUploadAllowedExtensions);
    model.addAttribute("fileuploadMaxUploadSize", String.valueOf(fileUploadProperties.getMaxFileSize()));
  }
}
