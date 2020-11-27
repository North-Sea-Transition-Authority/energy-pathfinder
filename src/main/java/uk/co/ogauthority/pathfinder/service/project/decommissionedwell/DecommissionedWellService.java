package uk.co.ogauthority.pathfinder.service.project.decommissionedwell;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissionedwell.DecommissionedWell;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedwell.DecommissionedWellType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedwell.WellMechanicalStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedwell.WellOperationalStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedwell.DecommissionedWellForm;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedwell.DecommissionedWellFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedwell.DecommissionedWellValidationHint;
import uk.co.ogauthority.pathfinder.repository.project.decommissionedwell.DecommissionedWellRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@Service
public class DecommissionedWellService implements ProjectFormSectionService {

  private final SearchSelectorService searchSelectorService;
  private final ValidationService validationService;
  private final DecommissionedWellFormValidator decommissionedWellFormValidator;
  private final DecommissionedWellRepository decommissionedWellRepository;
  private final ProjectSetupService projectSetupService;
  private final EntityDuplicationService entityDuplicationService;

  public DecommissionedWellService(SearchSelectorService searchSelectorService,
                                   ValidationService validationService,
                                   DecommissionedWellFormValidator decommissionedWellFormValidator,
                                   DecommissionedWellRepository decommissionedWellRepository,
                                   ProjectSetupService projectSetupService,
                                   EntityDuplicationService entityDuplicationService) {
    this.searchSelectorService = searchSelectorService;
    this.validationService = validationService;
    this.decommissionedWellFormValidator = decommissionedWellFormValidator;
    this.decommissionedWellRepository = decommissionedWellRepository;
    this.projectSetupService = projectSetupService;
    this.entityDuplicationService = entityDuplicationService;
  }

  public List<RestSearchItem> findTypesLikeWithManualEntry(String searchTerm) {
    return searchSelectorService.searchWithManualEntry(
        searchTerm,
        Arrays.asList(DecommissionedWellType.values().clone())
    );
  }

  public List<RestSearchItem> findOperationalStatusesLikeWithManualEntry(String searchTerm) {
    return searchSelectorService.searchWithManualEntry(
        searchTerm,
        Arrays.asList(WellOperationalStatus.values().clone())
    );
  }

  public List<RestSearchItem> findMechanicalStatusesLikeWithManualEntry(String searchTerm) {
    return searchSelectorService.searchWithManualEntry(
        searchTerm,
        Arrays.asList(WellMechanicalStatus.values().clone())
    );
  }

  public BindingResult validate(DecommissionedWellForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    var decommissionedWellValidationHint = new DecommissionedWellValidationHint(validationType);
    decommissionedWellFormValidator.validate(form, bindingResult, validationType, decommissionedWellValidationHint);
    validationService.validate(form, bindingResult, validationType);
    return bindingResult;
  }

  @Transactional
  public DecommissionedWell createDecommissionedWell(DecommissionedWellForm form, ProjectDetail projectDetail) {
    var decommissionedWell = new DecommissionedWell();
    decommissionedWell.setProjectDetail(projectDetail);
    return updateDecommissionedWell(form, decommissionedWell);
  }

  @Transactional
  public DecommissionedWell updateDecommissionedWell(Integer decommissionedWellId,
                                                     ProjectDetail projectDetail,
                                                     DecommissionedWellForm decommissionedWellForm) {
    var decommissionedWell = getDecommissionedWell(decommissionedWellId, projectDetail);
    return updateDecommissionedWell(decommissionedWellForm, decommissionedWell);
  }

  protected DecommissionedWell updateDecommissionedWell(DecommissionedWellForm form,
                                                        DecommissionedWell decommissionedWell) {

    searchSelectorService.mapSearchSelectorFormEntryToEntity(
        form.getType(),
        DecommissionedWellType.values(),
        decommissionedWell::setManualType,
        decommissionedWell::setType
    );

    decommissionedWell.setNumberToBeDecommissioned(form.getNumberToBeDecommissioned());

    form.getPlugAbandonmentDate()
        .create()
        .ifPresent(quarterYearInput -> {
          decommissionedWell.setPlugAbandonmentDateQuarter(quarterYearInput.getQuarter());
          decommissionedWell.setPlugAbandonmentDateYear(Integer.parseInt(quarterYearInput.getYear()));
        });

    decommissionedWell.setPlugAbandonmentDateType(form.getPlugAbandonmentDateType());

    searchSelectorService.mapSearchSelectorFormEntryToEntity(
        form.getOperationalStatus(),
        WellOperationalStatus.values(),
        decommissionedWell::setManualOperationalStatus,
        decommissionedWell::setOperationalStatus
    );

    searchSelectorService.mapSearchSelectorFormEntryToEntity(
        form.getMechanicalStatus(),
        WellMechanicalStatus.values(),
        decommissionedWell::setManualMechanicalStatus,
        decommissionedWell::setMechanicalStatus
    );

    return decommissionedWellRepository.save(decommissionedWell);
  }

  public Map<String, String> getPreSelectedType(DecommissionedWellForm form) {
    return searchSelectorService.getPreSelectedSearchSelectorValue(form.getType(), DecommissionedWellType.values());
  }

  public Map<String, String> getPreSelectedOperationalStatus(DecommissionedWellForm form) {
    return searchSelectorService.getPreSelectedSearchSelectorValue(
        form.getOperationalStatus(),
        WellOperationalStatus.values()
    );
  }

  public Map<String, String> getPreSelectedMechanicalStatus(DecommissionedWellForm form) {
    return searchSelectorService.getPreSelectedSearchSelectorValue(
        form.getMechanicalStatus(),
        WellMechanicalStatus.values()
    );
  }

  public DecommissionedWellForm getForm(Integer decommissionedWellId, ProjectDetail projectDetail) {
    return getForm(getDecommissionedWell(decommissionedWellId, projectDetail));
  }

  private DecommissionedWellForm getForm(DecommissionedWell decommissionedWell) {
    var form = new DecommissionedWellForm();

    var type = searchSelectorService.getManualOrStandardSelection(
        decommissionedWell.getManualType(),
        decommissionedWell.getType()
    );
    form.setType(type);

    form.setNumberToBeDecommissioned(decommissionedWell.getNumberToBeDecommissioned());
    form.setPlugAbandonmentDate(new QuarterYearInput(
        decommissionedWell.getPlugAbandonmentDateQuarter(),
        StringDisplayUtil.getValueAsStringOrNull(decommissionedWell.getPlugAbandonmentDateYear())
    ));
    form.setPlugAbandonmentDateType(decommissionedWell.getPlugAbandonmentDateType());

    var operationalStatus = searchSelectorService.getManualOrStandardSelection(
        decommissionedWell.getManualOperationalStatus(),
        decommissionedWell.getOperationalStatus()
    );
    form.setOperationalStatus(operationalStatus);

    var mechanicalStatus = searchSelectorService.getManualOrStandardSelection(
        decommissionedWell.getManualMechanicalStatus(),
        decommissionedWell.getMechanicalStatus()
    );
    form.setMechanicalStatus(mechanicalStatus);

    return form;
  }

  private DecommissionedWell getDecommissionedWell(Integer decommissionedWellId, ProjectDetail projectDetail) {
    return decommissionedWellRepository.findByIdAndProjectDetail(decommissionedWellId, projectDetail)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format(
                "No DecommissionedWell found with id %s for ProjectDetail with id %s",
                decommissionedWellId,
                projectDetail != null ? projectDetail.getId() : "null"
            )
        ));
  }

  public List<DecommissionedWell> getDecommissionedWellsForProjectDetail(ProjectDetail projectDetail) {
    return decommissionedWellRepository.findByProjectDetailOrderByIdAsc(projectDetail);
  }

  @Override
  public boolean isComplete(ProjectDetail detail) {
    return false;
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail) {
    return projectSetupService.taskSelectedForProjectDetail(detail, ProjectTask.WELLS);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    final var decommissionedWells = getDecommissionedWellsForProjectDetail(projectDetail);
    decommissionedWellRepository.deleteAll(decommissionedWells);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {
    entityDuplicationService.duplicateEntitiesAndSetNewParent(
        getDecommissionedWellsForProjectDetail(fromDetail),
        toDetail,
        DecommissionedWell.class
    );
  }
}
