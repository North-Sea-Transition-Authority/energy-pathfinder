package uk.co.ogauthority.pathfinder.model.enums.project.decommissionedpipeline;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.model.form.forminput.selectableitem.SelectableItem;

public enum PipelineRemovalPremise {

  FULL_REMOVAL(
      "Full removal",
      ""
  ),
  MAJOR_INTERVENTION(
      "Decommission in situ with major intervention",
      "Includes rock cover or trench and bury of entire length"
  ),
  MINOR_INTERVENTION(
      "Decommission in situ with minor intervention",
      "Includes rock cover, trench and bury or cut and lift of only exposed or shallow buried sections"
  ),
  MINIMAL_INTERVENTION(
      "Decommission in situ with minimal intervention",
      "Includes removal of ends and remediation of snag risk"
  );

  private final String displayName;

  private final String hintText;

  PipelineRemovalPremise(String displayName, String hintText) {
    this.displayName = displayName;
    this.hintText = hintText;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getHintText() {
    return hintText;
  }

  public static List<SelectableItem> getAllAsSelectableItems() {
    return Arrays.stream(values())
        .map(pipelineRemovalPremise -> new SelectableItem(
            pipelineRemovalPremise.name(),
            pipelineRemovalPremise.getDisplayName(),
            pipelineRemovalPremise.getHintText()
        ))
        .collect(Collectors.toUnmodifiableList());
  }
}
