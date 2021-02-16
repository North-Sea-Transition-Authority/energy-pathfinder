package uk.co.ogauthority.pathfinder.model.entity.wellbore;

import com.google.common.annotations.VisibleForTesting;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Immutable;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;

@Entity
@Table(name = "wellbores")
@Immutable
public class Wellbore implements SearchSelectable {

  @Id
  private Integer id;

  private String registrationNo;

  public Wellbore() {}

  @VisibleForTesting
  public Wellbore(Integer id, String registrationNo) {
    this.id = id;
    this.registrationNo = registrationNo;
  }

  public Integer getId() {
    return id;
  }

  public String getRegistrationNo() {
    return registrationNo;
  }

  @Override
  public String getSelectionId() {
    return String.valueOf(id);
  }

  @Override
  public String getSelectionText() {
    return getRegistrationNo();
  }
}
