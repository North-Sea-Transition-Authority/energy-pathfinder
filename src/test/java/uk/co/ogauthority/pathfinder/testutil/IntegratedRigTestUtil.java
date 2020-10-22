package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.enums.project.integratedrig.IntegratedRigIntentionToReactivate;
import uk.co.ogauthority.pathfinder.model.enums.project.integratedrig.IntegratedRigStatus;
import uk.co.ogauthority.pathfinder.model.form.project.integratedrig.IntegratedRigForm;

public class IntegratedRigTestUtil {

  private static final String NAME = "name";
  private static final IntegratedRigStatus STATUS = IntegratedRigStatus.IN_USE;
  private static final IntegratedRigIntentionToReactivate INTENTION_TO_REACTIVATE = IntegratedRigIntentionToReactivate.YES;

  public static IntegratedRigForm createIntegratedRigForm() {
    var form = new IntegratedRigForm();
    form.setStructure("1");
    form.setName(NAME);
    form.setStatus(STATUS);
    form.setIntentionToReactivate(INTENTION_TO_REACTIVATE);
    return form;
  }
}
