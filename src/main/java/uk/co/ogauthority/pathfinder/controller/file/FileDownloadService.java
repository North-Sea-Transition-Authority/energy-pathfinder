package uk.co.ogauthority.pathfinder.controller.file;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.file.UploadedFile;
import uk.co.ogauthority.pathfinder.util.file.FileDownloadUtil;

@Service
public class FileDownloadService {

  /**
   * Serves file for download.
   *
   * @param uploadedFile file we want to trigger download for
   * @return the ResponseEntity object containing the downloaded file
   */
  public ResponseEntity<Resource> serveFile(UploadedFile uploadedFile) {
    var resource = FileDownloadUtil.fetchFileAsStream(uploadedFile.getFileName(), uploadedFile.getFileData());
    var mediaType = MediaType.parseMediaType(uploadedFile.getContentType());
    return FileDownloadUtil.getResourceAsResponse(
        resource,
        mediaType,
        uploadedFile.getFileName(),
        uploadedFile.getFileSize()
    );
  }
}
