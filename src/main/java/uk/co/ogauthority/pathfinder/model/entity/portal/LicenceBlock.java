package uk.co.ogauthority.pathfinder.model.entity.portal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Immutable;
import uk.co.ogauthority.pathfinder.model.enums.portal.BlockLocation;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;

@Entity
@Table(name = "current_licence_blocks")
@Immutable
public class LicenceBlock implements SearchSelectable {

  @Id
  private String compositeKey;

  private Integer plmId;

  @Column(name = "block_ref")
  private String blockReference;

  @Column(name = "block_no")
  private String blockNumber;

  @Column(name = "quadrant_no")
  private String quadrantNumber;

  private String suffix;

  @Enumerated(EnumType.STRING)
  @Column(name = "location")
  private BlockLocation blockLocation;

  public LicenceBlock() {
  }

  public String getCompositeKey() {
    return compositeKey;
  }

  public Integer getPlmId() {
    return plmId;
  }

  public String getBlockReference() {
    return blockReference;
  }

  public String getBlockNumber() {
    return blockNumber;
  }

  public String getQuadrantNumber() {
    return quadrantNumber;
  }

  public String getSuffix() {
    return suffix;
  }

  public BlockLocation getBlockLocation() {
    return blockLocation;
  }

  public void setCompositeKey(String compositeKey) {
    this.compositeKey = compositeKey;
  }

  public void setPlmId(Integer plmId) {
    this.plmId = plmId;
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

  public void setSuffix(String suffix) {
    this.suffix = suffix;
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
}
