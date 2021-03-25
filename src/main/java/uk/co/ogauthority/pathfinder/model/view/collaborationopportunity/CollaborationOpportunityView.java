package uk.co.ogauthority.pathfinder.model.view.collaborationopportunity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.view.ContactDetailProjectSummaryItem;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;

public class CollaborationOpportunityView extends ContactDetailProjectSummaryItem {

  private StringWithTag function;

  private String descriptionOfWork;

  private String urgentResponseNeeded;

  private List<UploadedFileView> uploadedFileViews;

  public CollaborationOpportunityView() {
    this.uploadedFileViews = new ArrayList<>();
  }

  public CollaborationOpportunityView(Integer displayOrder,
                                      Integer id,
                                      Integer projectId
  ) {
    this.displayOrder = displayOrder;
    this.id = id;
    this.projectId = projectId;
  }

  public StringWithTag getFunction() {
    return function;
  }

  public void setFunction(StringWithTag function) {
    this.function = function;
  }

  public String getDescriptionOfWork() {
    return descriptionOfWork;
  }

  public void setDescriptionOfWork(String descriptionOfWork) {
    this.descriptionOfWork = descriptionOfWork;
  }

  public String getUrgentResponseNeeded() {
    return urgentResponseNeeded;
  }

  public void setUrgentResponseNeeded(String urgentResponseNeeded) {
    this.urgentResponseNeeded = urgentResponseNeeded;
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
    CollaborationOpportunityView that = (CollaborationOpportunityView) o;
    return Objects.equals(function, that.function)
        && Objects.equals(descriptionOfWork, that.descriptionOfWork)
        && Objects.equals(urgentResponseNeeded, that.urgentResponseNeeded)
        && Objects.equals(uploadedFileViews, that.uploadedFileViews);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        function,
        descriptionOfWork,
        urgentResponseNeeded,
        uploadedFileViews
    );
  }
}
