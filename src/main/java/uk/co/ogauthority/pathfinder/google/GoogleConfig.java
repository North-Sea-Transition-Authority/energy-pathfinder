package uk.co.ogauthority.pathfinder.google;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import uk.co.ogauthority.pathfinder.google.recaptcha.RecaptchaConfig;

@ConfigurationProperties(prefix = "google")
@Validated
public record GoogleConfig(@Valid @NotNull RecaptchaConfig recaptcha) {
}
