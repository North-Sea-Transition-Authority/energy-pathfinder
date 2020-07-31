package uk.co.ogauthority.pathfinder.model;

/**
 * Implementers of this interface can be displayed in checkboxes.
 */
public interface Checkable {

  String getIdentifier();

  String getDisplayName();

  Integer getDisplayOrder();

}
