package uk.co.ogauthority.pathfinder.service.project.integratedrig;

import java.util.Map;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.integratedrig.IntegratedRig;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.integratedrig.IntegratedRigForm;
import uk.co.ogauthority.pathfinder.repository.project.integratedrig.IntegratedRigRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFacilitiesService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class IntegratedRigService {

  private final DevUkFacilitiesService devUkFacilitiesService;
  private final IntegratedRigRepository integratedRigRepository;
  private final SearchSelectorService searchSelectorService;
  private final ValidationService validationService;

  @Autowired
  public IntegratedRigService(DevUkFacilitiesService devUkFacilitiesService,
                              IntegratedRigRepository integratedRigRepository,
                              SearchSelectorService searchSelectorService,
                              ValidationService validationService) {
    this.devUkFacilitiesService = devUkFacilitiesService;
    this.integratedRigRepository = integratedRigRepository;
    this.searchSelectorService = searchSelectorService;
    this.validationService = validationService;
  }

  public String getFacilityRestUrl() {
    return devUkFacilitiesService.getFacilitiesRestUrl();
  }

  public Map<String, String> getPreSelectedFacility(IntegratedRigForm form) {
    return devUkFacilitiesService.getPreSelectedFacility(form.getStructure());
  }

  public BindingResult validate(IntegratedRigForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    return validationService.validate(form, bindingResult, validationType);
  }

  public IntegratedRig createIntegratedRig(ProjectDetail projectDetail, IntegratedRigForm form) {
    var integratedRig = new IntegratedRig();
    return createOrUpdateIntegratedRig(integratedRig, projectDetail, form);
  }

  @Transactional
  IntegratedRig createOrUpdateIntegratedRig(IntegratedRig integratedRig,
                                            ProjectDetail projectDetail,
                                            IntegratedRigForm form) {
    setCommonEntityFields(integratedRig, projectDetail, form);
    return integratedRigRepository.save(integratedRig);
  }

  private void setCommonEntityFields(IntegratedRig integratedRig,
                                     ProjectDetail projectDetail,
                                     IntegratedRigForm form) {
    integratedRig.setProjectDetail(projectDetail);

    searchSelectorService.mapSearchSelectorFormEntryToEntity(
        form.getStructure(),
        devUkFacilitiesService.getFacilityAsList(form.getStructure()),
        integratedRig::setManualFacility,
        integratedRig::setFacility
    );

    integratedRig.setName(form.getName());
    integratedRig.setStatus(form.getStatus());
    integratedRig.setIntentionToReactivate(form.getIntentionToReactivate());
  }
}
