package uk.co.ogauthority.pathfinder.service.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;

@Service
public class ControllerHelperService {

  private final MessageSource messageSource;

  @Autowired
  public ControllerHelperService(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  /**
   * Standardises basic form POST behaviour, allows controllers to either return a ModelAndView that's failed validation
   * (populated with validation errors) or do a caller-specified action if passed validation.
   * @param bindingResult result of binding the form object from request
   * @param modelAndView the model and view to add the validation errors to if validation failed during binding
   * @param ifValid the action to perform if the validation passes
   * @return passed-in ModelAndView with validation errors added if validation failed, caller-specified ModelAndView otherwise
   */
  public ModelAndView checkErrorsAndRedirect(BindingResult bindingResult,
                                             ModelAndView modelAndView,
                                             Object form,
                                             Supplier<ModelAndView> ifValid) {

    if (bindingResult.hasErrors()) {
      addFieldValidationErrors(modelAndView, bindingResult, form);
      return modelAndView;
    }

    return ifValid.get();

  }

  /**
   * Adds field validation errors to a model and view.
   * @param modelAndView The model and view which failed validation
   * @param bindingResult The result of the submitted form containing the list of validation errors
   * @param form The submitted form object
   */
  private void addFieldValidationErrors(ModelAndView modelAndView, BindingResult bindingResult, Object form) {

    List<ErrorItem> errorList = new ArrayList<>();
    List<FieldError> fieldErrors = getFieldErrorsInFormFieldOrder(form, bindingResult);

    IntStream.range(0, fieldErrors.size()).forEach(index -> {

      var fieldError = fieldErrors.get(index);

      // try to get a message from the custom message store for the error, fallback to default message
      String errorMessage = messageSource.getMessage(
          getTypeMismatchErrorCode(fieldError).orElse(""),
          null,
          fieldError.getDefaultMessage(),
          Locale.getDefault());

      errorList.add(new ErrorItem(index, fieldError.getField(), errorMessage));

    });

    modelAndView.addObject("errorList", errorList);

  }

  private Optional<String> getTypeMismatchErrorCode(FieldError fieldError) {

    boolean isTypeMismatch = Objects.equals(fieldError.getCode(), "typeMismatch");

    // if there's no type mismatch, no need to find specific error code
    if (!isTypeMismatch) {
      return Optional.empty();
    }

    // if we have a type mismatch, find the mismatch code for the type we were expecting
    return Optional.ofNullable(fieldError.getCodes())
        .flatMap(codes -> Arrays.stream(codes)
            .filter(code -> code.contains("typeMismatch.java."))
            .findFirst());

  }

  /**
   * Helper method to ensure that the FieldErrors are returned in the same
   * order as the fields on the form object.
   * @param form The form that the validation has run against
   * @param bindingResult The result of the submitted form containing the list of validation errors
   * @return a list of FieldError objects sorted in the same order as the fields declared on the form
   */
  private List<FieldError> getFieldErrorsInFormFieldOrder(Object form, BindingResult bindingResult) {

    List<FieldError> errorList = new ArrayList<>();

    if (form != null && bindingResult != null && bindingResult.hasErrors()) {
      var formFields = Arrays.stream(form.getClass().getDeclaredFields())
          .map(Field::getName)
          .collect(Collectors.toList());

      errorList.addAll(bindingResult.getFieldErrors());
      errorList.sort(Comparator.comparing(fieldError -> formFields.indexOf(fieldError.getField())));
    } else if (form == null && bindingResult != null) {
      errorList = bindingResult.getFieldErrors();
    }

    return errorList;
  }

}
