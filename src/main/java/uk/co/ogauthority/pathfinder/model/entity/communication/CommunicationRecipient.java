package uk.co.ogauthority.pathfinder.model.entity.communication;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "communication_recipients")
public class CommunicationRecipient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "communication_id")
  private Communication communication;

  private String sentToEmailAddress;

  @Column(name = "sent_datetime")
  private Instant sentInstant;

  public Integer getId() {
    return id;
  }

  public Communication getCommunication() {
    return communication;
  }

  public void setCommunication(Communication communication) {
    this.communication = communication;
  }

  public String getSentToEmailAddress() {
    return sentToEmailAddress;
  }

  public void setSentToEmailAddress(String sentToEmailAddress) {
    this.sentToEmailAddress = sentToEmailAddress;
  }

  public Instant getSentInstant() {
    return sentInstant;
  }

  public void setSentInstant(Instant sentInstant) {
    this.sentInstant = sentInstant;
  }
}
