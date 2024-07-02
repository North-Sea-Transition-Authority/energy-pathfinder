package uk.co.ogauthority.pathfinder.model.entity.devuk;

import com.google.common.annotations.VisibleForTesting;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;
import uk.co.ogauthority.pathfinder.model.enums.project.UkcsArea;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;

@Entity(name = "devuk_fields")
@Immutable
public class DevUkField implements SearchSelectable {

  @Id
  private Integer fieldId;

  private String fieldName;

  private Integer status;

  private Integer operatorOuId;

  @Enumerated(EnumType.STRING)
  private UkcsArea ukcsArea;

  private Boolean isLandward;

  private Boolean isActive;

  public DevUkField() {
  }

  @VisibleForTesting
  public DevUkField(int fieldId, String fieldName, int status, UkcsArea ukcsArea) {
    this.fieldId = fieldId;
    this.fieldName = fieldName;
    this.status = status;
    this.ukcsArea = ukcsArea;
  }


  public Integer getFieldId() {
    return fieldId;
  }

  public void setFieldId(Integer fieldId) {
    this.fieldId = fieldId;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Integer getOperatorOuId() {
    return operatorOuId;
  }

  public void setOperatorOuId(Integer operatorOuId) {
    this.operatorOuId = operatorOuId;
  }

  public UkcsArea getUkcsArea() {
    return ukcsArea;
  }

  public void setUkcsArea(UkcsArea ukcsArea) {
    this.ukcsArea = ukcsArea;
  }

  public Boolean isLandward() {
    return isLandward;
  }

  public void setLandward(Boolean landward) {
    isLandward = landward;
  }

  public Boolean isActive() {
    return isActive;
  }

  public void setActive(Boolean active) {
    isActive = active;
  }

  @Override
  public String getSelectionId() {
    return String.valueOf(fieldId);
  }

  @Override
  public String getSelectionText() {
    return getFieldName();
  }
}
