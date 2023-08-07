package uk.co.ogauthority.pathfinder.model.entity.subscription;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;

@Entity
@Table(name = "subscriber_field_stage_preferences")
public class SubscriberFieldStage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Type(type = "uuid-char")
  @Column(name = "subscriber_uuid", updatable = false, nullable = false)
  private UUID subscriberUuid;

  @Enumerated(EnumType.STRING)
  private FieldStage fieldStage;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public UUID getSubscriberUuid() {
    return subscriberUuid;
  }

  public void setSubscriberUuid(UUID subscriberUuid) {
    this.subscriberUuid = subscriberUuid;
  }

  public FieldStage getFieldStage() {
    return fieldStage;
  }

  public void setFieldStage(FieldStage fieldStage) {
    this.fieldStage = fieldStage;
  }
}
