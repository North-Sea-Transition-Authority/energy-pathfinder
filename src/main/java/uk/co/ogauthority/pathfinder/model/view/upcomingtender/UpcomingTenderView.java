package uk.co.ogauthority.pathfinder.model.view.upcomingtender;

import java.util.List;
import uk.co.ogauthority.pathfinder.model.view.ProjectSummaryItem;
import uk.co.ogauthority.pathfinder.model.view.contactdetail.ContactDetailView;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;

public class UpcomingTenderView extends ProjectSummaryItem {

  private String tenderFunction;

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

  public String getTenderFunction() {
    return tenderFunction;
  }

  public void setTenderFunction(String tenderFunction) {
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
}
