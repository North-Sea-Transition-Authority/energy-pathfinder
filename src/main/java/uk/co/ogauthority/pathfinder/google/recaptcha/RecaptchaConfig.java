package uk.co.ogauthority.pathfinder.google.recaptcha;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record RecaptchaConfig(@NotBlank String verifyUrl, @NotBlank String siteKey, @NotBlank String secretKey) { }
