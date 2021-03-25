package uk.co.ogauthority.pathfinder.util.summary;

/**
 * An interface of common summary item fields.
 * Used by the {@link SummaryUtil} to do some summary item actions such as validation.
 */
public interface SummaryItem {

  Integer getDisplayOrder();

  Boolean isValid();

}
