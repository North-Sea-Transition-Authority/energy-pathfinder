package uk.co.ogauthority.pathfinder.model.entity.communication;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
