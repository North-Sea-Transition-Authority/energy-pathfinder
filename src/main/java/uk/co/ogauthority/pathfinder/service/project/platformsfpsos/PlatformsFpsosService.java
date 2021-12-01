package uk.co.ogauthority.pathfinder.service.project.platformsfpsos;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.PlatformFpsoInfrastructureType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoForm;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoValidationHint;
import uk.co.ogauthority.pathfinder.repository.project.platformsfpsos.PlatformFpsoRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFacilitiesService;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class PlatformsFpsosService implements ProjectFormSectionService {


  private final PlatformFpsoRepository platformFpsoRepository;
  private final DevUkFacilitiesService devUkFacilitiesService;
  private final SearchSelectorService searchSelectorService;
  private final PlatformFpsoFormValidator platformFpsoFormValidator;
  private final ValidationService validationService;
  private final ProjectSetupService projectSetupService;
  private final EntityDuplicationService entityDuplicationService;

  @Autowired
  public PlatformsFpsosService(
      PlatformFpsoRepository platformFpsoRepository,
      DevUkFacilitiesService devUkFacilitiesService,
      SearchSelectorService searchSelectorService,
      PlatformFpsoFormValidator platformFpsoFormValidator,
      ValidationService validationService,
      ProjectSetupService projectSetupService,
      EntityDuplicationService entityDuplicationService) {
    this.platformFpsoRepository = platformFpsoRepository;
    this.devUkFacilitiesService = devUkFacilitiesService;
    this.searchSelectorService = searchSelectorService;
    this.platformFpsoFormValidator = platformFpsoFormValidator;
    this.validationService = validationService;
    this.projectSetupService = projectSetupService;
    this.entityDuplicationService = entityDuplicationService;
  }

  @Transactional
  public PlatformFpso createPlatformFpso(ProjectDetail detail, PlatformFpsoForm form) {
    var platformFpso = new PlatformFpso(detail);
    return updatePlatformFpso(detail, platformFpso, form);
  }

  @Transactional
  public PlatformFpso updatePlatformFpso(ProjectDetail detail, PlatformFpso platformFpso, PlatformFpsoForm form) {
    platformFpso.setProjectDetail(detail);
    return platformFpsoRepository.save(setCommonFields(platformFpso, form));
  }

  private PlatformFpso setCommonFields(PlatformFpso platformFpso, PlatformFpsoForm form) {
    var infrastructureType = form.getInfrastructureType();
    platformFpso.setInfrastructureType(infrastructureType);

    String structure;
    if (PlatformFpsoInfrastructureType.PLATFORM.equals(infrastructureType)) {
      structure = form.getPlatformStructure();
    } else {
      structure = form.getFpsoStructure();
    }

    if (SearchSelectorService.isManualEntry(structure)) {
      platformFpso.setManualStructureName(SearchSelectorService.removePrefix(structure));
      platformFpso.setStructure(null);
    } else if (structure != null) {
      platformFpso.setStructure(devUkFacilitiesService.getOrError(Integer.parseInt(structure)));
      platformFpso.setManualStructureName(null);
    } else {
      platformFpso.setManualStructureName(null);
      platformFpso.setStructure(null);
    }

    if (PlatformFpsoInfrastructureType.FPSO.equals(infrastructureType)) {
      populateFloatingUnitNestedEntityProperties(form, platformFpso);
    } else {
      clearFloatingUnitNestedEntityProperties(platformFpso);
    }

    platformFpso.setTopsideFpsoMass(form.getTopsideFpsoMass());
    platformFpso.setEarliestRemovalYear(form.getTopsideRemovalYears().getMinYear());
    platformFpso.setLatestRemovalYear(form.getTopsideRemovalYears().getMaxYear());
    platformFpso.setFuturePlans(form.getFuturePlans());

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

    return platformFpso;
  }

  private void populateFloatingUnitNestedEntityProperties(PlatformFpsoForm sourceForm,
                                                          PlatformFpso destinationEntity) {
    destinationEntity.setFpsoType(sourceForm.getFpsoType());
    destinationEntity.setFpsoDimensions(sourceForm.getFpsoDimensions());
  }

  private void clearFloatingUnitNestedEntityProperties(PlatformFpso platformFpso) {
    platformFpso.setFpsoType(null);
    platformFpso.setFpsoDimensions(null);
  }

  @Transactional
  public void delete(PlatformFpso platformFpso) {
    platformFpsoRepository.delete(platformFpso);
  }

  public List<PlatformFpso> getPlatformsFpsosByProjectDetail(ProjectDetail detail) {
    return platformFpsoRepository.findAllByProjectDetailOrderByIdAsc(detail);
  }

  public List<PlatformFpso> getPlatformsFpsosByProjectAndVersion(Project project, Integer version) {
    return platformFpsoRepository.findAllByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version);
  }

  public PlatformFpso getOrError(Integer platformFpsoId) {
    return platformFpsoRepository.findById(platformFpsoId)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
                String.format("Unable to find PlatformFpso with ID %d", platformFpsoId))
        );
  }

  public BindingResult validate(PlatformFpsoForm form,
                                BindingResult bindingResult,
                                ValidationType validationType
  ) {
    var platformFpsoValidationHint = new PlatformFpsoValidationHint(validationType);
    platformFpsoFormValidator.validate(form, bindingResult, platformFpsoValidationHint);
    return validationService.validate(form, bindingResult, validationType);
  }

  public PlatformFpsoForm getForm(PlatformFpso platformFpso) {
    var form = new PlatformFpsoForm();

    var infrastructureType = platformFpso.getInfrastructureType();
    form.setInfrastructureType(infrastructureType);

    if (platformFpso.getStructure() != null) {
      if (PlatformFpsoInfrastructureType.PLATFORM.equals(infrastructureType)) {
        form.setPlatformStructure(platformFpso.getStructure().getId().toString());
      } else {
        form.setFpsoStructure(platformFpso.getStructure().getId().toString());
      }
    } else if (platformFpso.getManualStructureName() != null) {
      if (PlatformFpsoInfrastructureType.PLATFORM.equals(infrastructureType)) {
        form.setPlatformStructure(SearchSelectorService.getValueWithManualEntryPrefix(platformFpso.getManualStructureName()));
      } else {
        form.setFpsoStructure(SearchSelectorService.getValueWithManualEntryPrefix(platformFpso.getManualStructureName()));
      }
    }
    form.setFpsoType(platformFpso.getFpsoType());
    form.setFpsoDimensions(platformFpso.getFpsoDimensions());
    form.setTopsideFpsoMass(platformFpso.getTopsideFpsoMass());
    form.setTopsideRemovalYears(
        new MinMaxDateInput(platformFpso.getEarliestRemovalYear(), platformFpso.getLatestRemovalYear())
    );
    form.setSubstructureExpectedToBeRemoved(platformFpso.getSubstructuresExpectedToBeRemoved());
    form.setSubstructureRemovalPremise(platformFpso.getSubstructureRemovalPremise());
    form.setSubstructureRemovalMass(platformFpso.getSubstructureRemovalMass());
    form.setSubstructureRemovalYears(
        new MinMaxDateInput(platformFpso.getSubStructureRemovalEarliestYear(), platformFpso.getSubStructureRemovalLatestYear())
    );
    form.setFuturePlans(platformFpso.getFuturePlans());
    return form;
  }

  public Boolean isValid(PlatformFpso platformFpso, ValidationType validationType) {
    var form = getForm(platformFpso);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  public Map<String, String> getPreselectedPlatformStructure(PlatformFpsoForm form) {
    if (PlatformFpsoInfrastructureType.PLATFORM.equals(form.getInfrastructureType())) {
      return getPreselectedStructure(form.getPlatformStructure());
    }
    return Map.of();
  }

  public Map<String, String> getPreselectedFpsoStructure(PlatformFpsoForm form) {
    if (PlatformFpsoInfrastructureType.FPSO.equals(form.getInfrastructureType())) {
      return getPreselectedStructure(form.getFpsoStructure());
    }
    return Map.of();
  }

  private Map<String, String> getPreselectedStructure(String structure) {
    if (structure != null) {
      return SearchSelectorService.isManualEntry(structure)
          ? searchSelectorService.buildPrePopulatedSelections(
              Collections.singletonList(structure),
              Map.of(structure, structure)
            )
          : searchSelectorService.buildPrePopulatedSelections(
              Collections.singletonList(structure),
              Map.of(
                  structure,
                  devUkFacilitiesService.getOrError(Integer.parseInt(structure)).getFacilityName()
              )
            );

    }
    return Map.of();
  }

  @Override
  public boolean isComplete(ProjectDetail detail) {
    var platformsFpsos = getPlatformsFpsosByProjectDetail(detail);
    return !platformsFpsos.isEmpty() && platformsFpsos.stream().allMatch(p -> isValid(p, ValidationType.FULL));
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail) {
    return projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.PLATFORM_FPSO);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    platformFpsoRepository.deleteAllByProjectDetail(projectDetail);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {
    entityDuplicationService.duplicateEntitiesAndSetNewParent(
        getPlatformsFpsosByProjectDetail(fromDetail),
        toDetail,
        PlatformFpso.class
    );
  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return Set.of(ProjectType.INFRASTRUCTURE);
  }
}
