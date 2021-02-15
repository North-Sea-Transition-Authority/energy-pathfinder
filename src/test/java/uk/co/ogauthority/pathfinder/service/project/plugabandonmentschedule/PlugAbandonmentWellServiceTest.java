package uk.co.ogauthority.pathfinder.service.project.plugabandonmentschedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
  public void getWellboreViews() {
    var plugAbandonmentSchedule = new PlugAbandonmentSchedule();
    var plugAbandonmentSchedule1 = PlugAbandonmentWellTestUtil.createPlugAbandonmentWell(
        WellboreTestUtil.createWellbore("16/01- 2")
    );
    var plugAbandonmentSchedule2 = PlugAbandonmentWellTestUtil.createPlugAbandonmentWell(
        WellboreTestUtil.createWellbore("16/01- 1")
    );

    when(plugAbandonmentWellRepository.findAllByPlugAbandonmentSchedule(plugAbandonmentSchedule)).thenReturn(List.of(
        plugAbandonmentSchedule1,
        plugAbandonmentSchedule2
    ));

    var wellboreViews = plugAbandonmentWellService.getWellboreViewsFromSchedule(plugAbandonmentSchedule);
    assertWellboreViewMatchesWellbore(wellboreViews.get(0), plugAbandonmentSchedule2);
    assertWellboreViewMatchesWellbore(wellboreViews.get(1), plugAbandonmentSchedule1);
  }

  @Test
  public void getWellboreViewsFromForm() {
    var form = new PlugAbandonmentScheduleForm();
    var wellboreIds = List.of(1, 2);
    form.setWells(wellboreIds);

    var wellbore1 = WellboreTestUtil.createWellbore("16/01- 2");
    var wellbore2 = WellboreTestUtil.createWellbore("16/01- 1");

    when(wellboreService.getWellboresByIdsIn(wellboreIds)).thenReturn(List.of(wellbore1, wellbore2));

    var wellboreViews = plugAbandonmentWellService.getWellboreViewsFromForm(form);
    assertWellboreViewMatchesWellbore(wellboreViews.get(0), wellbore2);
    assertWellboreViewMatchesWellbore(wellboreViews.get(1), wellbore1);
  }

  private void assertWellboreViewMatchesWellbore(WellboreView wellboreView, PlugAbandonmentWell plugAbandonmentWell) {
    assertWellboreViewMatchesWellbore(wellboreView, plugAbandonmentWell.getWellbore());
  }

  private void assertWellboreViewMatchesWellbore(WellboreView wellboreView, Wellbore wellbore) {
    assertThat(wellboreView.getId()).isEqualTo(Integer.toString(wellbore.getId()));
    assertThat(wellboreView.getName()).isEqualTo(wellbore.getRegistrationNo());
    assertThat(wellboreView.isValid()).isTrue();
  }
}
