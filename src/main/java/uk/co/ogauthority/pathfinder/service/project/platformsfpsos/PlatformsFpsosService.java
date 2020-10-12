package uk.co.ogauthority.pathfinder.service.project.platformsfpsos;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoForm;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoValidationHint;
import uk.co.ogauthority.pathfinder.repository.project.platformsfpsos.PlatformFpsoRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFacilitiesService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class PlatformsFpsosService {


  private final PlatformFpsoRepository platformFpsoRepository;
  private final DevUkFacilitiesService devUkFacilitiesService;
  private final SearchSelectorService searchSelectorService;
  private final PlatformFpsoFormValidator platformFpsoFormValidator;
  private final ValidationService validationService;

  @Autowired
  public PlatformsFpsosService(
      PlatformFpsoRepository platformFpsoRepository,
      DevUkFacilitiesService devUkFacilitiesService,
      SearchSelectorService searchSelectorService,
      PlatformFpsoFormValidator platformFpsoFormValidator,
      ValidationService validationService) {
    this.platformFpsoRepository = platformFpsoRepository;
    this.devUkFacilitiesService = devUkFacilitiesService;
    this.searchSelectorService = searchSelectorService;
    this.platformFpsoFormValidator = platformFpsoFormValidator;
    this.validationService = validationService;
  }

  @Transactional
  public PlatformFpso createPlatformFpso(ProjectDetail detail, PlatformFpsoForm form) {
    var platformFpso = new PlatformFpso(detail);
    return platformFpsoRepository.save(updatePlatformFpso(platformFpso, form));
  }

  private PlatformFpso updatePlatformFpso(PlatformFpso platformFpso, PlatformFpsoForm form) {
    if (SearchSelectorService.isManualEntry(form.getStructure())) {
      platformFpso.setManualStructureName(SearchSelectorService.removePrefix(form.getStructure()));
      platformFpso.setStructure(null);
    } else if (form.getStructure() != null) {
      platformFpso.setStructure(devUkFacilitiesService.getOrError(Integer.parseInt(form.getStructure())));
      platformFpso.setManualStructureName(null);
    } else {
      platformFpso.setManualStructureName(null);
      platformFpso.setStructure(null);
    }
    platformFpso.setTopsideFpsoMass(form.getTopsideFpsoMass());
    platformFpso.setEarliestRemovalYear(form.getTopsideRemovalYears().getMinYear());
    platformFpso.setLatestRemovalYear(form.getTopsideRemovalYears().getMaxYear());
    platformFpso.setSubstructuresExpectedToBeRemoved(form.getSubstructureExpectedToBeRemoved());
    if (BooleanUtils.isTrue(form.getSubstructureExpectedToBeRemoved())) {
      platformFpso.setSubstructureRemovalPremise(form.getSubstructureRemovalPremise());
      platformFpso.setSubstructureRemovalMass(form.getSubstructureRemovalMass());
      platformFpso.setSubStructureRemovalEarliestYear(form.getSubstructureRemovalYears().getMinYear());
      platformFpso.setSubStructureRemovalLatestYear(form.getSubstructureRemovalYears().getMaxYear());
    } else {
      platformFpso.setSubstructureRemovalPremise(null);
      platformFpso.setSubstructureRemovalMass(null);
      platformFpso.setSubStructureRemovalEarliestYear(null);
      platformFpso.setSubStructureRemovalLatestYear(null);
    }
    platformFpso.setFpsoType(form.getFpsoType());
    platformFpso.setFpsoDimensions(form.getFpsoDimensions());
    platformFpso.setFuturePlans(form.getFuturePlans());

    return platformFpso;
  }

  public List<PlatformFpso> getPlatformsFpsosForDetail(ProjectDetail detail) {
    return platformFpsoRepository.findAllByProjectDetailOrderByIdAsc(detail);
  }


  public BindingResult validate(PlatformFpsoForm form,
                                BindingResult bindingResult,
                                ValidationType validationType
  ) {
    var platformFpsoValidationHint = new PlatformFpsoValidationHint(validationType);
    platformFpsoFormValidator.validate(form, bindingResult, platformFpsoValidationHint);
    return validationService.validate(form, bindingResult, validationType);
  }

  public PlatformFpsoForm getForm() {
    //TODO PAT-224 fetch from from entity
    return new PlatformFpsoForm();
  }

  public Boolean isValid(PlatformFpso platformFpso, ValidationType validationType) {
    var form = getForm();
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  public Map<String, String> getPreselectedStructure(PlatformFpsoForm form) {
    if (form.getStructure() != null) {
      return SearchSelectorService.isManualEntry(form.getStructure())
          ? searchSelectorService.buildPrePopulatedSelections(
              Collections.singletonList(form.getStructure()),
              Map.of(form.getStructure(), form.getStructure())
            )
          : searchSelectorService.buildPrePopulatedSelections(
              Collections.singletonList(form.getStructure()),
              Map.of(
                form.getStructure(),
                devUkFacilitiesService.getOrError(Integer.parseInt(form.getStructure())).getFacilityName()
              )
            );

    }
    return Map.of();
  }

}
