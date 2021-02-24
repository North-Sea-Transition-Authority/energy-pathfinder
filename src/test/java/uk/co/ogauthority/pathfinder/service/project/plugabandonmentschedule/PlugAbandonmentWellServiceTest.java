package uk.co.ogauthority.pathfinder.service.project.plugabandonmentschedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentSchedule;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentWell;
import uk.co.ogauthority.pathfinder.model.entity.wellbore.Wellbore;
import uk.co.ogauthority.pathfinder.model.form.project.plugabandonmentschedule.PlugAbandonmentScheduleForm;
import uk.co.ogauthority.pathfinder.model.view.wellbore.WellboreView;
import uk.co.ogauthority.pathfinder.repository.project.plugabandonmentschedule.PlugAbandonmentWellRepository;
import uk.co.ogauthority.pathfinder.service.wellbore.WellboreService;
import uk.co.ogauthority.pathfinder.testutil.PlugAbandonmentWellTestUtil;
import uk.co.ogauthority.pathfinder.testutil.WellboreTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class PlugAbandonmentWellServiceTest {

  @Mock
  private PlugAbandonmentWellRepository plugAbandonmentWellRepository;

  @Mock
  private WellboreService wellboreService;

  private PlugAbandonmentWellService plugAbandonmentWellService;

  @Before
  public void setup() {
    plugAbandonmentWellService = new PlugAbandonmentWellService(
        plugAbandonmentWellRepository,
        wellboreService
    );
  }

  @Test
  public void setPlugAbandonmentScheduleWells() {
    var plugAbandonmentSchedule = new PlugAbandonmentSchedule();
    var wellboreIds = List.of(1, 2);
    var wellbores = List.of(WellboreTestUtil.createWellbore(), WellboreTestUtil.createWellbore());

    when(wellboreService.getWellboresByIdsIn(wellboreIds)).thenReturn(wellbores);

    plugAbandonmentWellService.setPlugAbandonmentScheduleWells(plugAbandonmentSchedule, wellboreIds);

    verify(plugAbandonmentWellRepository, times(1)).deleteAllByPlugAbandonmentSchedule(plugAbandonmentSchedule);

    ArgumentCaptor<List<PlugAbandonmentWell>> plugAbandonmentWellsCaptor = ArgumentCaptor.forClass(List.class);
    verify(plugAbandonmentWellRepository, times(1)).saveAll(plugAbandonmentWellsCaptor.capture());
    List<PlugAbandonmentWell> plugAbandonmentWells = plugAbandonmentWellsCaptor.getValue();

    for (int i = 0; i < wellbores.size(); i++) {
      var wellbore = wellbores.get(i);
      var plugAbandonmentWell = plugAbandonmentWells.get(i);
      assertThat(plugAbandonmentWell.getPlugAbandonmentSchedule()).isEqualTo(plugAbandonmentSchedule);
      assertThat(plugAbandonmentWell.getWellbore()).isEqualTo(wellbore);
    }
  }

  @Test
  public void deletePlugAbandonmentScheduleWells_singlePlugAbandonmentSchedule() {
    var plugAbandonmentSchedule = new PlugAbandonmentSchedule();

    plugAbandonmentWellService.deletePlugAbandonmentScheduleWells(plugAbandonmentSchedule);

    verify(plugAbandonmentWellRepository, times(1)).deleteAllByPlugAbandonmentSchedule(plugAbandonmentSchedule);
  }

  @Test
  public void deletePlugAbandonmentScheduleWells_plugAbandonmentScheduleList() {
    var plugAbandonmentSchedules = List.of(
        new PlugAbandonmentSchedule(),
        new PlugAbandonmentSchedule()
    );

    plugAbandonmentWellService.deletePlugAbandonmentScheduleWells(plugAbandonmentSchedules);

    for (PlugAbandonmentSchedule plugAbandonmentSchedule : plugAbandonmentSchedules) {
      verify(plugAbandonmentWellRepository, times(1)).deleteAllByPlugAbandonmentSchedule(plugAbandonmentSchedule);
    }
  }

  @Test
  public void getPlugAbandonmentWells_whenExist_thenReturnPopulatedList() {
    var plugAbandonmentSchedule = new PlugAbandonmentSchedule();
    var plugAbandonmentWells = List.of(
        PlugAbandonmentWellTestUtil.createPlugAbandonmentWell(),
        PlugAbandonmentWellTestUtil.createPlugAbandonmentWell()
    );

    when(plugAbandonmentWellRepository.findAllByPlugAbandonmentSchedule(plugAbandonmentSchedule)).thenReturn(
        plugAbandonmentWells
    );

    assertThat(plugAbandonmentWellService.getPlugAbandonmentWells(plugAbandonmentSchedule)).isEqualTo(plugAbandonmentWells);
  }

  @Test
  public void getPlugAbandonmentWells_whenNoneExist_thenReturnEmptyList() {
    var plugAbandonmentSchedule = new PlugAbandonmentSchedule();

    when(plugAbandonmentWellRepository.findAllByPlugAbandonmentSchedule(plugAbandonmentSchedule)).thenReturn(
        Collections.emptyList()
    );

    assertThat(plugAbandonmentWellService.getPlugAbandonmentWells(plugAbandonmentSchedule)).isEmpty();
  }

  @Test
  public void getWellboreViewsFromScheduleSorted() {
    var plugAbandonmentSchedule = new PlugAbandonmentSchedule();

    when(plugAbandonmentWellRepository.findAllByPlugAbandonmentSchedule(plugAbandonmentSchedule)).thenReturn(
        PlugAbandonmentWellTestUtil.getUnorderedPlugAbandonmentWells()
    );

    var wellboreViews = plugAbandonmentWellService.getWellboreViewsFromScheduleSorted(plugAbandonmentSchedule);
    var orderedPlugAbandonmentWells = PlugAbandonmentWellTestUtil.getOrderedPlugAbandonmentWells();
    for (var i = 0; i < wellboreViews.size(); i++) {
      var wellboreView = wellboreViews.get(i);
      var plugAbandonmentWell = orderedPlugAbandonmentWells.get(i);
      assertWellboreViewMatchesWellbore(wellboreView, plugAbandonmentWell.getWellbore());
    }
  }

  @Test
  public void getWellboreViewsFromFormSorted() {
    var form = new PlugAbandonmentScheduleForm();
    var wellboreIds = List.of(1, 2, 3);
    form.setWells(wellboreIds);

    when(wellboreService.getWellboresByIdsIn(wellboreIds)).thenReturn(
        WellboreTestUtil.getUnorderedWellbores()
    );

    var wellboreViews = plugAbandonmentWellService.getWellboreViewsFromFormSorted(form);
    var orderedWellbores = WellboreTestUtil.getOrderedWellbores();
    for (var i = 0; i < wellboreViews.size(); i++) {
      var wellboreView = wellboreViews.get(i);
      var wellbore = orderedWellbores.get(i);
      assertWellboreViewMatchesWellbore(wellboreView, wellbore);
    }
  }

  private void assertWellboreViewMatchesWellbore(WellboreView wellboreView, Wellbore wellbore) {
    assertThat(wellboreView.getId()).isEqualTo(Integer.toString(wellbore.getId()));
    assertThat(wellboreView.getName()).isEqualTo(wellbore.getRegistrationNo());
    assertThat(wellboreView.isValid()).isTrue();
  }
}
