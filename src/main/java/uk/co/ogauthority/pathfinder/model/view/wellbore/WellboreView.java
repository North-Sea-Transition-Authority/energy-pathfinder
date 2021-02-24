package uk.co.ogauthority.pathfinder.model.view.wellbore;

import uk.co.ogauthority.pathfinder.model.addtolist.AddToListItem;
import uk.co.ogauthority.pathfinder.model.entity.wellbore.Wellbore;
import uk.co.ogauthority.pathfinder.model.wellbore.SortableWellbore;

public class WellboreView implements AddToListItem, SortableWellbore {

  private Integer id;

  private String registrationNo;

  private final String quadrantNumber;

  private final String blockNumber;

  private final String blockSuffix;

  private final String platformLetter;

  private final String drillingSeqNumber;

  private final String wellSuffix;

  private Boolean valid;

  public WellboreView(Wellbore wellbore, Boolean valid) {
    this.id = wellbore.getId();
    this.registrationNo = wellbore.getRegistrationNo();
    this.quadrantNumber = wellbore.getQuadrantNumber();
    this.blockNumber = wellbore.getBlockNumber();
    this.blockSuffix = wellbore.getBlockSuffix();
    this.platformLetter = wellbore.getPlatformLetter();
    this.drillingSeqNumber = wellbore.getDrillingSeqNumber();
    this.wellSuffix = wellbore.getWellSuffix();
    this.valid = valid;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setRegistrationNo(String registrationNo) {
    this.registrationNo = registrationNo;
  }

  public void setValid(Boolean valid) {
    this.valid = valid;
  }

  @Override
  public String getId() {
    return String.valueOf(id);
  }

  @Override
  public String getName() {
    return registrationNo;
  }

  @Override
  public Boolean isValid() {
    return valid;
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
}
