package uk.co.ogauthority.pathfinder.model.entity.communication;

import java.time.Instant;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.enums.communication.CommunicationStatus;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationJourneyStatus;

@Entity
@Table(name = "communications")
public class Communication {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  private RecipientType recipientType;

  private String emailSubject;

  @Lob
  @Column(name = "email_body", columnDefinition = "CLOB")
  private String emailBody;

  @Enumerated(EnumType.STRING)
  private CommunicationStatus status;

  private Instant createdDatetime;

  private Integer createdByWuaId;

  private Instant submittedDatetime;

  private Integer submittedByWuaId;

  @Enumerated(EnumType.STRING)
  @Column(name = "latest_journey_status")
  private CommunicationJourneyStatus latestCommunicationJourneyStatus;

  private String greetingText;

  private String signOffText;

  private String signOffIdentifier;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public RecipientType getRecipientType() {
    return recipientType;
  }

  public void setRecipientType(RecipientType recipientType) {
    this.recipientType = recipientType;
  }

  public String getEmailSubject() {
    return emailSubject;
  }

  public void setEmailSubject(String emailSubject) {
    this.emailSubject = emailSubject;
  }

  public String getEmailBody() {
    return emailBody;
  }

  public void setEmailBody(String emailBody) {
    this.emailBody = emailBody;
  }

  public CommunicationStatus getStatus() {
    return status;
  }

  public void setStatus(CommunicationStatus status) {
    this.status = status;
  }

  public Instant getCreatedDatetime() {
    return createdDatetime;
  }

  public void setCreatedDatetime(Instant createdDatetime) {
    this.createdDatetime = createdDatetime;
  }

  public Integer getCreatedByWuaId() {
    return createdByWuaId;
  }

  public void setCreatedByWuaId(Integer createdByWuaId) {
    this.createdByWuaId = createdByWuaId;
  }

  public Instant getSubmittedDatetime() {
    return submittedDatetime;
  }

  public void setSubmittedDatetime(Instant submittedDatetime) {
    this.submittedDatetime = submittedDatetime;
  }

  public Integer getSubmittedByWuaId() {
    return submittedByWuaId;
  }

  public void setSubmittedByWuaId(Integer submittedByWuaId) {
    this.submittedByWuaId = submittedByWuaId;
  }

  public CommunicationJourneyStatus getLatestCommunicationJourneyStatus() {
    return latestCommunicationJourneyStatus;
  }

  public void setLatestCommunicationJourneyStatus(CommunicationJourneyStatus latestCommunicationJourneyStatus) {
    this.latestCommunicationJourneyStatus = latestCommunicationJourneyStatus;
  }

  public String getGreetingText() {
    return greetingText;
  }

  public void setGreetingText(String greetingText) {
    this.greetingText = greetingText;
  }

  public String getSignOffText() {
    return signOffText;
  }

  public void setSignOffText(String signOffText) {
    this.signOffText = signOffText;
  }

  public String getSignOffIdentifier() {
    return signOffIdentifier;
  }

  public void setSignOffIdentifier(String signOffIdentifier) {
    this.signOffIdentifier = signOffIdentifier;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Communication communication = (Communication) o;
    return Objects.equals(id, communication.id)
        && Objects.equals(recipientType, communication.recipientType)
        && Objects.equals(emailSubject, communication.emailSubject)
        && Objects.equals(emailBody, communication.emailBody)
        && Objects.equals(status, communication.status)
        && Objects.equals(createdDatetime, communication.createdDatetime)
        && Objects.equals(createdByWuaId, communication.createdByWuaId)
        && Objects.equals(submittedDatetime, communication.submittedDatetime)
        && Objects.equals(submittedByWuaId, communication.submittedByWuaId)
        && Objects.equals(latestCommunicationJourneyStatus, communication.latestCommunicationJourneyStatus)
        && Objects.equals(greetingText, communication.greetingText)
        && Objects.equals(signOffText, communication.signOffText)
        && Objects.equals(signOffIdentifier, communication.signOffIdentifier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        recipientType,
        emailSubject,
        emailBody,
        status,
        createdDatetime,
        createdByWuaId,
        submittedDatetime,
        submittedByWuaId,
        latestCommunicationJourneyStatus,
        greetingText,
        signOffText,
        signOffIdentifier
    );
  }
}
