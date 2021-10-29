package uk.co.ogauthority.pathfinder.service.project.integratedrig;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.integratedrig.IntegratedRig;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.project.integratedrig.IntegratedRigForm;
import uk.co.ogauthority.pathfinder.repository.project.integratedrig.IntegratedRigRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFacilitiesService;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class IntegratedRigService implements ProjectFormSectionService {

  private final DevUkFacilitiesService devUkFacilitiesService;
  private final IntegratedRigRepository integratedRigRepository;
  private final SearchSelectorService searchSelectorService;
  private final ValidationService validationService;
  private final ProjectSetupService projectSetupService;
  private final EntityDuplicationService entityDuplicationService;

  @Autowired
  public IntegratedRigService(DevUkFacilitiesService devUkFacilitiesService,
                              IntegratedRigRepository integratedRigRepository,
                              SearchSelectorService searchSelectorService,
                              ValidationService validationService,
                              ProjectSetupService projectSetupService,
                              EntityDuplicationService entityDuplicationService) {
    this.devUkFacilitiesService = devUkFacilitiesService;
    this.integratedRigRepository = integratedRigRepository;
    this.searchSelectorService = searchSelectorService;
    this.validationService = validationService;
    this.projectSetupService = projectSetupService;
    this.entityDuplicationService = entityDuplicationService;
  }

  public IntegratedRigForm getForm(Integer integratedRigId, ProjectDetail projectDetail) {
    var integratedRig = getIntegratedRig(integratedRigId, projectDetail);
    return getForm(integratedRig);
  }

  private IntegratedRigForm getForm(IntegratedRig integratedRig) {
    var form = new IntegratedRigForm();

    String structure = null;

    if (integratedRig.getFacility() != null) {
      structure = integratedRig.getFacility().getSelectionId();
    } else if (integratedRig.getManualFacility() != null) {
      structure = SearchSelectorService.getValueWithManualEntryPrefix(integratedRig.getManualFacility());
    }

    form.setStructure(structure);
    form.setName(integratedRig.getName());
    form.setStatus(integratedRig.getStatus());
    form.setIntentionToReactivate(integratedRig.getIntentionToReactivate());

    return form;
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

  @Transactional
  public IntegratedRig createIntegratedRig(ProjectDetail projectDetail, IntegratedRigForm form) {
    var integratedRig = new IntegratedRig();
    return createOrUpdateIntegratedRig(integratedRig, projectDetail, form);
  }

  @Transactional
  public IntegratedRig updateIntegratedRig(Integer integratedRigId,
                                           ProjectDetail projectDetail,
                                           IntegratedRigForm form) {
    var integratedRig = getIntegratedRig(integratedRigId, projectDetail);
    return createOrUpdateIntegratedRig(integratedRig, projectDetail, form);
  }

  private IntegratedRig createOrUpdateIntegratedRig(IntegratedRig integratedRig,
                                                    ProjectDetail projectDetail,
                                                    IntegratedRigForm form) {
    setCommonEntityFields(integratedRig, projectDetail, form);
    return integratedRigRepository.save(integratedRig);
  }

  public List<IntegratedRig> getIntegratedRigs(ProjectDetail projectDetail) {
    return integratedRigRepository.findByProjectDetailOrderByIdAsc(projectDetail);
  }

  public List<IntegratedRig> getIntegratedRigsByProjectAndVersion(Project project, Integer version) {
    return integratedRigRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version);
  }

  public boolean isValid(IntegratedRig integratedRig, ValidationType validationType) {
    var form = getForm(integratedRig);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  @Transactional
  public void deleteIntegratedRig(IntegratedRig integratedRig) {
    integratedRigRepository.delete(integratedRig);
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

  public IntegratedRig getIntegratedRig(Integer integratedRigId, ProjectDetail projectDetail) {
    return integratedRigRepository.findByIdAndProjectDetail(integratedRigId, projectDetail)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(String.format(
            "Could not find IntegratedRig with ID %d for ProjectDetail with ID %s",
            integratedRigId,
            projectDetail.getId()
        )));
  }

  @Override
  public boolean isComplete(ProjectDetail projectDetail) {
    var integratedRigs = getIntegratedRigs(projectDetail);
    return !integratedRigs.isEmpty() && integratedRigs.stream()
        .allMatch(integratedRig -> isValid(integratedRig, ValidationType.FULL));
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail) {
    return projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.INTEGRATED_RIGS);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    integratedRigRepository.deleteAllByProjectDetail(projectDetail);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {
    entityDuplicationService.duplicateEntitiesAndSetNewParent(
        getIntegratedRigs(fromDetail),
        toDetail,
        IntegratedRig.class
    );
  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return Set.of(ProjectType.INFRASTRUCTURE);
  }
}
