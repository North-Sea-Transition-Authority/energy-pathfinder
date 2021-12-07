package uk.co.ogauthority.pathfinder.feedback;

import java.time.Instant;
import uk.co.fivium.feedbackmanagementservice.client.FeedbackManagementServiceFeedback;

public class TestFeedback implements FeedbackManagementServiceFeedback {

  private final Integer id;
  private final String submitterName;
  private final String submitterEmail;
  private final String serviceRating;
  private final String serviceImprovement;
  private final Instant givenDatetime;
  private final Integer transactionId;
  private final String transactionReference;

  public TestFeedback(Integer id, String submitterName, String submitterEmail, String serviceRating,
                      String serviceImprovement, Instant givenDatetime, Integer transactionId,
                      String transactionReference) {
    this.id = id;
    this.submitterName = submitterName;
    this.submitterEmail = submitterEmail;
    this.serviceRating = serviceRating;
    this.serviceImprovement = serviceImprovement;
    this.givenDatetime = givenDatetime;
    this.transactionId = transactionId;
    this.transactionReference = transactionReference;
  }

  public Integer getId() {
    return id;
  }

  @Override
  public String getSubmitterName() {
    return submitterName;
  }

  @Override
  public String getSubmitterEmail() {
    return submitterEmail;
  }

  @Override
  public String getServiceRating() {
    return serviceRating;
  }

  @Override
  public String getComment() {
    return serviceImprovement;
  }

  @Override
  public Instant getGivenDatetime() {
    return givenDatetime;
  }

  @Override
  public Integer getTransactionId() {
    return transactionId;
  }

  @Override
  public String getTransactionReference() {
    return transactionReference;
  }
}