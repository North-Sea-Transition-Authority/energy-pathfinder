package uk.co.ogauthority.pathfinder.model.addtolist;

public class AddToListDemo implements AddToListItem {

  public String id;

  public String name;

  public AddToListDemo(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }
}
