package uk.co.ogauthority.pathfinder.model.view.integratedrig;

import java.util.List;
import uk.co.ogauthority.pathfinder.model.view.ProjectSummaryItem;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;

public class IntegratedRigView extends ProjectSummaryItem {

  private String structure;

  private String name;

  private String status;

  private String intentionToReactivate;

  private List<SummaryLink> summaryLinks;

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

  public List<SummaryLink> getSummaryLinks() {
    return summaryLinks;
  }

  public void setSummaryLinks(List<SummaryLink> summaryLinks) {
    this.summaryLinks = summaryLinks;
  }
}
