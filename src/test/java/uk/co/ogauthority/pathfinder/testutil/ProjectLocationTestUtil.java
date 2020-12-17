package uk.co.ogauthority.pathfinder.testutil;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkField;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldType;
import uk.co.ogauthority.pathfinder.model.enums.project.UkcsArea;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationForm;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;

public class ProjectLocationTestUtil {
  public static final Integer FIELD_ID = 1;
  public static final String FIELD_NAME = "FieldName";
  public static final Integer FIELD_STATUS = 500;
  public static final DevUkField FIELD = new DevUkField(FIELD_ID, FIELD_NAME, FIELD_STATUS);
  public static final String MANUAL_FIELD_NAME = SearchSelectablePrefix.FREE_TEXT_PREFIX + "ManualFieldName";
  public static final String MANUAL_FIELD_NAME_NO_PREFIX = "ManualFieldName";
  public static final FieldType FIELD_TYPE = FieldType.CARBON_STORAGE;
  public static final Integer WATER_DEPTH = 90;
  public static final Boolean APPROVED_FDP_PLAN = true;
  public static final LocalDate APPROVED_FDP_DATE = LocalDate.now().withDayOfMonth(1);
  public static final Boolean APPROVED_DECOM_PROGRAM = false;
  public static final UkcsArea UKCS_AREA =  UkcsArea.WOS;
  public static final List<String> LICENCE_BLOCKS = List.of("12/34", "12/56");

  public static ProjectLocation getProjectLocation_withManualField(ProjectDetail details) {
    var projectLocation = new ProjectLocation(
        details,
        MANUAL_FIELD_NAME_NO_PREFIX
    );
    setCommonFields(projectLocation);
    return projectLocation;
  }

  public static ProjectLocation getProjectLocation_withField(ProjectDetail details) {
    var projectLocation =  new ProjectLocation(
        details,
        FIELD
    );
    setCommonFields(projectLocation);
    return projectLocation;
  }

  public static ProjectLocationForm getCompletedForm_manualField() {
    var form = new ProjectLocationForm(MANUAL_FIELD_NAME);
    setCommonFields(form);
    return form;
  }

  public static ProjectLocationForm getCompletedForm_withField() {
    var form = new ProjectLocationForm(FIELD_ID.toString());
    setCommonFields(form);
    return form;
  }

  public static ProjectLocationForm getBlankForm() {
    var form = new ProjectLocationForm();
    form.setApprovedFdpDate(new ThreeFieldDateInput(null, null, null));
    form.setApprovedDecomProgramDate(new ThreeFieldDateInput(null, null, null));
    form.setLicenceBlocks(Collections.emptyList());
    return form;
  }

  private static void setCommonFields(ProjectLocation projectLocation) {
    projectLocation.setFieldType(FIELD_TYPE);
    projectLocation.setMaximumWaterDepth(WATER_DEPTH);
    projectLocation.setApprovedFieldDevelopmentPlan(APPROVED_FDP_PLAN);
    projectLocation.setApprovedFdpDate(APPROVED_FDP_DATE);
    projectLocation.setApprovedDecomProgram(APPROVED_DECOM_PROGRAM);
    projectLocation.setApprovedDecomProgramDate(null);
    projectLocation.setUkcsArea(UKCS_AREA);
  }

  private static void setCommonFields(ProjectLocationForm form) {
    form.setFieldType(FIELD_TYPE);
    form.setMaximumWaterDepth(WATER_DEPTH);
    form.setApprovedFieldDevelopmentPlan(APPROVED_FDP_PLAN);
    form.setApprovedFdpDate(new ThreeFieldDateInput(APPROVED_FDP_DATE));
    form.setApprovedDecomProgram(APPROVED_DECOM_PROGRAM);
    form.setApprovedDecomProgramDate(new ThreeFieldDateInput(null, null, null));
    form.setUkcsArea(UKCS_AREA);
    form.setLicenceBlocks(LICENCE_BLOCKS);
  }
}
