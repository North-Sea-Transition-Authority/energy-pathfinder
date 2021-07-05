package uk.co.ogauthority.pathfinder.model.form.project.campaigninformation;

import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;

public final class CampaignInformationValidationHint {

  protected static final String PROJECT_SELECTOR_FIELD_NAME = "projectSelect";

  private final ValidationType validationType;
  private final ProjectDetail projectDetail;

  public CampaignInformationValidationHint(ValidationType validationType,
                                           ProjectDetail projectDetail) {
    this.validationType = validationType;
    this.projectDetail = projectDetail;
  }

  public String getProjectSelectorErrorMessage() {
    return String.format("Select at least one %s", projectDetail.getProjectType().getLowercaseDisplayName());
  }

  public String getProjectSelectorErrorCode() {
    return String.format("%s%s", getProjectSelectorFieldName(), FieldValidationErrorCodes.MIN_LENGTH_NOT_MET);
  }

  public String getProjectSelectorFieldName() {
    return PROJECT_SELECTOR_FIELD_NAME;
  }

  public ValidationType getValidationType() {
    return validationType;
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }

    if (!(obj instanceof CampaignInformationValidationHint)) {
      return false;
    }

    CampaignInformationValidationHint that = (CampaignInformationValidationHint) obj;
    return Objects.equals(validationType, that.validationType)
        && Objects.equals(projectDetail, that.projectDetail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(validationType, projectDetail);
  }
}
