package uk.co.ogauthority.pathfinder.model.entity.subscription;

import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import uk.co.ogauthority.pathfinder.model.enums.subscription.RelationToPathfinder;

@Entity
@Table(name = "subscribers")
public class Subscriber {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @GeneratedValue
  @Type(type = "uuid-char")
  @Column(name = "uuid", updatable = false, nullable = false)
  private UUID uuid;

  private String forename;

  private String surname;

  private String emailAddress;

  @Enumerated(EnumType.STRING)
  private RelationToPathfinder relationToPathfinder;

  @Lob
  @Column(name = "subscribe_reason", columnDefinition = "CLOB")
  private String subscribeReason;

  @Column(name = "subscribed_datetime")
  private Instant subscribedInstant;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public String getForename() {
    return forename;
  }

  public void setForename(String forename) {
    this.forename = forename;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public RelationToPathfinder getRelationToPathfinder() {
    return relationToPathfinder;
  }

  public void setRelationToPathfinder(
      RelationToPathfinder relationToPathfinder) {
    this.relationToPathfinder = relationToPathfinder;
  }

  public String getSubscribeReason() {
    return subscribeReason;
  }

  public void setSubscribeReason(String subscribeReason) {
    this.subscribeReason = subscribeReason;
  }

  public Instant getSubscribedInstant() {
    return subscribedInstant;
  }

  public void setSubscribedInstant(Instant subscribedInstant) {
    this.subscribedInstant = subscribedInstant;
  }
}
