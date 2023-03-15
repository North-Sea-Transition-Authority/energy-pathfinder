package uk.co.ogauthority.pathfinder.model.form.forminput.selectableitem;

/**
 * Utility class for representing an item with guidance that can be selected on the front end. Solves
 * the problem of having to specify radio items individually if each item has guidance but no hidden content.
 */
public class SelectableItem {

  private final String identifier;

  private final String prompt;

  private final String hintText;

  public SelectableItem(String identifier, String prompt, String hintText) {
    this.identifier = identifier;
    this.prompt = prompt;
    this.hintText = hintText;
  }

  public String getIdentifier() {
    return identifier;
  }

  public String getPrompt() {
    return prompt;
  }

  public String getHintText() {
    return hintText;
  }
}
