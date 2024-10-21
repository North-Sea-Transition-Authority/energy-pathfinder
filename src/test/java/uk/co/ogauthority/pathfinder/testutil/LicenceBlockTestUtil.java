package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.portal.LicenceBlock;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.model.enums.portal.BlockLocation;
import uk.co.ogauthority.pathfinder.model.view.location.ProjectLocationBlockView;

public class LicenceBlockTestUtil {

  public static final Integer PLM_ID = 1;

  public static final String BLOCK_REFERENCE = "12/34/a";

  public static final String BLOCK_NUMBER = "12";

  public static final String QUADRANT_NUMBER = "34";

  public static final String BLOCK_SUFFIX = "a";

  public static  String COMPOSITE_KEY =  QUADRANT_NUMBER + BLOCK_NUMBER + BLOCK_SUFFIX + PLM_ID;

  public static final BlockLocation BLOCK_LOCATION = BlockLocation.OFFSHORE;


  public static LicenceBlock getBlock() {
    return getBlock(BLOCK_REFERENCE);
  }

  public static LicenceBlock getBlock(String blockReference,
                                      String quadrantNumber,
                                      String blockNumber,
                                      String blockSuffix) {
    var block = new LicenceBlock();
    block.setBlockReference(blockReference);
    block.setCompositeKey(blockReference + quadrantNumber + blockNumber + blockSuffix + PLM_ID);
    block.setPedLicenceId(PLM_ID);
    block.setBlockNumber(blockNumber);
    block.setQuadrantNumber(quadrantNumber);
    block.setBlockSuffix(blockSuffix);
    block.setBlockLocation(BLOCK_LOCATION);
    return block;
  }

  public static LicenceBlock getBlock(String blockReference) {
    var block = new LicenceBlock();
    block.setBlockReference(blockReference);
    block.setCompositeKey(blockReference + COMPOSITE_KEY);
    setCommonFields(block);
    return block;
  }

  public static ProjectLocationBlockView getBlockView(Boolean isValid) {
    return new ProjectLocationBlockView(
        COMPOSITE_KEY,
        BLOCK_REFERENCE,
        isValid,
        QUADRANT_NUMBER,
        BLOCK_NUMBER,
        BLOCK_SUFFIX
    );
  }

  public static ProjectLocationBlock getProjectLocationBlock(ProjectLocation location, String blockReference) {
    return new ProjectLocationBlock(
        location,
        PLM_ID,
        blockReference,
        BLOCK_NUMBER,
        QUADRANT_NUMBER,
        BLOCK_SUFFIX,
        BLOCK_LOCATION
    );
  }

  public static ProjectLocationBlock getProjectLocationBlock(
      ProjectLocation location,
      String blockReference,
      String quadrantNumber,
      String blockNumber,
      String blockSuffix
  ) {
    return new ProjectLocationBlock(
        location,
        PLM_ID,
        blockReference,
        blockNumber,
        quadrantNumber,
        blockSuffix,
        BLOCK_LOCATION
    );
  }

  public static ProjectLocationBlock getProjectLocationBlock(ProjectLocation location, LicenceBlock licenceBlock) {
    return new ProjectLocationBlock(
        location,
        PLM_ID,
        licenceBlock.getBlockReference(),
        licenceBlock.getBlockNumber(),
        licenceBlock.getQuadrantNumber(),
        licenceBlock.getBlockSuffix(),
        BLOCK_LOCATION
    );
  }

  private static void setCommonFields(LicenceBlock block) {
    block.setPedLicenceId(PLM_ID);
    block.setBlockNumber(BLOCK_NUMBER);
    block.setQuadrantNumber(QUADRANT_NUMBER);
    block.setBlockSuffix(BLOCK_SUFFIX);
    block.setBlockLocation(BLOCK_LOCATION);
  }
}
