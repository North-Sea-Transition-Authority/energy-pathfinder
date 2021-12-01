package uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender;

import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.view.ContactDetailProjectSummaryItem;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;

public class ForwardWorkPlanUpcomingTenderView extends ContactDetailProjectSummaryItem {

  private StringWithTag tenderDepartment;

  private String descriptionOfWork;

  private String estimatedTenderStartDate;

  private String contractBand;

  private String contractLength;

  public ForwardWorkPlanUpcomingTenderView(Integer displayOrder,
                                           Integer id,
                                           Integer projectId) {
    this.displayOrder = displayOrder;
    this.id = id;
    this.projectId = projectId;
  }

  public StringWithTag getTenderDepartment() {
    return tenderDepartment;
  }

  public void setTenderDepartment(StringWithTag tenderDepartment) {
    this.tenderDepartment = tenderDepartment;
  }

  public String getDescriptionOfWork() {
    return descriptionOfWork;
  }

  public void setDescriptionOfWork(String descriptionOfWork) {
    this.descriptionOfWork = descriptionOfWork;
  }

  public String getEstimatedTenderStartDate() {
    return estimatedTenderStartDate;
  }

  public void setEstimatedTenderStartDate(
      String estimatedTenderStartDate) {
    this.estimatedTenderStartDate = estimatedTenderStartDate;
  }

  public String getContractBand() {
    return contractBand;
  }

  public void setContractBand(String contractBand) {
    this.contractBand = contractBand;
  }

  public String getContractLength() {
    return contractLength;
  }

  public void setContractLength(String contractLength) {
    this.contractLength = contractLength;
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
    ForwardWorkPlanUpcomingTenderView that = (ForwardWorkPlanUpcomingTenderView) o;
    return Objects.equals(tenderDepartment, that.tenderDepartment)
        && Objects.equals(descriptionOfWork, that.descriptionOfWork)
        && Objects.equals(estimatedTenderStartDate, that.estimatedTenderStartDate)
        && Objects.equals(contractBand, that.contractBand)
        && Objects.equals(contractLength, that.contractLength);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        tenderDepartment,
        descriptionOfWork,
        estimatedTenderStartDate,
        contractBand,
        contractLength
    );
  }
}
