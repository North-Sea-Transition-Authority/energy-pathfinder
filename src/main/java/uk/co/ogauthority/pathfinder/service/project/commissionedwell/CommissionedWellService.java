package uk.co.ogauthority.pathfinder.service.project.commissionedwell;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWell;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWellSchedule;
import uk.co.ogauthority.pathfinder.model.form.project.commissionedwell.CommissionedWellForm;
import uk.co.ogauthority.pathfinder.model.view.wellbore.WellboreView;
import uk.co.ogauthority.pathfinder.repository.project.commissionedwell.CommissionedWellRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.wellbore.WellboreService;

@Service
class CommissionedWellService {

  private final WellboreService wellboreService;
  private final CommissionedWellRepository commissionedWellRepository;
  private final EntityDuplicationService entityDuplicationService;

  @Autowired
  CommissionedWellService(WellboreService wellboreService,
                          CommissionedWellRepository commissionedWellRepository,
                          EntityDuplicationService entityDuplicationService) {
    this.wellboreService = wellboreService;
    this.commissionedWellRepository = commissionedWellRepository;
    this.entityDuplicationService = entityDuplicationService;
  }

  @Transactional
  public void saveCommissionedWells(CommissionedWellSchedule commissionedWellSchedule,
                                    List<Integer> wellsToCommission) {
    var wellboreList = wellboreService.getWellboresByIdsIn(wellsToCommission);

    var commissionedWellsToPersist = new ArrayList<CommissionedWell>();

    wellboreList.forEach(wellbore -> {
      var commissionedWell = new CommissionedWell();
      commissionedWell.setCommissionedWellSchedule(commissionedWellSchedule);
      commissionedWell.setWellbore(wellbore);
      commissionedWellsToPersist.add(commissionedWell);
    });

    commissionedWellRepository.deleteAllByCommissionedWellSchedule(commissionedWellSchedule);
    commissionedWellRepository.saveAll(commissionedWellsToPersist);
  }

  @Transactional
  public void deleteCommissionedWells(CommissionedWellSchedule commissionedWellSchedule) {
    commissionedWellRepository.deleteAllByCommissionedWellSchedule(commissionedWellSchedule);
  }

  @Transactional
  public void deleteCommissionedWells(List<CommissionedWellSchedule> commissionedWellSchedules) {
    commissionedWellRepository.deleteAllByCommissionedWellScheduleIn(commissionedWellSchedules);
  }

  List<CommissionedWell> getCommissionedWellsForSchedules(List<CommissionedWellSchedule> commissionedWellSchedules) {
    return commissionedWellRepository.findByCommissionedWellScheduleIn(commissionedWellSchedules);
  }

  List<CommissionedWell> getCommissionedWellsForSchedule(CommissionedWellSchedule commissionedWellSchedule) {
    return commissionedWellRepository.findByCommissionedWellSchedule(commissionedWellSchedule);
  }

  List<WellboreView> getWellboreViewsFromForm(CommissionedWellForm commissionedWellForm) {
    return wellboreService.getWellboresByIdsIn(commissionedWellForm.getWells())
        .stream()
        .map(wellbore -> new WellboreView(wellbore, true))
        .sorted(Comparator.comparing(WellboreView::getSortKey))
        .collect(Collectors.toList());
  }

  String getWellboreRestUrl() {
    return wellboreService.getWellboreRestUrl();
  }

  void copyCommissionedWellsToNewSchedules(
      Map<CommissionedWellSchedule, CommissionedWellSchedule> duplicatedCommissionedWellScheduleLookup
  ) {

    var commissionedWellScheduleWells = getCommissionedWellsForSchedules(
        new ArrayList<>(duplicatedCommissionedWellScheduleLookup.keySet())
    )
        .stream()
        .collect(Collectors.groupingBy(CommissionedWell::getCommissionedWellSchedule));

    duplicatedCommissionedWellScheduleLookup.forEach((originalCommissionedWellSchedule, duplicatedCommissionedWellSchedule) -> {

      var commissionedWellsForSchedule = commissionedWellScheduleWells.get(originalCommissionedWellSchedule);

      List<CommissionedWell> commissionedWellsToDuplicate = commissionedWellsForSchedule != null ? commissionedWellsForSchedule : List.of();

      entityDuplicationService.duplicateEntitiesAndSetNewParent(
          commissionedWellsToDuplicate,
          duplicatedCommissionedWellSchedule,
          CommissionedWell.class
      );
    });
  }
}
