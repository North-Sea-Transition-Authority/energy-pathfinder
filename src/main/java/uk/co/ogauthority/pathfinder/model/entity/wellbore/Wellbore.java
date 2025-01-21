package uk.co.ogauthority.pathfinder.model.entity.wellbore;

import com.google.common.annotations.VisibleForTesting;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;
import uk.co.ogauthority.pathfinder.model.wellbore.SortableWellbore;

@Entity
@Table(name = "wellbores")
@Immutable
public class Wellbore implements SearchSelectable, SortableWellbore {

  @Id
  private Integer id;

  private String registrationNo;

  @Column(name = "quadrant_no")
  private String quadrantNumber;

  @Column(name = "block_no")
  private String blockNumber;

  private String blockSuffix;

  private String platformLetter;

  @Column(name = "drilling_seq_no")
  private String drillingSeqNumber;

  private String wellSuffix;

  @Column(name = "mechanical_status")
  private String mechanicalStatus;

  public Wellbore() {}

  @VisibleForTesting
  public Wellbore(Integer id,
                  String registrationNo,
                  String quadrantNumber,
                  String blockNumber,
                  String blockSuffix,
                  String platformLetter,
                  String drillingSeqNumber,
                  String wellSuffix) {
    this.id = id;
    this.registrationNo = registrationNo;
    this.quadrantNumber = quadrantNumber;
    this.blockNumber = blockNumber;
    this.blockSuffix = blockSuffix;
    this.platformLetter = platformLetter;
    this.drillingSeqNumber = drillingSeqNumber;
    this.wellSuffix = wellSuffix;
  }

  public Integer getId() {
    return id;
  }

  public String getRegistrationNo() {
    return registrationNo;
  }

  @Override
  public String getSelectionId() {
    return String.valueOf(id);
  }

  @Override
  public String getSelectionText() {
    return getRegistrationNo();
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
  public String getPlatformLetter() {
    return platformLetter;
  }

  @Override
  public String getDrillingSeqNumber() {
    return drillingSeqNumber;
  }

  @Override
  public String getWellSuffix() {
    return wellSuffix;
  }

  public String getMechanicalStatus() {
    return mechanicalStatus;
  }
}
