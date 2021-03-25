package uk.co.ogauthority.pathfinder.model.entity.portal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Immutable;
import uk.co.ogauthority.pathfinder.model.enums.portal.BlockLocation;
import uk.co.ogauthority.pathfinder.model.licenceblock.SortableLicenceBlock;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;

@Entity
@Table(name = "current_licence_blocks")
@Immutable
public class LicenceBlock implements SearchSelectable, SortableLicenceBlock {

  @Id
  private String compositeKey;

  @Column(name = "plm_id")
  private Integer pedLicenceId;

  @Column(name = "block_ref")
  private String blockReference;

  @Column(name = "block_no")
  private String blockNumber;

  @Column(name = "quadrant_no")
  private String quadrantNumber;

  @Column(name = "suffix")
  private String blockSuffix;

  @Enumerated(EnumType.STRING)
  @Column(name = "location")
  private BlockLocation blockLocation;

  public String getCompositeKey() {
    return compositeKey;
  }

  public Integer getPedLicenceId() {
    return pedLicenceId;
  }

  public String getBlockReference() {
    return blockReference;
  }

  public BlockLocation getBlockLocation() {
    return blockLocation;
  }

  public void setCompositeKey(String compositeKey) {
    this.compositeKey = compositeKey;
  }

  public void setPedLicenceId(Integer plmId) {
    this.pedLicenceId = plmId;
  }

  public void setBlockReference(String blockReference) {
    this.blockReference = blockReference;
  }

  public void setBlockNumber(String blockNumber) {
    this.blockNumber = blockNumber;
  }

  public void setQuadrantNumber(String quadrantNumber) {
    this.quadrantNumber = quadrantNumber;
  }

  public void setBlockSuffix(String blockSuffix) {
    this.blockSuffix = blockSuffix;
  }

  public void setBlockLocation(BlockLocation blockLocation) {
    this.blockLocation = blockLocation;
  }

  @Override
  public String getSelectionId() {
    return getCompositeKey();
  }

  @Override
  public String getSelectionText() {
    return getBlockReference();
  }

  @Override
  public String getBlockNumber() {
    return blockNumber;
  }

  @Override
  public String getQuadrantNumber() {
    return quadrantNumber;
  }

  @Override
  public String getBlockSuffix() {
    return blockSuffix;
  }
}
