package uk.co.ogauthority.pathfinder.service.project.subseainfrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.subseainfrastructure.SubseaInfrastructure;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure.SubseaInfrastructureType;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.ConcreteMattressForm;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.OtherSubseaStructureForm;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.SubseaInfrastructureForm;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.SubseaInfrastructureFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.SubseaInfrastructureValidationHint;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.SubseaStructureForm;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.concretemattress.ConcreteMattressFullValidation;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.concretemattress.ConcreteMattressPartialValidation;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.othersubseastructure.OtherSubseaStructureFullValidation;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.othersubseastructure.OtherSubseaStructurePartialValidation;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.subseastructure.SubseaStructureFullValidation;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.validation.subseastructure.SubseaStructurePartialValidation;
import uk.co.ogauthority.pathfinder.repository.project.subseainfrastructure.SubseaInfrastructureRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFacilitiesService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@Service
public class SubseaInfrastructureService implements ProjectFormSectionService {

  private final DevUkFacilitiesService devUkFacilitiesService;
  private final SubseaInfrastructureRepository subseaInfrastructureRepository;
  private final SearchSelectorService searchSelectorService;
  private final ValidationService validationService;
  private final SubseaInfrastructureFormValidator subseaInfrastructureFormValidator;

  @Autowired
  public SubseaInfrastructureService(DevUkFacilitiesService devUkFacilitiesService,
                                     SubseaInfrastructureRepository subseaInfrastructureRepository,
                                     SearchSelectorService searchSelectorService,
                                     ValidationService validationService,
                                     SubseaInfrastructureFormValidator subseaInfrastructureFormValidator) {
    this.devUkFacilitiesService = devUkFacilitiesService;
    this.subseaInfrastructureRepository = subseaInfrastructureRepository;
    this.searchSelectorService = searchSelectorService;
    this.validationService = validationService;
    this.subseaInfrastructureFormValidator = subseaInfrastructureFormValidator;
  }

  public SubseaInfrastructureForm getForm(Integer subseaInfrastructureId, ProjectDetail projectDetail) {
    var subseaInfrastructure = getSubseaInfrastructure(subseaInfrastructureId, projectDetail);
    return getForm(subseaInfrastructure);
  }

  private SubseaInfrastructureForm getForm(SubseaInfrastructure subseaInfrastructure) {
    var form = new SubseaInfrastructureForm();

    String structure = null;

    if (subseaInfrastructure.getFacility() != null) {
      structure = subseaInfrastructure.getFacility().getSelectionId();
    } else if (subseaInfrastructure.getManualFacility() != null) {
      structure = SearchSelectorService.getValueWithManualEntryPrefix(subseaInfrastructure.getManualFacility());
    }

    form.setStructure(structure);
    form.setDescription(subseaInfrastructure.getDescription());
    form.setStatus(subseaInfrastructure.getStatus());
    form.setInfrastructureType(subseaInfrastructure.getInfrastructureType());

    setInfrastructureTypeFormFields(form, subseaInfrastructure);

    form.setDecommissioningDate(new MinMaxDateInput(
        StringDisplayUtil.getValueAsStringOrNull(subseaInfrastructure.getEarliestDecommissioningStartYear()),
        StringDisplayUtil.getValueAsStringOrNull(subseaInfrastructure.getLatestDecommissioningCompletionYear())
    ));

    return form;
  }

  public String getFacilityRestUrl() {
    return devUkFacilitiesService.getFacilitiesRestUrl();
  }

  public Map<String, String> getPreSelectedFacility(SubseaInfrastructureForm form) {
    return devUkFacilitiesService.getPreSelectedFacility(form.getStructure());
  }

  public BindingResult validate(SubseaInfrastructureForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {

    // This form only has a radio input which doesn't post anything back if no option is
    // selected. As a result the nested form is not initialised and the validation doesn't
    // work as expected. Initialise the child form so spring validates fields correctly.
    if (form.getSubseaStructureForm() == null) {
      form.setSubseaStructureForm(new SubseaStructureForm());
    }

    var subseaInfrastructureValidationHint = new SubseaInfrastructureValidationHint(validationType);
    subseaInfrastructureFormValidator.validate(form, bindingResult, subseaInfrastructureValidationHint);

    var hints = new ArrayList<>();
    if (form.getInfrastructureType() != null) {
      hints.add(getInfrastructureTypeValidationClass(form, validationType));
    }

    return validationService.validate(form, bindingResult, validationType, hints);
  }

  private Class<?> getInfrastructureTypeValidationClass(SubseaInfrastructureForm form, ValidationType validationType) {

    var infrastructureType = form.getInfrastructureType();

    if (infrastructureType.equals(SubseaInfrastructureType.CONCRETE_MATTRESSES)) {
      return (validationType.equals(ValidationType.FULL))
          ? ConcreteMattressFullValidation.class
          : ConcreteMattressPartialValidation.class;
    } else if (infrastructureType.equals(SubseaInfrastructureType.SUBSEA_STRUCTURE)) {
      return (validationType.equals(ValidationType.FULL))
          ? SubseaStructureFullValidation.class
          : SubseaStructurePartialValidation.class;
    } else if (infrastructureType.equals(SubseaInfrastructureType.OTHER)) {
      return (validationType.equals(ValidationType.FULL))
          ? OtherSubseaStructureFullValidation.class
          : OtherSubseaStructurePartialValidation.class;
    } else {
      throw new IllegalArgumentException("Could not determine validation class as infrastructure type is null");
    }
  }

  @Transactional
  public SubseaInfrastructure createSubseaInfrastructure(ProjectDetail projectDetail, SubseaInfrastructureForm form) {
    var subseaInfrastructure = new SubseaInfrastructure();
    return createOrUpdateSubseaInfrastructure(subseaInfrastructure, projectDetail, form);
  }

  @Transactional
  public SubseaInfrastructure updateSubseaInfrastructure(Integer subseaInfrastructureId,
                                                         ProjectDetail projectDetail,
                                                         SubseaInfrastructureForm form) {
    var subseaInfrastructure = getSubseaInfrastructure(subseaInfrastructureId, projectDetail);
    return createOrUpdateSubseaInfrastructure(subseaInfrastructure, projectDetail, form);
  }

  private SubseaInfrastructure createOrUpdateSubseaInfrastructure(SubseaInfrastructure subseaInfrastructure,
                                                                  ProjectDetail projectDetail,
                                                                  SubseaInfrastructureForm form) {
    setCommonEntityFields(subseaInfrastructure, projectDetail, form);
    return subseaInfrastructureRepository.save(subseaInfrastructure);
  }

  public List<SubseaInfrastructure> getSubseaInfrastructures(ProjectDetail projectDetail) {
    return subseaInfrastructureRepository.findByProjectDetailOrderByIdAsc(projectDetail);
  }

  public boolean isValid(SubseaInfrastructure subseaInfrastructure, ValidationType validationType) {
    var form = getForm(subseaInfrastructure);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  @Transactional
  public void deleteSubseaInfrastructure(SubseaInfrastructure subseaInfrastructure) {
    subseaInfrastructureRepository.delete(subseaInfrastructure);
  }

  private void setCommonEntityFields(SubseaInfrastructure subseaInfrastructure,
                                     ProjectDetail projectDetail,
                                     SubseaInfrastructureForm form) {

    subseaInfrastructure.setProjectDetail(projectDetail);

    searchSelectorService.mapSearchSelectorFormEntryToEntity(
        form.getStructure(),
        devUkFacilitiesService.getFacilityAsList(form.getStructure()),
        subseaInfrastructure::setManualFacility,
        subseaInfrastructure::setFacility
    );

    subseaInfrastructure.setDescription(form.getDescription());
    subseaInfrastructure.setStatus(form.getStatus());
    subseaInfrastructure.setInfrastructureType(form.getInfrastructureType());

    setInfrastructureTypeEntityFields(subseaInfrastructure, form);

    var decommissioningDate = form.getDecommissioningDate();

    if (decommissioningDate != null) {
      subseaInfrastructure.setEarliestDecommissioningStartYear(
          (decommissioningDate.getMinYear() != null) ? Integer.parseInt(decommissioningDate.getMinYear()) : null
      );
      subseaInfrastructure.setLatestDecommissioningCompletionYear(
          (decommissioningDate.getMaxYear() != null) ? Integer.parseInt(decommissioningDate.getMaxYear()) : null
      );
    }
  }

  private void setInfrastructureTypeEntityFields(SubseaInfrastructure subseaInfrastructure,
                                                 SubseaInfrastructureForm form) {
    if (form.getInfrastructureType() == null) {
      clearInfrastructureTypeConditionalEntityFields(subseaInfrastructure);
    } else if (form.getInfrastructureType().equals(SubseaInfrastructureType.CONCRETE_MATTRESSES)) {
      setConcreteMattressEntityFields(subseaInfrastructure, form.getConcreteMattressForm());
    } else if (form.getInfrastructureType().equals(SubseaInfrastructureType.SUBSEA_STRUCTURE)) {
      setSubseaStructureEntityFields(subseaInfrastructure, form.getSubseaStructureForm());
    } else if (form.getInfrastructureType().equals(SubseaInfrastructureType.OTHER)) {
      setOtherSubseaStructureEntityFields(subseaInfrastructure, form.getOtherSubseaStructureForm());
    }
  }

  public SubseaInfrastructure getSubseaInfrastructure(Integer subseaInfrastructureId, ProjectDetail projectDetail) {
    return subseaInfrastructureRepository.findByIdAndProjectDetail(subseaInfrastructureId, projectDetail)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(String.format(
            "Could not find SubseaInfrastructure with ID %d for ProjectDetail with ID %s",
            subseaInfrastructureId,
            projectDetail.getId()
        )));
  }

  private void clearInfrastructureTypeConditionalEntityFields(SubseaInfrastructure subseaInfrastructure) {
    clearConcreteMattressEntityFields(subseaInfrastructure);
    clearSubseaStructureEntityFields(subseaInfrastructure);
    clearOtherSubseaStructureEntityFields(subseaInfrastructure);
  }

  private void clearConcreteMattressEntityFields(SubseaInfrastructure subseaInfrastructure) {
    subseaInfrastructure.setNumberOfMattresses(null);
    subseaInfrastructure.setTotalEstimatedMattressMass(null);
  }

  private void setConcreteMattressEntityFields(SubseaInfrastructure subseaInfrastructure, ConcreteMattressForm form) {
    subseaInfrastructure.setNumberOfMattresses(form.getNumberOfMattresses());
    subseaInfrastructure.setTotalEstimatedMattressMass(form.getTotalEstimatedMattressMass());
    clearSubseaStructureEntityFields(subseaInfrastructure);
    clearOtherSubseaStructureEntityFields(subseaInfrastructure);
  }

  private void clearSubseaStructureEntityFields(SubseaInfrastructure subseaInfrastructure) {
    subseaInfrastructure.setTotalEstimatedSubseaMass(null);
  }

  private void setSubseaStructureEntityFields(SubseaInfrastructure subseaInfrastructure, SubseaStructureForm form) {
    subseaInfrastructure.setTotalEstimatedSubseaMass(form.getTotalEstimatedSubseaMass());
    clearConcreteMattressEntityFields(subseaInfrastructure);
    clearOtherSubseaStructureEntityFields(subseaInfrastructure);
  }

  private void clearOtherSubseaStructureEntityFields(SubseaInfrastructure subseaInfrastructure) {
    subseaInfrastructure.setOtherInfrastructureType(null);
    subseaInfrastructure.setTotalEstimatedOtherMass(null);
  }

  private void setOtherSubseaStructureEntityFields(SubseaInfrastructure subseaInfrastructure,
                                                   OtherSubseaStructureForm form) {
    subseaInfrastructure.setOtherInfrastructureType(form.getTypeOfStructure());
    subseaInfrastructure.setTotalEstimatedOtherMass(form.getTotalEstimatedMass());
    clearConcreteMattressEntityFields(subseaInfrastructure);
    clearSubseaStructureEntityFields(subseaInfrastructure);
  }

  private void setInfrastructureTypeFormFields(SubseaInfrastructureForm form,
                                               SubseaInfrastructure subseaInfrastructure) {

    form.setConcreteMattressForm(new ConcreteMattressForm());
    form.setSubseaStructureForm(new SubseaStructureForm());
    form.setOtherSubseaStructureForm(new OtherSubseaStructureForm());

    if (subseaInfrastructure.getInfrastructureType() == null) {
      clearInfrastructureTypeConditionalFormFields(form);
    } else if (subseaInfrastructure.getInfrastructureType().equals(SubseaInfrastructureType.CONCRETE_MATTRESSES)) {
      setConcreteMattressFormFields(subseaInfrastructure, form.getConcreteMattressForm());
    } else if (form.getInfrastructureType().equals(SubseaInfrastructureType.SUBSEA_STRUCTURE)) {
      setSubseaStructureFormFields(subseaInfrastructure, form.getSubseaStructureForm());
    } else if (form.getInfrastructureType().equals(SubseaInfrastructureType.OTHER)) {
      setOtherSubseaStructureFormFields(subseaInfrastructure, form.getOtherSubseaStructureForm());
    }
  }

  private void clearInfrastructureTypeConditionalFormFields(SubseaInfrastructureForm form) {
    clearConcreteMattressFormFields(form.getConcreteMattressForm());
    clearSubseaStructureFormFields(form.getSubseaStructureForm());
    clearOtherSubseaStructureFormFields(form.getOtherSubseaStructureForm());
  }

  private void clearConcreteMattressFormFields(ConcreteMattressForm form) {
    form.setNumberOfMattresses(null);
    form.setTotalEstimatedMattressMass(null);
  }

  private void clearSubseaStructureFormFields(SubseaStructureForm form) {
    form.setTotalEstimatedSubseaMass(null);
  }

  private void clearOtherSubseaStructureFormFields(OtherSubseaStructureForm form) {
    form.setTypeOfStructure(null);
    form.setTotalEstimatedMass(null);
  }

  private void setConcreteMattressFormFields(SubseaInfrastructure subseaInfrastructure, ConcreteMattressForm form) {
    form.setNumberOfMattresses(subseaInfrastructure.getNumberOfMattresses());
    form.setTotalEstimatedMattressMass(subseaInfrastructure.getTotalEstimatedMattressMass());
  }

  private void setSubseaStructureFormFields(SubseaInfrastructure subseaInfrastructure, SubseaStructureForm form) {
    form.setTotalEstimatedSubseaMass(subseaInfrastructure.getTotalEstimatedSubseaMass());
  }

  private void setOtherSubseaStructureFormFields(SubseaInfrastructure subseaInfrastructure,
                                                 OtherSubseaStructureForm form) {
    form.setTypeOfStructure(subseaInfrastructure.getOtherInfrastructureType());
    form.setTotalEstimatedMass(subseaInfrastructure.getTotalEstimatedOtherMass());
  }

  @Override
  public boolean isComplete(ProjectDetail projectDetail) {
    var subseaInfrastructures = getSubseaInfrastructures(projectDetail);
    return !subseaInfrastructures.isEmpty() && subseaInfrastructures.stream()
        .allMatch(subseaInfrastructure -> isValid(subseaInfrastructure, ValidationType.FULL));
  }
}
