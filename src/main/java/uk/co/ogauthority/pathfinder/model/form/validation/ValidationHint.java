package uk.co.ogauthority.pathfinder.model.form.validation;

public interface ValidationHint {

  boolean isValid(Object objectToTest);

  String getErrorMessage();

  String getCode();
}
