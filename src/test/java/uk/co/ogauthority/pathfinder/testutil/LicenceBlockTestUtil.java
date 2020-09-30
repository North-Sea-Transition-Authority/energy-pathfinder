package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.portal.LicenceBlock;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.model.enums.portal.BlockLocation;
import uk.co.ogauthority.pathfinder.model.view.projectlocation.ProjectLocationBlockView;

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
        isValid
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

  private static void setCommonFields(LicenceBlock block) {
    block.setPedLicenceId(PLM_ID);
    block.setBlockNumber(BLOCK_NUMBER);
    block.setQuadrantNumber(QUADRANT_NUMBER);
    block.setSuffix(BLOCK_SUFFIX);
    block.setBlockLocation(BLOCK_LOCATION);
  }
}
