package uk.co.ogauthority.pathfinder.model.form.project.location;

import javax.validation.constraints.NotEmpty;
import uk.co.ogauthority.pathfinder.model.form.validation.FormValidation;

public class ProjectLocationForm implements FormValidation {

  @NotEmpty(message = "Select a field", groups = Full.class)
  private String field;

  public ProjectLocationForm() {
  }

  public ProjectLocationForm(String field) {
    this.field = field;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }
}
