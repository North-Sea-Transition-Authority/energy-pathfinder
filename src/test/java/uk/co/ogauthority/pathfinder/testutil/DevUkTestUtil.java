package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkFacility;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkField;

public class DevUkTestUtil {

  public static final Integer FACILITY_ID = 1;
  public static final String FACILITY_NAME = "FAC1";

  public static final Integer FIELD_ID = 1;
  public static final String FIELD_NAME = "Field1";
  public static final Integer FIELD_STATUS = 500;


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
    return getDevUkField(FIELD_ID, FIELD_NAME, FIELD_STATUS);
  }

  public static DevUkField getDevUkField(Integer id, String name, Integer status) {
    return new DevUkField(
        id,
        name,
        status
    );
  }

}
