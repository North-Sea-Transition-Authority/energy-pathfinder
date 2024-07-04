package uk.co.ogauthority.pathfinder.model.entity.subscription;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;

@Entity
@Table(name = "subscriber_field_stage_preferences")
public class SubscriberFieldStage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @JdbcTypeCode(SqlTypes.VARCHAR)
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
