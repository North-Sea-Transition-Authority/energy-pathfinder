package uk.co.ogauthority.pathfinder.service.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;

@Service
public class ValidationService {

  private final SpringValidatorAdapter validator;

  @Autowired
  public ValidationService(SpringValidatorAdapter validator) {
    this.validator = validator;
  }

  public BindingResult validate(Object form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    validator.validate(form, bindingResult, validationType.getValidationClass());
    return bindingResult;
  }
}
