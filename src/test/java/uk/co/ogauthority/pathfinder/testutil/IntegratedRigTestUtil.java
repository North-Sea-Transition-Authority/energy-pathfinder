package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkFacility;
import uk.co.ogauthority.pathfinder.model.entity.project.integratedrig.IntegratedRig;
import uk.co.ogauthority.pathfinder.model.enums.project.integratedrig.IntegratedRigIntentionToReactivate;
import uk.co.ogauthority.pathfinder.model.enums.project.integratedrig.IntegratedRigStatus;
import uk.co.ogauthority.pathfinder.model.form.project.integratedrig.IntegratedRigForm;
import uk.co.ogauthority.pathfinder.model.view.integratedrig.IntegratedRigView;
import uk.co.ogauthority.pathfinder.model.view.integratedrig.IntegratedRigViewUtil;

public class IntegratedRigTestUtil {

  private static final String NAME = "name";
  private static final IntegratedRigStatus STATUS = IntegratedRigStatus.IN_USE;
  private static final IntegratedRigIntentionToReactivate INTENTION_TO_REACTIVATE = IntegratedRigIntentionToReactivate.YES;

  private IntegratedRigTestUtil() {
    throw new IllegalStateException("IntegratedRigTestUtil is a utility class and should not be instantiated");
  }

  public static IntegratedRig createIntegratedRig_withDevUkFacility() {
    return createIntegratedRig(DevUkTestUtil.getDevUkFacility(), null);
  }

  public static IntegratedRig createIntegratedRig_withManualFacility() {
    return createIntegratedRig(null, "manual facility name");
  }

  public static IntegratedRigForm createIntegratedRigForm() {
    var form = new IntegratedRigForm();
    form.setStructure("1");
    form.setName(NAME);
    form.setStatus(STATUS);
    form.setIntentionToReactivate(INTENTION_TO_REACTIVATE);
    return form;
  }

  public static IntegratedRigView createIntegratedRigView() {
    var integratedRig = createIntegratedRig_withDevUkFacility();
    return IntegratedRigViewUtil.from(integratedRig, 1, true);
  }

  public static IntegratedRigView createIntegratedRigView(Integer displayOrder, boolean isValid) {
    var integratedRigView = createIntegratedRigView();
    integratedRigView.setDisplayOrder(displayOrder);
    integratedRigView.setIsValid(isValid);
    return integratedRigView;
  }

  private static IntegratedRig createIntegratedRig(DevUkFacility facility,
                                                   String manualFacility) {
    var integratedRig = new IntegratedRig();
    integratedRig.setProjectDetail(ProjectUtil.getProjectDetails());
    integratedRig.setFacility(facility);
    integratedRig.setManualFacility(manualFacility);
    integratedRig.setName(NAME);
    integratedRig.setStatus(STATUS);
    integratedRig.setIntentionToReactivate(INTENTION_TO_REACTIVATE);

    return integratedRig;
  }
}
