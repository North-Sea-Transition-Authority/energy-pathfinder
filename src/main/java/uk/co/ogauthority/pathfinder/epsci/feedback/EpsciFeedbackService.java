package uk.co.ogauthority.pathfinder.epsci.feedback;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.fivium.feedbackmanagementservice.client.FeedbackClientService;
import uk.co.ogauthority.pathfinder.feedback.Feedback;

@Service
class EpsciFeedbackService {

  private final FeedbackClientService feedbackClientService;
  private final Clock utcClock;

  private static final Logger LOGGER = LoggerFactory.getLogger(EpsciFeedbackService.class);

  public EpsciFeedbackService(@Qualifier("utcClock") Clock utcClock,
                              ObjectMapper objectMapper,
                              @Value("${fms.url.base}") String baseUrl,
                              @Value("${fms.http.connectTimeout}") Long duration,
                              @Value("${fms.url.saveFeedback}") String saveFeedbackPostUrl,
                              @Value("${fms.service.name}") String serviceName,
                              @Value("${fms.auth.presharedKey}") String preSharedKey) {

    feedbackClientService = new FeedbackClientService(
      objectMapper,
      baseUrl,
      duration,
      saveFeedbackPostUrl,
      serviceName + "_EPSCI",
      preSharedKey
    );

    this.utcClock = utcClock;
  }

  void saveFeedback(EpsciFeedbackForm form) {
    var feedback = new Feedback();
    feedback.setServiceRating(form.serviceRating().name());
    feedback.setComment(form.feedback());
    feedback.setTransactionReference(form.epsciPath());
    feedback.setGivenDatetime(utcClock.instant());
    try {
      feedbackClientService.saveFeedback(feedback);
    } catch (Exception e) {
      LOGGER.error("Could not save feedback {}", feedback, e);
    }
  }

}
