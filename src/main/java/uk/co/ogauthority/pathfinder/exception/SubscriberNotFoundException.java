package uk.co.ogauthority.pathfinder.exception;

import javax.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "The subscriber could not be found")
public class SubscriberNotFoundException extends EntityNotFoundException {

  public SubscriberNotFoundException(String message) {
    super(message);
  }
}
