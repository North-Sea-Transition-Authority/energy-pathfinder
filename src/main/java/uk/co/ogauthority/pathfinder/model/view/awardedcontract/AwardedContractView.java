package uk.co.ogauthority.pathfinder.model.view.awardedcontract;

import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.view.ProjectSummaryItem;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.contactdetail.ContactDetailView;

public class AwardedContractView extends ProjectSummaryItem {

  private String contractorName;

  private StringWithTag contractFunction;

  private String descriptionOfWork;

  private String dateAwarded;

  private String contractBand;

  private ContactDetailView contactDetailView;

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

  public ContactDetailView getContactDetailView() {
    return contactDetailView;
  }

  public void setContactDetailView(ContactDetailView contactDetailView) {
    this.contactDetailView = contactDetailView;
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
        && Objects.equals(contractBand, that.contractBand)
        && Objects.equals(contactDetailView, that.contactDetailView);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        contractorName,
        contractFunction,
        descriptionOfWork,
        dateAwarded,
        contractBand,
        contactDetailView
    );
  }
}
