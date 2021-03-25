package uk.co.ogauthority.pathfinder.model.searchselector;

/**
 * SearchSelectable provides the required information required to produce a RestSearchItem.
 * A list of SearchSelectable implementations are used by the SearchSelectorService.
 */
public interface SearchSelectable {

  String getSelectionId();

  String getSelectionText();

}
