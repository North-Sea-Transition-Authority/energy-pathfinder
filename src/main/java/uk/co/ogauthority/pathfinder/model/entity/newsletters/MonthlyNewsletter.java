package uk.co.ogauthority.pathfinder.model.entity.newsletters;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import uk.co.ogauthority.pathfinder.model.enums.NewsletterSendingResult;

@Entity
@Table(name = "monthly_newsletters")
public class MonthlyNewsletter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private Instant creationDateTime;

  @Enumerated(EnumType.STRING)
  private NewsletterSendingResult result;

  private Instant resultDateTime;


  public MonthlyNewsletter() {
  }

  public MonthlyNewsletter(Instant creationDateTime) {
    this.creationDateTime = creationDateTime;
  }

  public Instant getCreationDateTime() {
    return creationDateTime;
  }

  public void setCreationDateTime(Instant creationDateTime) {
    this.creationDateTime = creationDateTime;
  }

  public NewsletterSendingResult getResult() {
    return result;
  }

  public void setResult(NewsletterSendingResult result) {
    this.result = result;
  }

  public Instant getResultDateTime() {
    return resultDateTime;
  }

  public void setResultDateTime(Instant resultDateTime) {
    this.resultDateTime = resultDateTime;
  }
}
