package uk.co.ogauthority.pathfinder.service.project.plugabandonmentschedule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentSchedule;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentWell;
import uk.co.ogauthority.pathfinder.model.entity.wellbore.Wellbore;
import uk.co.ogauthority.pathfinder.model.form.project.plugabandonmentschedule.PlugAbandonmentScheduleForm;
import uk.co.ogauthority.pathfinder.model.view.wellbore.WellboreView;
import uk.co.ogauthority.pathfinder.repository.project.plugabandonmentschedule.PlugAbandonmentWellRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.wellbore.WellboreService;

@Service
public class PlugAbandonmentWellService {

  private final PlugAbandonmentWellRepository plugAbandonmentWellRepository;
  private final WellboreService wellboreService;
  private final EntityDuplicationService entityDuplicationService;

  @Autowired
  public PlugAbandonmentWellService(
      PlugAbandonmentWellRepository plugAbandonmentWellRepository,
      WellboreService wellboreService,
      EntityDuplicationService entityDuplicationService) {
    this.plugAbandonmentWellRepository = plugAbandonmentWellRepository;
    this.wellboreService = wellboreService;
    this.entityDuplicationService = entityDuplicationService;
  }

  @Transactional
  public void setPlugAbandonmentScheduleWells(PlugAbandonmentSchedule plugAbandonmentSchedule, List<Integer> wellboreIds) {
    var wellbores = getWellbores(wellboreIds);
    var plugAbandonmentWells = new ArrayList<PlugAbandonmentWell>();

    for (Wellbore wellbore : wellbores) {
      var plugAbandonmentWell = new PlugAbandonmentWell();
      plugAbandonmentWell.setPlugAbandonmentSchedule(plugAbandonmentSchedule);
      plugAbandonmentWell.setWellbore(wellbore);
      plugAbandonmentWells.add(plugAbandonmentWell);
    }

    plugAbandonmentWellRepository.deleteAllByPlugAbandonmentSchedule(plugAbandonmentSchedule);
    plugAbandonmentWellRepository.saveAll(plugAbandonmentWells);
  }

  @Transactional
  public void deletePlugAbandonmentScheduleWells(PlugAbandonmentSchedule plugAbandonmentSchedule) {
    plugAbandonmentWellRepository.deleteAllByPlugAbandonmentSchedule(plugAbandonmentSchedule);
  }

  public void deletePlugAbandonmentScheduleWells(List<PlugAbandonmentSchedule> plugAbandonmentSchedules) {
    plugAbandonmentSchedules.forEach(this::deletePlugAbandonmentScheduleWells);
  }

  public List<PlugAbandonmentWell> getPlugAbandonmentWells(PlugAbandonmentSchedule plugAbandonmentSchedule) {
    return plugAbandonmentWellRepository.findAllByPlugAbandonmentSchedule(plugAbandonmentSchedule);
  }

  public List<WellboreView> getWellboreViewsFromScheduleSorted(PlugAbandonmentSchedule plugAbandonmentSchedule) {
    var wellbores = plugAbandonmentWellRepository.findAllByPlugAbandonmentSchedule(plugAbandonmentSchedule)
        .stream()
        .map(PlugAbandonmentWell::getWellbore)
        .collect(Collectors.toList());

    return getWellboreViewsSorted(wellbores);
  }

  public List<WellboreView> getWellboreViewsFromFormSorted(PlugAbandonmentScheduleForm form) {
    return getWellboreViewsSorted(getWellbores(form.getWells()));
  }

  private List<WellboreView> getWellboreViewsSorted(List<Wellbore> wellbores) {
    return wellbores.stream()
        .map(this::convertToWellboreView)
        .sorted(Comparator.comparing(WellboreView::getSortKey))
        .collect(Collectors.toList());
  }

  private List<Wellbore> getWellbores(List<Integer> wellboreIds) {
    return wellboreService.getWellboresByIdsIn(wellboreIds);
  }

  private WellboreView convertToWellboreView(Wellbore wellbore) {
    return new WellboreView(wellbore, true);
  }

  public void copyPlugAbandonmentWells(Map<PlugAbandonmentSchedule, PlugAbandonmentSchedule> duplicatedPlugAbandonmentScheduleLookup) {
    duplicatedPlugAbandonmentScheduleLookup.forEach((originalPlugAbandonmentSchedule, duplicatedPlugAbandonmentSchedule) ->
        entityDuplicationService.duplicateEntitiesAndSetNewParent(
            getPlugAbandonmentWells(originalPlugAbandonmentSchedule),
            duplicatedPlugAbandonmentSchedule,
            PlugAbandonmentWell.class
        )
    );
  }
}
