package uk.co.ogauthority.pathfinder.service.project.commissionedwell;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWell;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWellSchedule;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.repository.project.commissionedwell.CommissionedWellRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.wellbore.WellboreService;
import uk.co.ogauthority.pathfinder.testutil.CommissionedWellTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.WellboreTestUtil;

@ExtendWith(MockitoExtension.class)
class CommissionedWellServiceTest {

  @Mock
  private WellboreService wellboreService;

  @Mock
  private CommissionedWellRepository commissionedWellRepository;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private CommissionedWellService commissionedWellService;

  @BeforeEach
  void setup() {
    commissionedWellService = new CommissionedWellService(
        wellboreService,
        commissionedWellRepository,
        entityDuplicationService
    );
  }

  @Test
  void saveCommissionedWells_whenWellboresToCommission_verifyInteractions() {

    var commissionedWellSchedule = new CommissionedWellSchedule();
    var wellsToCommission = List.of(1, 2);

    var expectedWellbore1 = WellboreTestUtil.createWellbore(1);
    var expectedWellbore2 = WellboreTestUtil.createWellbore(2);

    when(wellboreService.getWellboresByIdsIn(wellsToCommission)).thenReturn(
        List.of(expectedWellbore1, expectedWellbore2)
    );

    commissionedWellService.saveCommissionedWells(
        commissionedWellSchedule,
        wellsToCommission
    );

    var expectedCommissionedWell1 = new CommissionedWell();
    expectedCommissionedWell1.setCommissionedWellSchedule(commissionedWellSchedule);
    expectedCommissionedWell1.setWellbore(expectedWellbore1);

    var expectedCommissionedWell2 = new CommissionedWell();
    expectedCommissionedWell2.setCommissionedWellSchedule(commissionedWellSchedule);
    expectedCommissionedWell2.setWellbore(expectedWellbore2);

    var expectedCommissionedWells = List.of(
        expectedCommissionedWell1,
        expectedCommissionedWell2
    );

    verify(commissionedWellRepository, times(1)).deleteAllByCommissionedWellSchedule(
        commissionedWellSchedule
    );

    verify(commissionedWellRepository, times(1)).saveAll(expectedCommissionedWells);
  }

  @Test
  void saveCommissionedWells_whenNoWellboresToCommission_verifyInteractions() {

    var commissionedWellSchedule = new CommissionedWellSchedule();
    List<Integer> wellsToCommission = Collections.emptyList();

    when(wellboreService.getWellboresByIdsIn(wellsToCommission)).thenReturn(
        Collections.emptyList()
    );

    commissionedWellService.saveCommissionedWells(
        commissionedWellSchedule,
        wellsToCommission
    );

    verify(commissionedWellRepository, times(1)).deleteAllByCommissionedWellSchedule(
        commissionedWellSchedule
    );

    verify(commissionedWellRepository, times(1)).saveAll(Collections.emptyList());
  }

  @Test
  void getCommissionedWellsForSchedules_whenNoResults_thenEmptyList() {

    var commissionedWellSchedules = List.of(new CommissionedWellSchedule());

    when(commissionedWellRepository.findByCommissionedWellScheduleIn(commissionedWellSchedules))
        .thenReturn(Collections.emptyList());

    var resultingCommissionedWells = commissionedWellService.getCommissionedWellsForSchedules(commissionedWellSchedules);

    assertThat(resultingCommissionedWells).isEmpty();
  }

  @Test
  void getCommissionedWellsForSchedules_whenResults_thenPopulatedList() {

    var commissionedWellSchedules = List.of(new CommissionedWellSchedule());

    var expectedCommissionedWells = List.of(
        new CommissionedWell()
    );

    when(commissionedWellRepository.findByCommissionedWellScheduleIn(commissionedWellSchedules))
        .thenReturn(expectedCommissionedWells);

    var resultingCommissionedWells = commissionedWellService.getCommissionedWellsForSchedules(commissionedWellSchedules);

    assertThat(resultingCommissionedWells).isEqualTo(expectedCommissionedWells);

  }

  @Test
  void getCommissionedWellsForSchedule_whenNoResults_thenEmptyList() {

    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    when(commissionedWellRepository.findByCommissionedWellSchedule(commissionedWellSchedule))
        .thenReturn(Collections.emptyList());

    var resultingCommissionedWells = commissionedWellService.getCommissionedWellsForSchedule(commissionedWellSchedule);

    assertThat(resultingCommissionedWells).isEmpty();
  }

  @Test
  void getCommissionedWellsForSchedule_whenResults_thenPopulatedList() {

    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    var expectedCommissionedWells = List.of(
        CommissionedWellTestUtil.getCommissionedWell()
    );

    when(commissionedWellRepository.findByCommissionedWellSchedule(commissionedWellSchedule))
        .thenReturn(expectedCommissionedWells);

    var resultingCommissionedWells = commissionedWellService.getCommissionedWellsForSchedule(commissionedWellSchedule);

    assertThat(resultingCommissionedWells).isEqualTo(expectedCommissionedWells);
  }

  @Test
  void deleteCommissionedWells_singleScheduleVariant_verifyInteractions() {

    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    commissionedWellService.deleteCommissionedWells(commissionedWellSchedule);

    verify(commissionedWellRepository, times(1)).deleteAllByCommissionedWellSchedule(commissionedWellSchedule);
  }

  @Test
  void deleteCommissionedWells_multipleScheduleVariant_verifyInteractions() {

    var firstCommissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();
    firstCommissionedWellSchedule.setLatestCompletionYear(2020);

    var secondCommissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();
    firstCommissionedWellSchedule.setLatestCompletionYear(2021);

    commissionedWellService.deleteCommissionedWells(List.of(firstCommissionedWellSchedule, secondCommissionedWellSchedule));

    verify(commissionedWellRepository, times(1)).deleteAllByCommissionedWellScheduleIn(
        List.of(firstCommissionedWellSchedule, secondCommissionedWellSchedule)
    );
  }

  @Test
  void copyCommissionedWellsToNewSchedules_verifyInteractions() {

    var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    // GIVEN a commissioned well schedule with one well
    var firstOriginalCommissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule(
        fromProjectDetail,
        2020,
        2021
    );

    var firstScheduleCommissionedWell = CommissionedWellTestUtil.getCommissionedWell();
    firstScheduleCommissionedWell.setCommissionedWellSchedule(firstOriginalCommissionedWellSchedule);

    // AND another commissioned well schedule with two wells for the same project detail
    var secondOriginalCommissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule(
        fromProjectDetail,
        2022,
        2023
    );

    var secondScheduleCommissionedWell1 = CommissionedWellTestUtil.getCommissionedWell();
    secondScheduleCommissionedWell1.setCommissionedWellSchedule(secondOriginalCommissionedWellSchedule);
    secondScheduleCommissionedWell1.setId(10);

    var secondScheduleCommissionedWell2 = CommissionedWellTestUtil.getCommissionedWell();
    secondScheduleCommissionedWell2.setCommissionedWellSchedule(secondOriginalCommissionedWellSchedule);
    secondScheduleCommissionedWell2.setId(20);

    // AND we have already duplicated the well schedules from the source project detail
    var firstDuplicateCommissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule(
        toProjectDetail,
        firstOriginalCommissionedWellSchedule.getEarliestStartYear(),
        firstOriginalCommissionedWellSchedule.getLatestCompletionYear()
    );

    var secondDuplicateCommissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule(
        toProjectDetail,
        secondOriginalCommissionedWellSchedule.getEarliestStartYear(),
        secondOriginalCommissionedWellSchedule.getLatestCompletionYear()
    );

    var duplicatedEntityLookup = Map.of(
        firstOriginalCommissionedWellSchedule, firstDuplicateCommissionedWellSchedule,
        secondOriginalCommissionedWellSchedule, secondDuplicateCommissionedWellSchedule
    );

    // WHEN we copy the commissioned wells to the new schedules

    when(commissionedWellRepository.findByCommissionedWellScheduleIn(argThat(argument -> argument.containsAll(List.of(firstOriginalCommissionedWellSchedule, secondOriginalCommissionedWellSchedule)))))
        .thenReturn(List.of(
            firstScheduleCommissionedWell,
            secondScheduleCommissionedWell1,
            secondScheduleCommissionedWell2
        ));

    commissionedWellService.copyCommissionedWellsToNewSchedules(duplicatedEntityLookup);

    // THEN we will have an interaction with the entity duplication service for each of the schedules with the
    // correct number of commissioned wells
    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        List.of(firstScheduleCommissionedWell),
        duplicatedEntityLookup.get(firstOriginalCommissionedWellSchedule),
        CommissionedWell.class
    );

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        List.of(secondScheduleCommissionedWell1, secondScheduleCommissionedWell2),
        duplicatedEntityLookup.get(secondOriginalCommissionedWellSchedule),
        CommissionedWell.class
    );
  }

}