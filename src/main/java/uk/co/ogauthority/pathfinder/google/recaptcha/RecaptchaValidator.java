package uk.co.ogauthority.pathfinder.google.recaptcha;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
class RecaptchaValidator implements ConstraintValidator<ValidRecaptchaChallenge, RecaptchaProtectedForm> {

  private final RecaptchaService recaptchaService;

  RecaptchaValidator(RecaptchaService recaptchaService) {
    this.recaptchaService = recaptchaService;
  }

  @Override
  public void initialize(ValidRecaptchaChallenge constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(RecaptchaProtectedForm form, ConstraintValidatorContext constraintValidatorContext) {

    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
        .currentRequestAttributes()).getRequest();
    String response = request.getParameter(RecaptchaService.RESPONSE_REQUEST_PARAMETER_NAME);
    return recaptchaService.isValid(response);
  }
}
