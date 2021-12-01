package uk.co.ogauthority.pathfinder.model.view.campaigninformation;

import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.addtolist.AddToListItem;
import uk.co.ogauthority.pathfinder.model.entity.project.SelectableProject;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;

public class CampaignProjectView implements AddToListItem, SearchSelectable {

  private final SelectableProject selectableProject;

  public CampaignProjectView(SelectableProject selectableProject) {
    this.selectableProject = selectableProject;
  }

  public SelectableProject getSelectableProject() {
    return selectableProject;
  }

  @Override
  public String getId() {
    return String.valueOf(selectableProject.getProjectId());
  }

  @Override
  public String getName() {
    return String.format(
        "%s (%s)",
        selectableProject.getProjectDisplayName(),
        selectableProject.getOperatorGroupName()
    );
  }

  @Override
  public Boolean isValid() {
    return selectableProject.isPublished();
  }

  @Override
  public String getSelectionId() {
    return getId();
  }

  @Override
  public String getSelectionText() {
    return getName();
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CampaignProjectView that = (CampaignProjectView) o;
    return Objects.equals(selectableProject, that.selectableProject);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        selectableProject
    );
  }
}
