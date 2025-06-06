package uk.co.ogauthority.pathfinder.util.file;

import java.sql.Blob;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class FileDownloadUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloadUtil.class);

  private FileDownloadUtil() {
    throw new IllegalStateException("FileDownloadUtil is a utility class and should not be instantiated");
  }

  /**
   * Creates the ResponseEntity object to fully configure the HTTP response to the download request.
   *
   * @param resource      the file being downloaded
   * @param mediaType     the content type of the file
   * @param filename      the name of the file
   * @param contentLength the length of the file
   * @return the ResponseEntity object associated to the required resource
   */
  public static ResponseEntity<Resource> getResourceAsResponse(Resource resource,
                                                               MediaType mediaType,
                                                               String filename,
                                                               long contentLength) {
    return ResponseEntity.ok()
        .contentType(mediaType)
        .contentLength(contentLength)
        .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", filename))
        .body(resource);
  }


  public static Resource fetchFileAsStream(String fileName, Blob fileData) {
    try {
      return new InputStreamResource(
          fileData.getBinaryStream()
      );
    } catch (SQLException e) {
      if (LOGGER.isErrorEnabled()) {
        LOGGER.error(String.format("Failed to fetch file %s from the database. Exception: %s", fileName, e));
      }
      return null;
    }
  }

  /**
   * Converts the displayed file size into a more readable string with the appropriate unit. Eg. 29573 bytes -> 29kB
   *
   * @param fileSize the size of the file in bytes
   * @return a String specifying the size of the file with the corresponding byte unit
   */
  public static String fileSizeFormatter(Long fileSize) {
    int i = -1;
    String[] byteUnits = {" kB", " MB", " GB", " TB"};
    do {
      fileSize = fileSize / 1024;
      i++;
    } while (fileSize > 1024);

    return Math.max(fileSize, 1) + byteUnits[i];
  }
}
