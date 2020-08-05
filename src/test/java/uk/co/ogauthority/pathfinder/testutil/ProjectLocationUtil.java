package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkField;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationForm;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;

public class ProjectLocationUtil {
  public static final Integer FIELD_ID = 1;
  public static final String FIELD_NAME = "FieldName";
  public static final Integer FIELD_STATUS = 500;
  public static final DevUkField FIELD = new DevUkField(FIELD_ID, FIELD_NAME, FIELD_STATUS);
  public static final String MANUAL_FIELD_NAME = SearchSelectable.FREE_TEXT_PREFIX + "ManualFieldName";
  public static final String MANUAL_FIELD_NAME_NO_PREFIX = "ManualFieldName";

  public static ProjectLocation getProjectLocation_withManualField(ProjectDetail details) {
    return new ProjectLocation(
        details,
        MANUAL_FIELD_NAME
    );
  }

  public static ProjectLocation getProjectLocation_withField(ProjectDetail details) {
    return new ProjectLocation(
        details,
        FIELD
    );
  }

  public static ProjectLocationForm getCompletedForm_manualField() {
    return new ProjectLocationForm(MANUAL_FIELD_NAME);
  }

  public static ProjectLocationForm getCompletedForm_withField() {
    return new ProjectLocationForm(FIELD_ID.toString());
  }

}
