package uk.co.ogauthority.pathfinder.google.recaptcha;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.google.GoogleConfig;

@Service
public class RecaptchaService {

  private final GoogleConfig googleConfig;
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  public static final String RESPONSE_REQUEST_PARAMETER_NAME = "g-recaptcha-response";
  private static final Logger LOGGER = LoggerFactory.getLogger(RecaptchaService.class);

  RecaptchaService(GoogleConfig googleConfig, ObjectMapper objectMapper) {
    this.googleConfig = googleConfig;
    this.httpClient = HttpClient.newBuilder().build();
    this.objectMapper = objectMapper;
  }

  boolean isValid(String response) {
    if (StringUtils.isBlank(response)) {
      LOGGER.info("Recaptcha response is null");
      return false;
    }
    try {
      Map<String, String> params = Map.of(
          "secret", googleConfig.recaptcha().secretKey(),
          "response", response
      );

      var request = HttpRequest.newBuilder()
          .uri(URI.create(googleConfig.recaptcha().verifyUrl()))
          .POST(HttpRequest.BodyPublishers.ofString(convertToFormEncodedString(params)))
          .header("Content-Type", String.valueOf(MediaType.APPLICATION_FORM_URLENCODED))
          .build();

      HttpResponse<String> httpResponse = httpClient.send(
          request,
          HttpResponse.BodyHandlers.ofString()
      );

      SiteVerifyResponseBody googleResponse = objectMapper.readValue(
          httpResponse.body(),
          SiteVerifyResponseBody.class);

      LOGGER.info("Recaptcha validation response: {}", googleResponse);

      return googleResponse.success;
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }

  }

  private String convertToFormEncodedString(Map<String, String> params) {
    return params.entrySet().stream()
        .map(entry -> String.join("=",
            URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8),
            URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
        ).collect(Collectors.joining("&"));
  }

  public void addRecaptchaToModelAndView(ModelAndView modelAndView) {
    modelAndView.addObject("recaptchaSiteKey", googleConfig.recaptcha().siteKey());
  }

  record SiteVerifyResponseBody(boolean success, @JsonProperty("challenge_ts") Instant challengeInstant,
                                String hostname, @JsonProperty("error-codes") List<String> errorCodes) { }

}
