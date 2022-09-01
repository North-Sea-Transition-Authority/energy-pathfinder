package uk.co.ogauthority.pathfinder.service.project.commissionedwell;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWell;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWellSchedule;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.commissionedwell.CommissionedWellForm;
import uk.co.ogauthority.pathfinder.repository.project.commissionedwell.CommissionedWellScheduleRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@Service
public class CommissionedWellScheduleService implements ProjectFormSectionService {

  private final ProjectSetupService projectSetupService;
  private final CommissionedWellScheduleRepository commissionedWellScheduleRepository;
  private final CommissionedWellService commissionedWellService;
  private final CommissionedWellScheduleValidationService commissionedWellScheduleValidationService;
  private final EntityDuplicationService entityDuplicationService;

  @Autowired
  public CommissionedWellScheduleService(
      ProjectSetupService projectSetupService,
      CommissionedWellScheduleRepository commissionedWellScheduleRepository,
      CommissionedWellService commissionedWellService,
      CommissionedWellScheduleValidationService commissionedWellScheduleValidationService,
      EntityDuplicationService entityDuplicationService
  ) {
    this.projectSetupService = projectSetupService;
    this.commissionedWellScheduleRepository = commissionedWellScheduleRepository;
    this.commissionedWellService = commissionedWellService;
    this.commissionedWellScheduleValidationService = commissionedWellScheduleValidationService;
    this.entityDuplicationService = entityDuplicationService;
  }

  @Transactional
  public void createCommissionWellSchedule(CommissionedWellForm form,
                                           ProjectDetail projectDetail) {
    var commissionedWellSchedule = new CommissionedWellSchedule();
    commissionedWellSchedule.setProjectDetail(projectDetail);
    updateCommissionedWellSchedule(commissionedWellSchedule, form);
  }

  @Transactional
  public void updateCommissionedWellSchedule(CommissionedWellSchedule commissionedWellSchedule,
                                             CommissionedWellForm commissionedWellForm) {
    var commissioningSchedule = commissionedWellForm.getCommissioningSchedule();

    var earliestStartYear = commissioningSchedule.getMinYear() != null
        ? Integer.parseInt(commissioningSchedule.getMinYear())
        : null;

    var latestCompletionYear = commissioningSchedule.getMaxYear() != null
        ? Integer.parseInt(commissioningSchedule.getMaxYear())
        : null;

    commissionedWellSchedule.setEarliestStartYear(earliestStartYear);
    commissionedWellSchedule.setLatestCompletionYear(latestCompletionYear);

    commissionedWellSchedule = commissionedWellScheduleRepository.save(commissionedWellSchedule);
    commissionedWellService.saveCommissionedWells(commissionedWellSchedule, commissionedWellForm.getWells());
  }

  public Optional<CommissionedWellSchedule> getCommissionedWellSchedule(int commissionedWellScheduleId) {
    return commissionedWellScheduleRepository.findById(commissionedWellScheduleId);
  }

  List<CommissionedWellSchedule> getCommissionedWellSchedules(ProjectDetail projectDetail) {
    return commissionedWellScheduleRepository.findByProjectDetailOrderByIdAsc(projectDetail);
  }

  public CommissionedWellForm getForm(CommissionedWellSchedule commissionedWellSchedule,
                                      List<CommissionedWell> commissionedWells) {

    var form = new CommissionedWellForm();

    form.setCommissioningSchedule(new MinMaxDateInput(
        StringDisplayUtil.getValueAsStringOrNull(commissionedWellSchedule.getEarliestStartYear()),
        StringDisplayUtil.getValueAsStringOrNull(commissionedWellSchedule.getLatestCompletionYear())
    ));

    form.setWells(
        commissionedWells
            .stream()
            .map(commissionedWell -> commissionedWell.getWellbore().getId())
            .collect(Collectors.toList())
    );

    return form;
  }

  public List<CommissionedWell> getCommissionedWellsForSchedule(CommissionedWellSchedule commissionedWellSchedule) {
    return commissionedWellService.getCommissionedWellsForSchedule(commissionedWellSchedule);
  }

  @Transactional
  public void deleteCommissionedWellSchedule(CommissionedWellSchedule commissionedWellSchedule) {
    commissionedWellService.deleteCommissionedWells(commissionedWellSchedule);
    commissionedWellScheduleRepository.delete(commissionedWellSchedule);
  }

  List<CommissionedWellSchedule> getCommissionedWellSchedulesByProjectAndVersion(Project project, Integer version) {
    return commissionedWellScheduleRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        project,
        version
    );
  }

  @Override
  public boolean isComplete(ProjectDetail detail) {

    var commissionedWellSchedules = getCommissionedWellSchedules(detail);
    var commissionedWellsForSchedules = commissionedWellService.getCommissionedWellsForSchedules(commissionedWellSchedules);

    var commissionedWellSchedulesAsForms = new ArrayList<CommissionedWellForm>();

    commissionedWellSchedules.forEach(commissionedWellSchedule -> {

      var commissionedWellForSchedule = commissionedWellsForSchedules
          .stream()
          .filter(commissionedWell -> commissionedWell.getCommissionedWellSchedule().equals(commissionedWellSchedule))
          .collect(Collectors.toUnmodifiableList());

      commissionedWellSchedulesAsForms.add(
          getForm(
              commissionedWellSchedule,
              commissionedWellForSchedule
          )
      );
    });

    return !commissionedWellSchedulesAsForms.isEmpty()
        && commissionedWellScheduleValidationService.areAllFormsValid(
            commissionedWellSchedulesAsForms,
            ValidationType.FULL
        );
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail) {
    return projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.COMMISSIONED_WELLS);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    var commissionedWellSchedules = getCommissionedWellSchedules(projectDetail);
    commissionedWellService.deleteCommissionedWells(commissionedWellSchedules);
    commissionedWellScheduleRepository.deleteAll(commissionedWellSchedules);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {
    var duplicatedCommissionedWellScheduleEntities = entityDuplicationService.duplicateEntitiesAndSetNewParent(
        getCommissionedWellSchedules(fromDetail),
        toDetail,
        CommissionedWellSchedule.class
    );

    var duplicatedCommissionedWellScheduleEntityMap = entityDuplicationService.createDuplicatedEntityPairingMap(
        duplicatedCommissionedWellScheduleEntities
    );

    commissionedWellService.copyCommissionedWellsToNewSchedules(duplicatedCommissionedWellScheduleEntityMap);
  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return Set.of(ProjectType.INFRASTRUCTURE);
  }
}
