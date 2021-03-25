package uk.co.ogauthority.pathfinder.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FoxUrlService {

  private final String foxLoginUrl;
  private final String foxLogoutUrl;
  private final String foxRegistrationUrl;

  public FoxUrlService(@Value("${app.fox.login-url}") String foxLoginUrl,
                       @Value("${app.fox.logout-url}") String foxLogoutUrl,
                       @Value("${app.fox.registration-url}") String foxRegistrationUrl) {
    this.foxLoginUrl = foxLoginUrl;
    this.foxLogoutUrl = foxLogoutUrl;
    this.foxRegistrationUrl = foxRegistrationUrl;
  }

  public String getFoxLoginUrl() {
    return foxLoginUrl;
  }

  public String getFoxLogoutUrl() {
    return foxLogoutUrl;
  }

  public String getFoxRegistrationUrl() {
    return foxRegistrationUrl;
  }
}
