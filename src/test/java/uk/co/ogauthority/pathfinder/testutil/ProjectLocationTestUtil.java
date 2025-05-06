package uk.co.ogauthority.pathfinder.testutil;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import uk.co.fivium.formlibrary.input.CoordinateInputLatitudeHemisphere;
import uk.co.fivium.formlibrary.input.CoordinateInputLongitudeHemisphere;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkField;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldType;
import uk.co.ogauthority.pathfinder.model.enums.project.UkcsArea;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationForm;

public class ProjectLocationTestUtil {

  public static final Integer CENTRE_OF_INTEREST_LATITUDE_DEGREES = 51;
  public static final Integer CENTRE_OF_INTEREST_LATITUDE_MINUTES = 30;
  public static final Double CENTRE_OF_INTEREST_LATITUDE_SECONDS = 32.4;
  public static final String CENTRE_OF_INTEREST_LATITUDE_HEMISPHERE = CoordinateInputLatitudeHemisphere.NORTH.name();
  public static final Integer CENTRE_OF_INTEREST_LONGITUDE_DEGREES = 0;
  public static final Integer CENTRE_OF_INTEREST_LONGITUDE_MINUTES = 7;
  public static final Double CENTRE_OF_INTEREST_LONGITUDE_SECONDS = 19.2;
  public static final String CENTRE_OF_INTEREST_LONGITUDE_HEMISPHERE = CoordinateInputLongitudeHemisphere.WEST.name();
  public static final Integer FIELD_ID = 1;
  public static final String FIELD_NAME = "FieldName";
  public static final Integer FIELD_STATUS = 500;
  public static final UkcsArea FIELD_UKCS_AREA = UkcsArea.CNS;
  public static final DevUkField FIELD = new DevUkField(FIELD_ID, FIELD_NAME, FIELD_STATUS, FIELD_UKCS_AREA);
  public static final FieldType FIELD_TYPE = FieldType.CARBON_STORAGE;
  public static final Integer WATER_DEPTH = 90;
  public static final Boolean APPROVED_FDP_PLAN = true;
  public static final LocalDate APPROVED_FDP_DATE = LocalDate.now().withDayOfMonth(1);
  public static final Boolean APPROVED_DECOM_PROGRAM = false;
  public static final List<String> LICENCE_BLOCKS = List.of("12/34", "12/56");

  public static ProjectLocation getProjectLocation(ProjectDetail details) {
    var projectLocation =  new ProjectLocation(
        details,
        FIELD
    );
    setCommonFields(projectLocation);
    return projectLocation;
  }

  public static ProjectLocationForm getCompletedForm() {
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
    projectLocation.setCentreOfInterestLatitudeDegrees(CENTRE_OF_INTEREST_LATITUDE_DEGREES);
    projectLocation.setCentreOfInterestLatitudeMinutes(CENTRE_OF_INTEREST_LATITUDE_MINUTES);
    projectLocation.setCentreOfInterestLatitudeSeconds(CENTRE_OF_INTEREST_LATITUDE_SECONDS);
    projectLocation.setCentreOfInterestLatitudeHemisphere(CENTRE_OF_INTEREST_LATITUDE_HEMISPHERE);

    projectLocation.setCentreOfInterestLongitudeDegrees(CENTRE_OF_INTEREST_LONGITUDE_DEGREES);
    projectLocation.setCentreOfInterestLongitudeMinutes(CENTRE_OF_INTEREST_LONGITUDE_MINUTES);
    projectLocation.setCentreOfInterestLongitudeSeconds(CENTRE_OF_INTEREST_LONGITUDE_SECONDS);
    projectLocation.setCentreOfInterestLongitudeHemisphere(CENTRE_OF_INTEREST_LONGITUDE_HEMISPHERE);

    projectLocation.setFieldType(FIELD_TYPE);
    projectLocation.setMaximumWaterDepth(WATER_DEPTH);
    projectLocation.setApprovedFieldDevelopmentPlan(APPROVED_FDP_PLAN);
    projectLocation.setApprovedFdpDate(APPROVED_FDP_DATE);
    projectLocation.setApprovedDecomProgram(APPROVED_DECOM_PROGRAM);
    projectLocation.setApprovedDecomProgramDate(null);
  }

  private static void setCommonFields(ProjectLocationForm form) {
    var centreOfInterestLatitude = form.getCentreOfInterestLatitude();
    centreOfInterestLatitude.getDegreesInput().setInteger(CENTRE_OF_INTEREST_LATITUDE_DEGREES);
    centreOfInterestLatitude.getMinutesInput().setInteger(CENTRE_OF_INTEREST_LATITUDE_MINUTES);
    centreOfInterestLatitude.getSecondsInput().setInputValue(Double.toString(CENTRE_OF_INTEREST_LATITUDE_SECONDS));

    var centreOfInterestLongitude = form.getCentreOfInterestLongitude();
    centreOfInterestLongitude.getDegreesInput().setInteger(CENTRE_OF_INTEREST_LONGITUDE_DEGREES);
    centreOfInterestLongitude.getMinutesInput().setInteger(CENTRE_OF_INTEREST_LONGITUDE_MINUTES);
    centreOfInterestLongitude.getSecondsInput().setInputValue(Double.toString(CENTRE_OF_INTEREST_LONGITUDE_SECONDS));
    centreOfInterestLongitude.getHemisphereInput().setInputValue(CENTRE_OF_INTEREST_LONGITUDE_HEMISPHERE);

    form.setFieldType(FIELD_TYPE);
    form.setMaximumWaterDepth(WATER_DEPTH);
    form.setApprovedFieldDevelopmentPlan(APPROVED_FDP_PLAN);
    form.setApprovedFdpDate(new ThreeFieldDateInput(APPROVED_FDP_DATE));
    form.setApprovedDecomProgram(APPROVED_DECOM_PROGRAM);
    form.setApprovedDecomProgramDate(new ThreeFieldDateInput(null, null, null));
    form.setLicenceBlocks(LICENCE_BLOCKS);
  }
}
