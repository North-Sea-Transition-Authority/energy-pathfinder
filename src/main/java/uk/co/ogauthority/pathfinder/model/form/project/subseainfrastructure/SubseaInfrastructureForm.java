package uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.InfrastructureStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure.SubseaInfrastructureType;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.concretemattress.ConcreteMattressFullValidation;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.concretemattress.ConcreteMattressPartialValidation;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.othersubseastructure.OtherSubseaStructureFullValidation;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.othersubseastructure.OtherSubseaStructurePartialValidation;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.subseastructure.SubseaStructureFullValidation;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.subseastructure.SubseaStructurePartialValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class SubseaInfrastructureForm {

  @NotEmpty(message = "Select which surface infrastructure this subsea item ties back to", groups = FullValidation.class)
  private String structure;

  @NotEmpty(message = "Enter a description for the subsea item", groups = FullValidation.class)
  private String description;

  @NotNull(message = "Select the status of the subsea item", groups = FullValidation.class)
  private InfrastructureStatus status;

  @NotNull(message = "Select the type of subsea infrastructure", groups = FullValidation.class)
  private SubseaInfrastructureType infrastructureType;

  @NotNull(groups = { ConcreteMattressFullValidation.class, ConcreteMattressPartialValidation.class })
  @Valid
  private ConcreteMattressForm concreteMattressForm;

  @NotNull(groups = { SubseaStructureFullValidation.class, SubseaStructurePartialValidation.class })
  @Valid
  private SubseaStructureForm subseaStructureForm;

  @NotNull(groups = { OtherSubseaStructureFullValidation.class, OtherSubseaStructurePartialValidation.class })
  @Valid
  private OtherSubseaStructureForm otherSubseaStructureForm;

  private MinMaxDateInput decommissioningDate;

  public String getStructure() {
    return structure;
  }

  public void setStructure(String structure) {
    this.structure = structure;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public InfrastructureStatus getStatus() {
    return status;
  }

  public void setStatus(
      InfrastructureStatus status) {
    this.status = status;
  }

  public SubseaInfrastructureType getInfrastructureType() {
    return infrastructureType;
  }

  public void setInfrastructureType(
      SubseaInfrastructureType infrastructureType) {
    this.infrastructureType = infrastructureType;
  }

  public ConcreteMattressForm getConcreteMattressForm() {
    return concreteMattressForm;
  }

  public void setConcreteMattressForm(
      ConcreteMattressForm concreteMattressForm) {
    this.concreteMattressForm = concreteMattressForm;
  }

  public SubseaStructureForm getSubseaStructureForm() {
    return subseaStructureForm;
  }

  public void setSubseaStructureForm(
      SubseaStructureForm subseaStructureForm) {
    this.subseaStructureForm = subseaStructureForm;
  }

  public OtherSubseaStructureForm getOtherSubseaStructureForm() {
    return otherSubseaStructureForm;
  }

  public void setOtherSubseaStructureForm(OtherSubseaStructureForm otherSubseaStructureForm) {
    this.otherSubseaStructureForm = otherSubseaStructureForm;
  }

  public MinMaxDateInput getDecommissioningDate() {
    return decommissioningDate;
  }

  public void setDecommissioningDate(MinMaxDateInput decommissioningDate) {
    this.decommissioningDate = decommissioningDate;
  }
}
