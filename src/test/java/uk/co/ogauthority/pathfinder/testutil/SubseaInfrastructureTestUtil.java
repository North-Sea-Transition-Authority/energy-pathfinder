package uk.co.ogauthority.pathfinder.testutil;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkFacility;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.subseainfrastructure.SubseaInfrastructure;
import uk.co.ogauthority.pathfinder.model.enums.project.InfrastructureStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure.SubseaInfrastructureType;
import uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure.SubseaStructureMass;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.ConcreteMattressForm;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.OtherSubseaStructureForm;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.SubseaInfrastructureForm;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.SubseaStructureForm;
import uk.co.ogauthority.pathfinder.model.view.subseainfrastructure.SubseaInfrastructureView;
import uk.co.ogauthority.pathfinder.model.view.subseainfrastructure.SubseaInfrastructureViewUtil;

public class SubseaInfrastructureTestUtil {

  private static final String DESCRIPTION = "description";
  private static final InfrastructureStatus INFRASTRUCTURE_STATUS = InfrastructureStatus.IN_USE;
  private static final Integer EARLIEST_DECOM_START = 2020;
  private static final Integer LATEST_DECOM_COMPLETION = LocalDate.now().getYear();
  private static final Integer NUMBER_OF_MATTRESSES = 1;
  private static final Integer TOTAL_MATTRESS_MASS = 10;
  private static final SubseaStructureMass SUBSEA_STRUCTURE_MASS = SubseaStructureMass.GREATER_THAN_OR_EQUAL_400_TONNES;
  private static final String OTHER_STRUCTURE_TYPE = "my structure";
  private static final Integer TOTAL_OTHER_STRUCTURE_MASS = 20;

  private SubseaInfrastructureTestUtil() {
    throw new IllegalStateException("SubseaInfrastructureTestUtil is a utility class and should not be instantiated");
  }

  public static SubseaInfrastructure createSubseaInfrastructure(Integer id, ProjectDetail projectDetail) {
    return createSubseaInfrastructure(id, projectDetail, DevUkTestUtil.getDevUkFacility(), null);
  }

  public static SubseaInfrastructure createSubseaInfrastructure_withDevUkFacility() {
    return createSubseaInfrastructure(DevUkTestUtil.getDevUkFacility(), null);
  }

  public static SubseaInfrastructure createSubseaInfrastructure_withManualFacility() {
    return createSubseaInfrastructure(null, "manual facility name");
  }

  public static SubseaInfrastructure createSubseaInfrastructure_withConcreteMattresses() {
    var subseaInfrastructure = createSubseaInfrastructure(DevUkTestUtil.getDevUkFacility(), null);
    setConcreteMattressEntityFields(subseaInfrastructure);
    return subseaInfrastructure;
  }

  public static SubseaInfrastructure createSubseaInfrastructure_withSubseaStructure() {
    var subseaInfrastructure = createSubseaInfrastructure(DevUkTestUtil.getDevUkFacility(), null);
    setSubseaStructureEntityFields(subseaInfrastructure);
    return subseaInfrastructure;
  }

  public static SubseaInfrastructure createSubseaInfrastructure_withOtherInfrastructure() {
    var subseaInfrastructure = createSubseaInfrastructure(DevUkTestUtil.getDevUkFacility(), null);
    setOtherInfrastructureEntityFields(subseaInfrastructure);
    return subseaInfrastructure;
  }

  public static SubseaInfrastructureForm createSubseaInfrastructureForm() {
    return createSubseaInfrastructureForm(SubseaInfrastructureType.CONCRETE_MATTRESSES);
  }

  public static SubseaInfrastructureForm createSubseaInfrastructureForm(SubseaInfrastructureType infrastructureType) {
    var form = new SubseaInfrastructureForm();
    form.setStructure("1");
    form.setDescription(DESCRIPTION);
    form.setStatus(INFRASTRUCTURE_STATUS);
    form.setInfrastructureType(infrastructureType);
    form.setDecommissioningDate(
        new MinMaxDateInput(String.valueOf(EARLIEST_DECOM_START), String.valueOf(LATEST_DECOM_COMPLETION))
    );

    setInfrastructureTypeHiddenFormFields(form);

    return form;
  }

  public static SubseaInfrastructureView createSubseaInfrastructureView() {
    var subseaInfrastructure = createSubseaInfrastructure_withDevUkFacility();
    return SubseaInfrastructureViewUtil.from(subseaInfrastructure, 1, true);
  }

  public static SubseaInfrastructureView createSubseaInfrastructureView(Integer displayOrder, boolean isValid) {
    var subseaInfrastructureView = createSubseaInfrastructureView();
    subseaInfrastructureView.setDisplayOrder(displayOrder);
    subseaInfrastructureView.setIsValid(isValid);
    return subseaInfrastructureView;
  }

  private static void setInfrastructureTypeHiddenFormFields(SubseaInfrastructureForm form) {

    final var infrastructureType = form.getInfrastructureType();

    if (infrastructureType.equals(SubseaInfrastructureType.CONCRETE_MATTRESSES)) {
      form.setConcreteMattressForm(createConcreteMattressForm());
    } else if (infrastructureType.equals(SubseaInfrastructureType.SUBSEA_STRUCTURE)) {
      form.setSubseaStructureForm(createSubseaStructureForm());
    } else if (infrastructureType.equals(SubseaInfrastructureType.OTHER)) {
      form.setOtherSubseaStructureForm(createOtherSubseaStructureForm());
    }
  }

  private static ConcreteMattressForm createConcreteMattressForm() {
    var concreteMattressForm = new ConcreteMattressForm();
    concreteMattressForm.setNumberOfMattresses(NUMBER_OF_MATTRESSES);
    concreteMattressForm.setTotalEstimatedMattressMass(TOTAL_MATTRESS_MASS);
    return concreteMattressForm;
  }

  private static SubseaStructureForm createSubseaStructureForm() {
    var subseaStructureForm = new SubseaStructureForm();
    subseaStructureForm.setTotalEstimatedSubseaMass(SUBSEA_STRUCTURE_MASS);
    return subseaStructureForm;
  }

  private static OtherSubseaStructureForm createOtherSubseaStructureForm() {
    var otherSubseaStructureForm = new OtherSubseaStructureForm();
    otherSubseaStructureForm.setTypeOfStructure(OTHER_STRUCTURE_TYPE);
    otherSubseaStructureForm.setTotalEstimatedMass(TOTAL_OTHER_STRUCTURE_MASS);
    return otherSubseaStructureForm;
  }

  private static SubseaInfrastructure createSubseaInfrastructure(DevUkFacility facility, String manualFacility) {
    return createSubseaInfrastructure(null, ProjectUtil.getProjectDetails(), facility, manualFacility);
  }

  private static SubseaInfrastructure createSubseaInfrastructure(
      Integer id,
      ProjectDetail projectDetail,
      DevUkFacility facility,
      String manualFacility
  ) {
    var subseaInfrastructure = new SubseaInfrastructure(id);
    subseaInfrastructure.setProjectDetail(projectDetail);
    subseaInfrastructure.setFacility(facility);
    subseaInfrastructure.setManualFacility(manualFacility);
    subseaInfrastructure.setDescription(DESCRIPTION);
    subseaInfrastructure.setStatus(INFRASTRUCTURE_STATUS);
    subseaInfrastructure.setEarliestDecommissioningStartYear(EARLIEST_DECOM_START);
    subseaInfrastructure.setLatestDecommissioningCompletionYear(LATEST_DECOM_COMPLETION);

    setConcreteMattressEntityFields(subseaInfrastructure);

    return subseaInfrastructure;
  }

  private static void setConcreteMattressEntityFields(SubseaInfrastructure subseaInfrastructure) {
    subseaInfrastructure.setInfrastructureType(SubseaInfrastructureType.CONCRETE_MATTRESSES);
    subseaInfrastructure.setNumberOfMattresses(NUMBER_OF_MATTRESSES);
    subseaInfrastructure.setTotalEstimatedMattressMass(TOTAL_MATTRESS_MASS);

    subseaInfrastructure.setTotalEstimatedSubseaMass(null);
    subseaInfrastructure.setOtherInfrastructureType(null);
    subseaInfrastructure.setTotalEstimatedOtherMass(null);
  }

  private static void setSubseaStructureEntityFields(SubseaInfrastructure subseaInfrastructure) {
    subseaInfrastructure.setInfrastructureType(SubseaInfrastructureType.SUBSEA_STRUCTURE);
    subseaInfrastructure.setTotalEstimatedSubseaMass(SUBSEA_STRUCTURE_MASS);

    subseaInfrastructure.setNumberOfMattresses(null);
    subseaInfrastructure.setTotalEstimatedMattressMass(null);
    subseaInfrastructure.setOtherInfrastructureType(null);
    subseaInfrastructure.setTotalEstimatedOtherMass(null);
  }

  private static void setOtherInfrastructureEntityFields(SubseaInfrastructure subseaInfrastructure) {
    subseaInfrastructure.setInfrastructureType(SubseaInfrastructureType.OTHER);
    subseaInfrastructure.setOtherInfrastructureType("type");
    subseaInfrastructure.setTotalEstimatedOtherMass(1);

    subseaInfrastructure.setTotalEstimatedSubseaMass(null);
    subseaInfrastructure.setNumberOfMattresses(null);
    subseaInfrastructure.setTotalEstimatedMattressMass(null);
  }
}
