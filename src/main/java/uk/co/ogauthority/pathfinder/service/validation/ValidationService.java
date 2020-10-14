package uk.co.ogauthority.pathfinder.service.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
    return validate(form, bindingResult, validationType, new ArrayList<>());
  }

  public BindingResult validate(Object form,
                                BindingResult bindingResult,
                                ValidationType validationType,
                                List<Object> validationHints) {
    return validate(form, bindingResult, Set.of(validationType), validationHints);
  }

  public BindingResult validate(Object form,
                                BindingResult bindingResult,
                                Set<ValidationType> validationTypes) {
    return validate(form, bindingResult, validationTypes, new ArrayList<>());
  }

  public BindingResult validate(Object form,
                                BindingResult bindingResult,
                                Set<ValidationType> validationTypes,
                                List<Object> validationHints) {
    var validationTypeClasses = validationTypes
        .stream()
        .map(ValidationType::getValidationClass)
        .collect(Collectors.toList());

    validationHints.addAll(validationTypeClasses);

    validator.validate(form, bindingResult, validationHints.toArray());
    return bindingResult;
  }
}
