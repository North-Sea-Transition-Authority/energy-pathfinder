package uk.co.ogauthority.pathfinder.controller.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.service.file.FileUploadService;

@Controller
public class FileDownloadController {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloadController.class);

  private FileUploadService fileUploadService;
  private FileDownloadService fileDownloadService;

  @Autowired
  public FileDownloadController(FileUploadService fileUploadService,
                                FileDownloadService fileDownloadService) {
    this.fileUploadService = fileUploadService;
    this.fileDownloadService = fileDownloadService;
  }

  @GetMapping("/public/file")
  @ResponseBody
  public ResponseEntity<Resource> downloadFileById(@RequestParam("fileId") String fileId) {
    var fileUpload = fileUploadService.getUploadedFromReportableProjectByFileId(fileId);
    if (fileUpload.isPresent()) {
      return fileDownloadService.serveFile(fileUpload.get());
    } else {
      if (LOGGER.isWarnEnabled()) {
        LOGGER.warn(
            "Cannot find file with id {} in a reportable project",
            fileId.replaceAll("[\n\r\t]", "_")
        );
      }
      throw new PathfinderEntityNotFoundException(
          String.format("Cannot find file with id %s in a reportable project", fileId));
    }
  }
}
