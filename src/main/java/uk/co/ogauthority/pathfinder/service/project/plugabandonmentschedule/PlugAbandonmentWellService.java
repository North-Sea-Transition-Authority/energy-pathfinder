package uk.co.ogauthority.pathfinder.service.project.plugabandonmentschedule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
import uk.co.ogauthority.pathfinder.service.wellbore.WellboreService;

@Service
public class PlugAbandonmentWellService {

  private final PlugAbandonmentWellRepository plugAbandonmentWellRepository;
  private final WellboreService wellboreService;

  @Autowired
  public PlugAbandonmentWellService(
      PlugAbandonmentWellRepository plugAbandonmentWellRepository,
      WellboreService wellboreService) {
    this.plugAbandonmentWellRepository = plugAbandonmentWellRepository;
    this.wellboreService = wellboreService;
  }

  @Transactional
  public void setPlugAbandonmentScheduleWells(PlugAbandonmentSchedule plugAbandonmentSchedule, List<Integer> wellboreIds) {
    var wellbores = getWellbores(wellboreIds);
    var plugAbandonmentWells = new ArrayList<PlugAbandonmentWell>();

    wellbores.forEach((wellbore) -> {
      var plugAbandonmentWell = new PlugAbandonmentWell();
      plugAbandonmentWell.setPlugAbandonmentSchedule(plugAbandonmentSchedule);
      plugAbandonmentWell.setWellbore(wellbore);
      plugAbandonmentWells.add(plugAbandonmentWell);
    });

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

  public List<WellboreView> getWellboreViewsFromSchedule(PlugAbandonmentSchedule plugAbandonmentSchedule) {
    var wellbores = plugAbandonmentWellRepository.findAllByPlugAbandonmentSchedule(plugAbandonmentSchedule)
        .stream()
        .map(PlugAbandonmentWell::getWellbore)
        .collect(Collectors.toList());

    return getWellboreViews(wellbores);
  }

  public List<WellboreView> getWellboreViewsFromForm(PlugAbandonmentScheduleForm form) {
    return getWellboreViews(getWellbores(form.getWells()));
  }

  private List<WellboreView> getWellboreViews(List<Wellbore> wellbores) {
    return wellbores.stream()
        .map(this::convertToWellboreView)
        .sorted(Comparator.comparing(WellboreView::getName))
        .collect(Collectors.toList());
  }

  private List<Wellbore> getWellbores(List<Integer> wellboreIds) {
    return wellboreService.getWellboresByIdsIn(wellboreIds);
  }

  private WellboreView convertToWellboreView(Wellbore wellbore) {
    return new WellboreView(wellbore.getId(), wellbore.getRegistrationNo(), true);
  }
}
