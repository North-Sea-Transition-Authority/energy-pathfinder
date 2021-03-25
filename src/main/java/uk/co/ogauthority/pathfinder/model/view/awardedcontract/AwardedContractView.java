package uk.co.ogauthority.pathfinder.model.view.awardedcontract;

import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.view.ContactDetailProjectSummaryItem;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;

public class AwardedContractView extends ContactDetailProjectSummaryItem {

  private String contractorName;

  private StringWithTag contractFunction;

  private String descriptionOfWork;

  private String dateAwarded;

  private String contractBand;

  public String getContractorName() {
    return contractorName;
  }

  public void setContractorName(String contractorName) {
    this.contractorName = contractorName;
  }

  public StringWithTag getContractFunction() {
    return contractFunction;
  }

  public void setContractFunction(StringWithTag contractFunction) {
    this.contractFunction = contractFunction;
  }

  public String getDescriptionOfWork() {
    return descriptionOfWork;
  }

  public void setDescriptionOfWork(String descriptionOfWork) {
    this.descriptionOfWork = descriptionOfWork;
  }

  public String getDateAwarded() {
    return dateAwarded;
  }

  public void setDateAwarded(String dateAwarded) {
    this.dateAwarded = dateAwarded;
  }

  public String getContractBand() {
    return contractBand;
  }

  public void setContractBand(String contractBand) {
    this.contractBand = contractBand;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    AwardedContractView that = (AwardedContractView) o;
    return Objects.equals(contractorName, that.contractorName)
        && Objects.equals(contractFunction, that.contractFunction)
        && Objects.equals(descriptionOfWork, that.descriptionOfWork)
        && Objects.equals(dateAwarded, that.dateAwarded)
        && Objects.equals(contractBand, that.contractBand);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        contractorName,
        contractFunction,
        descriptionOfWork,
        dateAwarded,
        contractBand
    );
  }
}
