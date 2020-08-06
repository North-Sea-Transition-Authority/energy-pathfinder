package uk.co.ogauthority.pathfinder.model.entity.devuk;

import com.google.common.annotations.VisibleForTesting;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.Immutable;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;

@Entity(name = "devuk_fields")
@Immutable
public class DevUkField implements SearchSelectable {

  @Id
  private Integer fieldId;

  private String fieldName;

  private Integer status;

  private Integer operatorOuId;

  public DevUkField() {
  }

  @VisibleForTesting
  public DevUkField(int fieldId, String fieldName, int status) {
    this.fieldId = fieldId;
    this.fieldName = fieldName;
    this.status = status;
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

  @Override
  public String getSelectionId() {
    return String.valueOf(fieldId);
  }

  @Override
  public String getSelectionText() {
    return fieldName;
  }
}
