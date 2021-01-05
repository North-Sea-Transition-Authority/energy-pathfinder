package uk.co.ogauthority.pathfinder.service.difference;

import java.time.Instant;

public class UndiffableObject {

  private Instant time;

  public UndiffableObject(Instant time) {
    this.time = time;
  }

  public Instant getTime() {
    return time;
  }

  public void setTime(Instant time) {
    this.time = time;
  }
}
