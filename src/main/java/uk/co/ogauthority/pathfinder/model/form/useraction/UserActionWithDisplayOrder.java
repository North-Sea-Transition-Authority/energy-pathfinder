package uk.co.ogauthority.pathfinder.model.form.useraction;

import java.util.Objects;

public class UserActionWithDisplayOrder {

  private final UserAction userAction;

  private final int displayOrder;

  public UserActionWithDisplayOrder(UserAction userAction, int displayOrder) {
    this.userAction = userAction;
    this.displayOrder = displayOrder;
  }

  public UserAction getUserAction() {
    return userAction;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserActionWithDisplayOrder that = (UserActionWithDisplayOrder) o;
    return displayOrder == that.displayOrder
        && Objects.equals(userAction, that.userAction);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userAction, displayOrder);
  }
}
