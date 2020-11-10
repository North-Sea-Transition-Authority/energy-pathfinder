package uk.co.ogauthority.pathfinder.model.view.upcomingtender;

import java.util.List;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.view.ProjectSummaryItem;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.contactdetail.ContactDetailView;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;

public class UpcomingTenderView extends ProjectSummaryItem {

  private StringWithTag tenderFunction;

  private String descriptionOfWork;

  private String estimatedTenderDate;

  private String contractBand;

  private ContactDetailView contactDetailView;

  private List<UploadedFileView> uploadedFileViews;

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

  public ContactDetailView getContactDetailView() {
    return contactDetailView;
  }

  public void setContactDetailView(ContactDetailView contactDetailView) {
    this.contactDetailView = contactDetailView;
  }

  public List<UploadedFileView> getUploadedFileViews() {
    return uploadedFileViews;
  }

  public void setUploadedFileViews(
      List<UploadedFileView> uploadedFileViews) {
    this.uploadedFileViews = uploadedFileViews;
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
        && Objects.equals(contactDetailView, that.contactDetailView)
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
        contactDetailView,
        uploadedFileViews
    );
  }
}
