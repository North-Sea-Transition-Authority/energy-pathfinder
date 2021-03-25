package uk.co.ogauthority.pathfinder.model.view.integratedrig;

import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.view.ProjectSummaryItem;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;

public class IntegratedRigView extends ProjectSummaryItem {

  private StringWithTag structure;

  private String name;

  private String status;

  private String intentionToReactivate;

  public StringWithTag getStructure() {
    return structure;
  }

  public void setStructure(StringWithTag structure) {
    this.structure = structure;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getIntentionToReactivate() {
    return intentionToReactivate;
  }

  public void setIntentionToReactivate(String intentionToReactivate) {
    this.intentionToReactivate = intentionToReactivate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    IntegratedRigView that = (IntegratedRigView) o;

    return Objects.equals(structure, that.structure)
        && Objects.equals(name, that.name)
        && Objects.equals(status, that.status)
        && Objects.equals(intentionToReactivate, that.intentionToReactivate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        structure,
        name,
        status,
        intentionToReactivate
    );
  }
}
