package uk.co.ogauthority.pathfinder.model.view.wellbore;

import uk.co.ogauthority.pathfinder.model.addtolist.AddToListItem;

public class WellboreView implements AddToListItem {

  private Integer id;

  private String registrationNo;

  private Boolean valid;

  public WellboreView(Integer id, String registrationNo, Boolean valid) {
    this.id = id;
    this.registrationNo = registrationNo;
    this.valid = valid;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setRegistrationNo(String registrationNo) {
    this.registrationNo = registrationNo;
  }

  public void setValid(Boolean valid) {
    this.valid = valid;
  }

  @Override
  public String getId() {
    return String.valueOf(id);
  }

  @Override
  public String getName() {
    return registrationNo;
  }

  @Override
  public Boolean isValid() {
    return valid;
  }
}
