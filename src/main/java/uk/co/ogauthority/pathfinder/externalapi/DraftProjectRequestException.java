package uk.co.ogauthority.pathfinder.externalapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class DraftProjectRequestException extends RuntimeException {

  public DraftProjectRequestException(String message) {
    super(message);
  }
}
