package uk.co.ogauthority.pathfinder.exception;

import javax.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "The item could not be found")
public class PathfinderEntityNotFoundException extends EntityNotFoundException {
  public PathfinderEntityNotFoundException(String message) {
    super(message);
  }
}
