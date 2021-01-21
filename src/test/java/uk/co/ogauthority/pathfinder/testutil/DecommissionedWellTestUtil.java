package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.project.decommissionedwell.DecommissionedWell;
import uk.co.ogauthority.pathfinder.model.enums.project.InputEntryType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedwell.DecommissionedWellType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedwell.WellMechanicalStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedwell.WellOperationalStatus;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedwell.DecommissionedWellForm;

public class DecommissionedWellTestUtil {

  private static final DecommissionedWellType TYPE = DecommissionedWellType.EXPLORATION_AND_APPRAISAL;
  private static final Integer NUMBER_TO_DECOMMISSION = 5;
  private static final Quarter PA_DATE_QUARTER = Quarter.Q1;
  private static final Integer PA_DATE_YEAR = 2020;
  private static final InputEntryType PA_DATE_TYPE = InputEntryType.ACTUAL;
  private static final WellOperationalStatus OPERATIONAL_STATUS = WellOperationalStatus.SUSPENDED;
  private static final WellMechanicalStatus MECHANICAL_STATUS = WellMechanicalStatus.ABANDONED_PHASE_2;

  private DecommissionedWellTestUtil() {
    throw new IllegalStateException("DecommissionedWellTestUtil is a utility class and should not be instantiated");
  }

  public static DecommissionedWellForm getCompletedForm() {
    var form = new DecommissionedWellForm();
    form.setType(TYPE.getSelectionId());
    form.setNumberToBeDecommissioned(NUMBER_TO_DECOMMISSION);
    form.setPlugAbandonmentDate(new QuarterYearInput(PA_DATE_QUARTER, String.valueOf(PA_DATE_YEAR)));
    form.setPlugAbandonmentDateType(PA_DATE_TYPE);
    form.setOperationalStatus(OPERATIONAL_STATUS.getSelectionId());
    form.setMechanicalStatus(MECHANICAL_STATUS.getSelectionId());
    return form;
  }

  public static DecommissionedWell createDecommissionedWell() {
    var decommissionedWell = new DecommissionedWell();
    decommissionedWell.setProjectDetail(ProjectUtil.getProjectDetails());
    decommissionedWell.setNumberToBeDecommissioned(NUMBER_TO_DECOMMISSION);
    decommissionedWell.setPlugAbandonmentDateQuarter(PA_DATE_QUARTER);
    decommissionedWell.setPlugAbandonmentDateYear(PA_DATE_YEAR);
    decommissionedWell.setPlugAbandonmentDateType(PA_DATE_TYPE);
    decommissionedWell.setType(TYPE);
    decommissionedWell.setManualType(null);
    decommissionedWell.setOperationalStatus(OPERATIONAL_STATUS);
    decommissionedWell.setManualOperationalStatus(null);
    decommissionedWell.setMechanicalStatus(MECHANICAL_STATUS);
    decommissionedWell.setManualMechanicalStatus(null);
    return decommissionedWell;
  }
}
