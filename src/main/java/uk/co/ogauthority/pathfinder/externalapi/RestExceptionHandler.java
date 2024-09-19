package uk.co.ogauthority.pathfinder.externalapi;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(basePackages = "uk.co.ogauthority.pathfinder.externalapi")
public class RestExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<RestError> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException exception,
                                                              HttpServletRequest request) {
    LOGGER.error(
        "Unsupported content type provided to resource {}.",
        request.getRequestURI(),
        exception
    );

    var error = new RestError(
        HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
        String.format("An unsupported content type was provided: %s", exception.getLocalizedMessage())
    );

    return constructResponseEntity(error);

  }

  @ExceptionHandler({
      HttpMessageNotReadableException.class,
      MethodArgumentTypeMismatchException.class
  })
  public ResponseEntity<RestError> handleMismatchedInputException(HttpServletRequest request, Exception exception) {

    LOGGER.error(
        "Bad request made to resource {}.",
        request.getRequestURI(),
        exception
    );

    var error = new RestError(
        HttpStatus.BAD_REQUEST.value(),
        "A bad request was made to the requested resource"
    );

    return constructResponseEntity(error);

  }

  @ExceptionHandler(DraftProjectRequestException.class)
  public ResponseEntity<RestError> handleDraftProjectException(HttpServletRequest request, Exception exception) {
    LOGGER.error("DRAFT project requested", exception);

    var error = new RestError(
        HttpStatus.BAD_REQUEST.value(),
        "Could not fetch projects - you cannot request DRAFT projects"
    );

    return constructResponseEntity(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<RestError> handleGeneralException(HttpServletRequest request, Exception exception) {
    LOGGER.error(
        "Unhandled exception when requesting resource {}.",
        request.getRequestURI(),
        exception
    );

    var error = new RestError(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "An internal server error occurred"
    );

    return constructResponseEntity(error);
  }

  public static ResponseEntity<RestError> constructResponseEntity(RestError error) {
    return ResponseEntity
        .status(error.getStatus())
        .contentType(MediaType.APPLICATION_JSON)
        .body(error);
  }
}
