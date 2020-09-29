package uk.co.ogauthority.pathfinder.model.view.projectlocation;

import uk.co.ogauthority.pathfinder.model.addtolist.AddToListItem;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;

public class ProjectLocationBlockView implements AddToListItem {

  public String compositeKey;

  public String blockReference;

  public Boolean isValid;

  public ProjectLocationBlockView(ProjectLocationBlock projectLocationBlock, Boolean isValid) {
    this.compositeKey = projectLocationBlock.getCompositeKey();
    this.blockReference = projectLocationBlock.getBlockReference();
    this.isValid = isValid;
  }

  public ProjectLocationBlockView(String compositeKey,
                                  String blockReference,
                                  Boolean isValid
  ) {
    this.compositeKey = compositeKey;
    this.blockReference = blockReference;
    this.isValid = isValid;
  }

  public String getCompositeKey() {
    return compositeKey;
  }

  public void setCompositeKey(String compositeKey) {
    this.compositeKey = compositeKey;
  }

  public void setBlockReference(String name) {
    this.blockReference = name;
  }

  public String getBlockReference() {
    return blockReference;
  }

  public Boolean getValid() {
    return isValid;
  }

  public void setValid(Boolean valid) {
    isValid = valid;
  }

  @Override
  public String getId() {
    return getCompositeKey();
  }

  @Override
  public String getName() {
    return getBlockReference();
  }

  @Override
  public Boolean isValid() {
    return getValid();
  }
}
