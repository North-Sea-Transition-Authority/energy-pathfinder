package uk.co.ogauthority.pathfinder.service.dashboard;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.dashboard.DashboardFilter;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.UkcsArea;
import uk.co.ogauthority.pathfinder.testutil.DashboardFilterTestUtil;
import uk.co.ogauthority.pathfinder.testutil.DashboardProjectItemTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class DashboardFilterServiceTest {

  private static final String OPERATOR = "Test operator";
  private static final String TITLE = "Test project";
  private static final FieldStage FIELD_STAGE = FieldStage.DEVELOPMENT;
  private static final String FIELD_NAME = "Filter Test Field";
  private static final ProjectStatus PROJECT_STATUS = ProjectStatus.PUBLISHED;
  private static final UkcsArea UKCS_AREA = UkcsArea.NNS;

  private static final DashboardProjectItem OPERATOR_ITEM = DashboardProjectItemTestUtil.getDashboardProjectItem(ProjectOperatorTestUtil.getOrgGroup(OPERATOR));
  private static final DashboardProjectItem TITLE_ITEM = DashboardProjectItemTestUtil.getDashboardProjectItem(TITLE);
  private static final DashboardProjectItem FIELD_STAGE_ITEM = DashboardProjectItemTestUtil.getDashboardProjectItem(FIELD_STAGE);
  private static final DashboardProjectItem FIELD_ITEM = DashboardProjectItemTestUtil.getDashboardProjectItem_withField(FIELD_NAME);
  private static final DashboardProjectItem STATUS_ITEM = DashboardProjectItemTestUtil.getDashboardProjectItem(PROJECT_STATUS);
  private static final DashboardProjectItem UKCS_ITEM = DashboardProjectItemTestUtil.getDashboardProjectItem(UKCS_AREA);

  private DashboardFilter filter;
  private List<DashboardProjectItem> dashboardProjectItems = List.of(
      OPERATOR_ITEM,
      TITLE_ITEM,
      FIELD_STAGE_ITEM,
      FIELD_ITEM,
      STATUS_ITEM,
      UKCS_ITEM
  );

  private DashboardFilterService dashboardFilterService;

  @Before
  public void setUp() throws Exception {
    dashboardFilterService = new DashboardFilterService();
    filter = DashboardFilterTestUtil.getEmptyFilter();
  }

  //OPERATOR_NAME
  @Test
  public void operatorMatches_whenFilterNotSet() {
    assertThat(dashboardFilterService.operatorMatches(OPERATOR_ITEM, filter)).isTrue();
  }

  @Test
  public void operatorMatches_whenFilterSet_filterMatches() {
    filter.setOperatorName(OPERATOR);
    assertThat(dashboardFilterService.operatorMatches(OPERATOR_ITEM, filter)).isTrue();
  }

  @Test
  public void operatorMatches_whenFilterSet_filterMatchesCaseInsensitive() {
    filter.setOperatorName(OPERATOR.toLowerCase());
    assertThat(dashboardFilterService.operatorMatches(OPERATOR_ITEM, filter)).isTrue();
  }

  @Test
  public void operatorMatches_whenFilterSet_filterDoesNotMatch() {
    filter.setOperatorName("Another operator name");
    assertThat(dashboardFilterService.operatorMatches(OPERATOR_ITEM, filter)).isFalse();
  }
  //TITLE
  @Test
  public void titleMatches_whenFilterNotSet() {
    assertThat(dashboardFilterService.titleMatches(TITLE_ITEM, filter)).isTrue();
  }

  @Test
  public void titleMatches_whenFilterSet_filterMatches() {
    filter.setProjectTitle(TITLE);
    assertThat(dashboardFilterService.titleMatches(TITLE_ITEM, filter)).isTrue();
  }

  @Test
  public void titleMatches_whenFilterSet_filterMatchesCaseInsensitive() {
    filter.setProjectTitle(TITLE.toLowerCase());
    assertThat(dashboardFilterService.titleMatches(TITLE_ITEM, filter)).isTrue();
  }

  @Test
  public void titleMatches_whenFilterSet_filterDoesNotMatch() {
    filter.setProjectTitle("Another project title");
    assertThat(dashboardFilterService.titleMatches(TITLE_ITEM, filter)).isFalse();
  }


  //FIELD STAGE
  @Test
  public void fieldStageMatches_whenFilterNotSet() {
    assertThat(dashboardFilterService.fieldStageMatches(FIELD_STAGE_ITEM, filter)).isTrue();
  }

  @Test
  public void fieldStageMatches_whenFilterSet_filterMatches() {
    filter.setFieldStages(List.of(FIELD_STAGE, FieldStage.ENERGY_TRANSITION));
    assertThat(dashboardFilterService.fieldStageMatches(FIELD_STAGE_ITEM, filter)).isTrue();
  }

  @Test
  public void fieldStageMatches_whenFilterSet_filterDoesNotMatch() {
    filter.setFieldStages(List.of(FieldStage.DISCOVERY, FieldStage.ENERGY_TRANSITION));
    assertThat(dashboardFilterService.fieldStageMatches(FIELD_STAGE_ITEM, filter)).isFalse();
  }


  //FIELD
  @Test
  public void fieldMatches_whenFilterNotSet() {
    assertThat(dashboardFilterService.fieldMatches(FIELD_ITEM, filter)).isTrue();
  }

  @Test
  public void fieldMatches_whenFilterSet_filterMatches() {
    filter.setField(FIELD_NAME);
    assertThat(dashboardFilterService.fieldMatches(FIELD_ITEM, filter)).isTrue();
  }

  @Test
  public void fieldMatches_whenFilterSet_filterMatchesCaseInsensitive() {
    filter.setField(FIELD_NAME.toLowerCase());
    assertThat(dashboardFilterService.fieldMatches(FIELD_ITEM, filter)).isTrue();
  }

  @Test
  public void fieldMatches_whenFilterSet_filterDoesNotMatch() {
    filter.setField("Another field name");
    assertThat(dashboardFilterService.fieldMatches(FIELD_ITEM, filter)).isFalse();
  }


  //UKCS AREA
  @Test
  public void UkcsAreaMatches_whenFilterNotSet() {
    assertThat(dashboardFilterService.ukcsAreaMatches(UKCS_ITEM, filter)).isTrue();
  }

  @Test
  public void UkcsAreaMatches_whenFilterSet_filterMatches() {
    filter.setUkcsAreas(List.of(UKCS_AREA, UkcsArea.CNS));
    assertThat(dashboardFilterService.ukcsAreaMatches(UKCS_ITEM, filter)).isTrue();
  }

  @Test
  public void UkcsAreaMatches_whenFilterSet_filterDoesNotMatch() {
    filter.setUkcsAreas(List.of(UkcsArea.IS, UkcsArea.CNS));
    assertThat(dashboardFilterService.ukcsAreaMatches(UKCS_ITEM, filter)).isFalse();
  }


  //STATUS
  @Test
  public void statusMatches_whenFilterNotSet() {
    assertThat(dashboardFilterService.statusMatches(STATUS_ITEM, filter)).isTrue();
  }

  @Test
  public void statusMatches_whenFilterSet_filterMatches() {
    filter.setProjectStatusList(List.of(PROJECT_STATUS, ProjectStatus.DRAFT));
    assertThat(dashboardFilterService.statusMatches(STATUS_ITEM, filter)).isTrue();
  }

  @Test
  public void statusMatches_whenFilterSet_filterDoesNotMatch() {
    filter.setProjectStatusList(List.of(ProjectStatus.QA, ProjectStatus.DRAFT));
    assertThat(dashboardFilterService.statusMatches(STATUS_ITEM, filter)).isFalse();
  }

  //FILTER
  @Test
  public void filter_blankFilter_allResultsReturned() {
    var results = dashboardFilterService.filter(dashboardProjectItems, filter);
    assertThat(results).containsExactlyElementsOf(dashboardProjectItems);
  }

  @Test
  public void filter_operatorMatches() {
    filter.setOperatorName(OPERATOR);
    var results = dashboardFilterService.filter(dashboardProjectItems, filter);

    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0)).isEqualTo(OPERATOR_ITEM);
  }

  @Test
  public void filter_titleMatches() {
    filter.setProjectTitle(TITLE);
    var results = dashboardFilterService.filter(dashboardProjectItems, filter);

    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0)).isEqualTo(TITLE_ITEM);
  }

  @Test
  public void filter_fieldStageMatches() {
    filter.setFieldStages(List.of(FIELD_STAGE));
    var results = dashboardFilterService.filter(dashboardProjectItems, filter);

    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0)).isEqualTo(FIELD_STAGE_ITEM);
  }

  @Test
  public void filter_fieldMatches() {
    filter.setField(FIELD_NAME);
    var results = dashboardFilterService.filter(dashboardProjectItems, filter);

    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0)).isEqualTo(FIELD_ITEM);
  }

  @Test
  public void filter_ukcsAreaMatches() {
    filter.setUkcsAreas(List.of(UKCS_AREA));
    var results = dashboardFilterService.filter(dashboardProjectItems, filter);

    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0)).isEqualTo(UKCS_ITEM);
  }

  @Test
  public void filter_statusMatches() {
    filter.setProjectStatusList(List.of(PROJECT_STATUS));
    var results = dashboardFilterService.filter(dashboardProjectItems, filter);

    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0)).isEqualTo(STATUS_ITEM);
  }

  @Test
  public void filter_allFieldsSet_matchesResult() {
    var dashboardItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    filter.setOperatorName(DashboardProjectItemTestUtil.ORGANISATION_GROUP.getName());
    filter.setProjectTitle(DashboardProjectItemTestUtil.PROJECT_TITLE);
    filter.setFieldStages(List.of(DashboardProjectItemTestUtil.FIELD_STAGE));
    filter.setField(DashboardProjectItemTestUtil.FIELD_NAME);
    filter.setUkcsAreas(List.of(DashboardProjectItemTestUtil.UKCS_AREA));
    filter.setProjectStatusList(List.of(DashboardProjectItemTestUtil.PROJECT_STATUS));

    var results = dashboardFilterService.filter(List.of(dashboardItem), filter);
    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0)).isEqualTo(dashboardItem);
  }

  @Test
  public void filter_allFieldsSet_oneMismatchedField_doesNotMatchResult() {
    var dashboardItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    filter.setOperatorName(DashboardProjectItemTestUtil.ORGANISATION_GROUP.getName());
    filter.setProjectTitle(TITLE);
    filter.setFieldStages(List.of(DashboardProjectItemTestUtil.FIELD_STAGE));
    filter.setField(DashboardProjectItemTestUtil.FIELD_NAME);
    filter.setUkcsAreas(List.of(DashboardProjectItemTestUtil.UKCS_AREA));
    filter.setProjectStatusList(List.of(DashboardProjectItemTestUtil.PROJECT_STATUS));

    var results = dashboardFilterService.filter(List.of(dashboardItem), filter);
    assertThat(results).isEmpty();
  }

  @Test
  public void filter_orderedCorrectly() {
    var firstDashboardItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    var secondDashboardItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    var thirdDashboardItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    firstDashboardItem.setSortKey(Instant.now().plus(2, ChronoUnit.DAYS));
    secondDashboardItem.setSortKey(Instant.now().plus(1, ChronoUnit.DAYS));

    var results = dashboardFilterService.filter(List.of(thirdDashboardItem, secondDashboardItem, firstDashboardItem), filter);
    assertThat(results).containsExactly(firstDashboardItem, secondDashboardItem, thirdDashboardItem);
  }
}
