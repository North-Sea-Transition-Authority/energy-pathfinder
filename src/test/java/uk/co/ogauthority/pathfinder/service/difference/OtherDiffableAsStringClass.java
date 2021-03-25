package uk.co.ogauthority.pathfinder.service.difference;

import uk.co.ogauthority.pathfinder.model.difference.DiffableAsString;

public class OtherDiffableAsStringClass implements DiffableAsString {

  String value;

  public OtherDiffableAsStringClass(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String getDiffableString() {
    return value;
  }
}
