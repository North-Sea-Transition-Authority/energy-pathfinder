package uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender;

import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.view.ContactDetailProjectSummaryItem;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;

public class WorkPlanUpcomingTenderView extends ContactDetailProjectSummaryItem {

  private StringWithTag tenderDepartment;

  private String descriptionOfWork;

  private String estimatedTenderDate;

  private String contractBand;

  public WorkPlanUpcomingTenderView(Integer displayOrder,
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

  public String getEstimatedTenderDate() {
    return estimatedTenderDate;
  }

  public void setEstimatedTenderDate(String estimatedTenderDate) {
    this.estimatedTenderDate = estimatedTenderDate;
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
    WorkPlanUpcomingTenderView that = (WorkPlanUpcomingTenderView) o;
    return Objects.equals(tenderDepartment, that.tenderDepartment)
        && Objects.equals(descriptionOfWork, that.descriptionOfWork)
        && Objects.equals(estimatedTenderDate, that.estimatedTenderDate)
        && Objects.equals(contractBand, that.contractBand);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        tenderDepartment,
        descriptionOfWork,
        estimatedTenderDate,
        contractBand
    );
  }
}
