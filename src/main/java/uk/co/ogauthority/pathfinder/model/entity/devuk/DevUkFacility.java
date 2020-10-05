package uk.co.ogauthority.pathfinder.model.entity.devuk;

import com.google.common.annotations.VisibleForTesting;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.Immutable;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;

@Entity(name = "devuk_facilities")
@Immutable
public class DevUkFacility implements SearchSelectable {

  @Id
  private Integer id;

  private String facilityName;

  public DevUkFacility() {}

  @VisibleForTesting
  public DevUkFacility(Integer id, String facilityName) {
    this.id = id;
    this.facilityName = facilityName;
  }

  public Integer getId() {
    return id;
  }

  public String getFacilityName() {
    return facilityName;
  }

  @Override
  public String getSelectionId() {
    return String.valueOf(id);
  }

  @Override
  public String getSelectionText() {
    return facilityName;
  }
}
