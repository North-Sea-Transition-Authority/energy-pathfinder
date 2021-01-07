package uk.co.ogauthority.pathfinder.model.view.projectlocation;

import uk.co.ogauthority.pathfinder.model.addtolist.AddToListItem;
import uk.co.ogauthority.pathfinder.model.entity.portal.LicenceBlock;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.model.licenceblock.SortableLicenceBlock;

public class ProjectLocationBlockView implements AddToListItem, SortableLicenceBlock {

  private String compositeKey;

  private String blockReference;

  private Boolean isValid;

  private final String quadrantNumber;

  private final String blockNumber;

  private final String blockSuffix;

  public ProjectLocationBlockView(ProjectLocationBlock projectLocationBlock, Boolean isValid) {
    this(projectLocationBlock.getCompositeKey(),
        projectLocationBlock.getBlockReference(),
        isValid,
        projectLocationBlock.getQuadrantNumber(),
        projectLocationBlock.getBlockNumber(),
        projectLocationBlock.getBlockSuffix());
  }

  public ProjectLocationBlockView(LicenceBlock licenceBlock, Boolean isValid) {
    this(licenceBlock.getCompositeKey(),
        licenceBlock.getBlockReference(),
        isValid,
        licenceBlock.getQuadrantNumber(),
        licenceBlock.getBlockNumber(),
        licenceBlock.getBlockSuffix());
  }

  public ProjectLocationBlockView(String compositeKey,
                                  String blockReference,
                                  Boolean isValid,
                                  String quadrantNumber,
                                  String blockNumber,
                                  String blockSuffix
  ) {
    this.compositeKey = compositeKey;
    this.blockReference = blockReference;
    this.isValid = isValid;
    this.quadrantNumber = quadrantNumber;
    this.blockNumber = blockNumber;
    this.blockSuffix = blockSuffix;
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
  public String getQuadrantNumber() {
    return quadrantNumber;
  }

  @Override
  public String getBlockNumber() {
    return blockNumber;
  }

  @Override
  public String getBlockSuffix() {
    return blockSuffix;
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
