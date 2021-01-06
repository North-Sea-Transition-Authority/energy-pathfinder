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
import uk.co.ogauthority.pathfinder.model.licenceblock.SortableLicenceBlock;
import uk.co.ogauthority.pathfinder.service.entityduplication.ChildEntity;

@Entity
@Table(name = "project_location_blocks")
public class ProjectLocationBlock implements ChildEntity<Integer, ProjectLocation>, SortableLicenceBlock {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "project_location_id")
  private ProjectLocation projectLocation;

  @Column(name = "plm_id")
  private Integer pedLicenceId;

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
                              Integer pedLicenceId,
                              String blockReference,
                              String blockNumber,
                              String quadrantNumber,
                              String blockSuffix,
                              BlockLocation blockLocation
  ) {
    this.projectLocation = projectLocation;
    this.pedLicenceId = pedLicenceId;
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

  public Integer getPedLicenceId() {
    return pedLicenceId;
  }

  public void setPedLicenceId(Integer plmId) {
    this.pedLicenceId = plmId;
  }

  public String getBlockReference() {
    return blockReference;
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
        getPedLicenceId() != null ? getPedLicenceId().toString() : ""
    );
  }

  @Override
  public void clearId() {
    this.id = null;
  }

  @Override
  public void setParent(ProjectLocation parentEntity) {
    setProjectLocation(parentEntity);
  }

  @Override
  public ProjectLocation getParent() {
    return getProjectLocation();
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
