package uk.co.ogauthority.pathfinder.service.difference;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class DifferenceTestWithUnsupportedListField {

  List<Instant> instantList;

  public DifferenceTestWithUnsupportedListField() {
    this.instantList = Collections.singletonList(Instant.now());
  }

  public List<Instant> getInstantList() {
    return instantList;
  }

  public void setInstantList(List<Instant> instantList) {
    this.instantList = instantList;
  }
}
