package uk.co.ogauthority.pathfinder.model.view.upcomingtender;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.view.ContactDetailProjectSummaryItem;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;

public class UpcomingTenderView extends ContactDetailProjectSummaryItem {

  private StringWithTag tenderFunction;

  private String descriptionOfWork;

  private String estimatedTenderDate;

  private String contractBand;

  private List<UploadedFileView> uploadedFileViews;

  private String addedByOrganisationGroup;

  public UpcomingTenderView() {
    this.uploadedFileViews = new ArrayList<>();
  }

  public UpcomingTenderView(
      Integer displayOrder,
      Integer id,
      Integer projectId
  ) {
    this.displayOrder = displayOrder;
    this.id = id;
    this.projectId = projectId;
  }

  public StringWithTag getTenderFunction() {
    return tenderFunction;
  }

  public void setTenderFunction(StringWithTag tenderFunction) {
    this.tenderFunction = tenderFunction;
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

  public List<UploadedFileView> getUploadedFileViews() {
    return uploadedFileViews;
  }

  public void setUploadedFileViews(
      List<UploadedFileView> uploadedFileViews) {
    this.uploadedFileViews = uploadedFileViews;
  }

  public String getAddedByOrganisationGroup() {
    return addedByOrganisationGroup;
  }

  public void setAddedByOrganisationGroup(String addedByOrganisationGroup) {
    this.addedByOrganisationGroup = addedByOrganisationGroup;
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
    UpcomingTenderView that = (UpcomingTenderView) o;
    return Objects.equals(tenderFunction, that.tenderFunction)
        && Objects.equals(descriptionOfWork, that.descriptionOfWork)
        && Objects.equals(estimatedTenderDate, that.estimatedTenderDate)
        && Objects.equals(contractBand, that.contractBand)
        && Objects.equals(uploadedFileViews, that.uploadedFileViews);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        tenderFunction,
        descriptionOfWork,
        estimatedTenderDate,
        contractBand,
        uploadedFileViews
    );
  }
}
