package uk.co.ogauthority.pathfinder.model.view.integratedrig;

import uk.co.ogauthority.pathfinder.model.view.ProjectSummaryItem;

public class IntegratedRigView extends ProjectSummaryItem {

  private String structure;

  private String name;

  private String status;

  private String intentionToReactivate;

  public String getStructure() {
    return structure;
  }

  public void setStructure(String structure) {
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
}
