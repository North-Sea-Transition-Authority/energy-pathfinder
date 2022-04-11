package uk.co.ogauthority.pathfinder.model.view.organisationgroup;

import uk.co.ogauthority.pathfinder.model.addtolist.AddToListItem;

public class OrganisationGroupView implements AddToListItem {

  private Integer id;

  private String name;

  private Boolean valid;

  public OrganisationGroupView(Integer id, String  name, Boolean valid) {
    this.id = id;
    this.name = name;
    this.valid = valid;
  }

  @Override
  public String getId() {
    return id.toString();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Boolean isValid() {
    return valid;
  }
}
