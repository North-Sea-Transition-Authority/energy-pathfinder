package uk.co.ogauthority.pathfinder.model.view;

import java.util.Objects;

public final class StringWithTag {

  private final String value;

  private final Tag tag;

  public StringWithTag() {
    this("", Tag.NONE);
  }

  public StringWithTag(String value) {
    this(value, Tag.NONE);
  }

  public StringWithTag(String value, Tag tag) {
    this.value = value;
    this.tag = tag;
  }

  public String getValue() {
    return value;
  }

  public Tag getTag() {
    return tag;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StringWithTag that = (StringWithTag) o;
    return Objects.equals(value, that.value)
        && tag == that.tag;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, tag);
  }
}
