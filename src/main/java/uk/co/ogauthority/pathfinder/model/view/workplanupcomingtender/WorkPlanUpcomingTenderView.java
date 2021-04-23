package uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender;

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
}
