package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkFacility;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkField;
import uk.co.ogauthority.pathfinder.model.enums.project.UkcsArea;

public class DevUkTestUtil {

  public static final Integer FACILITY_ID = 1;
  public static final String FACILITY_NAME = "FAC1";

  public static final Integer FIELD_ID = 1;
  public static final String FIELD_NAME = "Field1";
  public static final Integer FIELD_STATUS = 500;
  public static final UkcsArea FIELD_UKCS_AREA = UkcsArea.CNS;

  public static DevUkFacility getDevUkFacility() {
    return getDevUkFacility(
        FACILITY_ID,
        FACILITY_NAME
    );
  }

  public static DevUkFacility getDevUkFacility(Integer id, String name) {
    return new DevUkFacility(
        id,
        name
    );
  }
  public static DevUkField getDevUkField() {
    return getDevUkField(FIELD_ID, FIELD_NAME, FIELD_STATUS, FIELD_UKCS_AREA);
  }

  public static DevUkField getDevUkField(Integer id, String name, Integer status, UkcsArea ukcsArea) {
    return new DevUkField(
        id,
        name,
        status,
        ukcsArea
    );
  }

}
