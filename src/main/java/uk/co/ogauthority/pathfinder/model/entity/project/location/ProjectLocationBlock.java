package uk.co.ogauthority.pathfinder.model.entity.project.location;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.enums.portal.BlockLocation;

@Entity
@Table(name = "project_location_blocks")
public class ProjectLocationBlock {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "project_location_id")
  private ProjectLocation projectLocation;

  private Integer plmId;

  @Column(name = "block_ref")
  private String blockReference;

  @Column(name = "block_no")
  private String blockNumber;

  @Column(name = "quadrant_no")
  private String quadrantNumber;

  @Column(name = "block_suffix")
  private String blockSuffix;

  @Enumerated(EnumType.STRING)
  @Column(name = "location")
  private BlockLocation blockLocation;

  public ProjectLocationBlock() {
  }

  public ProjectLocationBlock(ProjectLocation projectLocation,
                              Integer plmId,
                              String blockReference,
                              String blockNumber,
                              String quadrantNumber,
                              String blockSuffix,
                              BlockLocation blockLocation
  ) {
    this.projectLocation = projectLocation;
    this.plmId = plmId;
    this.blockReference = blockReference;
    this.blockNumber = blockNumber;
    this.quadrantNumber = quadrantNumber;
    this.blockSuffix = blockSuffix;
    this.blockLocation = blockLocation;
  }

  public Integer getId() {
    return id;
  }

  public ProjectLocation getProjectLocation() {
    return projectLocation;
  }

  public void setProjectLocation(ProjectLocation projectLocation) {
    this.projectLocation = projectLocation;
  }

  public Integer getPlmId() {
    return plmId;
  }

  public void setPlmId(Integer plmId) {
    this.plmId = plmId;
  }

  public String getBlockReference() {
    return blockReference;
  }

  public void setBlockReference(String blockReference) {
    this.blockReference = blockReference;
  }

  public String getBlockNumber() {
    return blockNumber;
  }

  public void setBlockNumber(String blockNumber) {
    this.blockNumber = blockNumber;
  }

  public String getQuadrantNumber() {
    return quadrantNumber;
  }

  public void setQuadrantNumber(String quadrantNumber) {
    this.quadrantNumber = quadrantNumber;
  }

  public String getBlockSuffix() {
    return blockSuffix;
  }

  public void setBlockSuffix(String suffix) {
    this.blockSuffix = suffix;
  }

  public BlockLocation getBlockLocation() {
    return blockLocation;
  }

  public void setBlockLocation(BlockLocation blockLocation) {
    this.blockLocation = blockLocation;
  }

  /**
   * build up the composite key used by the {@link uk.co.ogauthority.pathfinder.model.entity.portal.LicenceBlock} .
   * @return composite key for the selected block
   */
  public String getCompositeKey() {
    return String.join(
        "",
        getBlockReference(),
        getQuadrantNumber(),
        getBlockNumber(),
        getBlockSuffix() != null ? getBlockSuffix() : "",
        getPlmId().toString()
    );
  }
}
