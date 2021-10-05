package uk.co.ogauthority.pathfinder.model.view.dashboard.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorDisplayNameUtil;
import uk.co.ogauthority.pathfinder.testutil.DashboardProjectItemTestUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.DateUtil;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureProjectDashboardItemViewUtilTest {

  @Test
  public void from_whenAllPropertiesPopulated() {

    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();

    final var dashboardProjectItemView = InfrastructureProjectDashboardItemViewUtil.from(dashboardProjectItem);

    assertCommonViewProperties(dashboardProjectItemView, dashboardProjectItem);

    assertThat(dashboardProjectItemView.getFieldStage()).isEqualTo(dashboardProjectItem.getFieldStage().getDisplayName());
    assertThat(dashboardProjectItemView.getFieldName()).isEqualTo(dashboardProjectItem.getFieldName());
  }

  @Test
  public void from_whenFieldStageIsNull_thenNotSetText() {

    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    dashboardProjectItem.setFieldStage(null);

    final var dashboardProjectItemView = InfrastructureProjectDashboardItemViewUtil.from(dashboardProjectItem);

    assertThat(dashboardProjectItemView.getFieldStage()).isEqualTo(StringDisplayUtil.NOT_SET_TEXT);
    assertCommonViewProperties(dashboardProjectItemView, dashboardProjectItem);
  }

  @Test
  public void from_whenFieldNameIsNull_thenNotSetText() {

    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    dashboardProjectItem.setFieldName(null);

    final var dashboardProjectItemView = InfrastructureProjectDashboardItemViewUtil.from(dashboardProjectItem);

    assertThat(dashboardProjectItemView.getFieldName()).isEqualTo(StringDisplayUtil.NOT_SET_TEXT);
    assertCommonViewProperties(dashboardProjectItemView, dashboardProjectItem);
  }

  @Test
  public void from_whenTitleIsNull_thenLinkPromptIsPlaceholderText() {

    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    dashboardProjectItem.setProjectTitle(null);

    final var dashboardProjectItemView = InfrastructureProjectDashboardItemViewUtil.from(dashboardProjectItem);

    final var expectedProjectTitle = String.format(
        InfrastructureProjectDashboardItemViewUtil.TITLE_PLACEHOLDER,
        dashboardProjectItem.getStatus().getDisplayName(),
        DateUtil.formatInstant(dashboardProjectItem.getCreatedDatetime())
    );

    assertThat(dashboardProjectItemView.getDashboardLink().getPrompt()).isEqualTo(expectedProjectTitle);
    assertCommonViewProperties(dashboardProjectItemView, dashboardProjectItem);
  }

  @Test
  public void from_whenTitleIsNull_thenLinkScreenReaderTextIsEmpty() {

    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    dashboardProjectItem.setProjectTitle(null);

    final var dashboardProjectItemView = InfrastructureProjectDashboardItemViewUtil.from(dashboardProjectItem);

    assertThat(dashboardProjectItemView.getDashboardLink().getScreenReaderText()).isEmpty();
    assertCommonViewProperties(dashboardProjectItemView, dashboardProjectItem);
  }

  @Test
  public void from_whenTitleIsProvided_thenLinkScreenReaderTextIsPlaceholder() {

    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    dashboardProjectItem.setProjectTitle("title");

    final var dashboardProjectItemView = InfrastructureProjectDashboardItemViewUtil.from(dashboardProjectItem);

    final var expectedScreenReaderText = String.format(
        InfrastructureProjectDashboardItemViewUtil.SCREEN_READER_TEXT,
        DateUtil.formatInstant(dashboardProjectItem.getCreatedDatetime())
    );

    assertThat(dashboardProjectItemView.getDashboardLink().getScreenReaderText()).isEqualTo(expectedScreenReaderText);
    assertCommonViewProperties(dashboardProjectItemView, dashboardProjectItem);
  }

  @Test
  public void from_whenStatusIsDraftAndVersionIsOne_thenUrlIsTaskList() {

    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    dashboardProjectItem.setStatus(ProjectStatus.DRAFT);
    dashboardProjectItem.setVersion(1);

    assertDashboardItemUrlIsExpected(
        dashboardProjectItem,
        getTaskListUrl(dashboardProjectItem.getProjectId())
    );
  }

  @Test
  public void from_whenStatusIsDraftAndVersionIsNotOne_thenUrlIsManagementPage() {

    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    dashboardProjectItem.setStatus(ProjectStatus.DRAFT);
    dashboardProjectItem.setVersion(2);

    assertDashboardItemUrlIsExpected(
        dashboardProjectItem,
        getManagementPageUrl(dashboardProjectItem.getProjectId())
    );
  }

  @Test
  public void from_whenStatusIsQA_thenUrlIsManagementPage() {
    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    dashboardProjectItem.setStatus(ProjectStatus.QA);

    assertDashboardItemUrlIsExpected(
        dashboardProjectItem,
        getManagementPageUrl(dashboardProjectItem.getProjectId())
    );
  }

  @Test
  public void from_whenStatusIsPublished_thenUrlIsManagementPage() {
    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    dashboardProjectItem.setStatus(ProjectStatus.PUBLISHED);

    assertDashboardItemUrlIsExpected(
        dashboardProjectItem,
        getManagementPageUrl(dashboardProjectItem.getProjectId())
    );
  }

  @Test
  public void from_whenStatusIsArchived_thenUrlIsManagementPage() {
    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    dashboardProjectItem.setStatus(ProjectStatus.ARCHIVED);

    assertDashboardItemUrlIsExpected(
        dashboardProjectItem,
        getManagementPageUrl(dashboardProjectItem.getProjectId())
    );
  }

  @Test
  public void from_whenPublishedAsProjectOperator_thenOperatorNameIsOrganisationGroupName() {

    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();

    var projectOperatorGroup = TeamTestingUtil.generateOrganisationGroup(
        100,
        "organisation group name",
        "organisation group short name"
    );

    dashboardProjectItem.setOrganisationGroup(projectOperatorGroup);
    dashboardProjectItem.setPublishableOperator(null);

    var dashboardProjectItemView = InfrastructureProjectDashboardItemViewUtil.from(dashboardProjectItem);

    assertCommonViewProperties(dashboardProjectItemView, dashboardProjectItem);
    assertThat(dashboardProjectItemView.getOperatorName()).isEqualTo(dashboardProjectItem.getOperatorName());
  }

  @Test
  public void from_whenNotPublishedAsProjectOperator_thenOperatorNameIsOrganisationGroupAndPublishableOrganisation() {

    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();

    var projectOperatorGroup = TeamTestingUtil.generateOrganisationGroup(
        100,
        "organisation group name",
        "organisation group short name"
    );

    var publishableOperator = TeamTestingUtil.generateOrganisationUnit(
        10,
        "organisation name",
        projectOperatorGroup
    );

    dashboardProjectItem.setOrganisationGroup(projectOperatorGroup);
    dashboardProjectItem.setPublishableOperator(publishableOperator);

    var dashboardProjectItemView = InfrastructureProjectDashboardItemViewUtil.from(dashboardProjectItem);

    assertCommonViewProperties(dashboardProjectItemView, dashboardProjectItem);

    var expectedOperatorName = ProjectOperatorDisplayNameUtil.getProjectOperatorDisplayName(
        projectOperatorGroup,
        publishableOperator
    );

    assertThat(dashboardProjectItemView.getOperatorName()).isEqualTo(expectedOperatorName);
  }

  private void assertCommonViewProperties(InfrastructureProjectDashboardItemView dashboardProjectItemView,
                                          DashboardProjectItem dashboardProjectItem) {
    assertThat(dashboardProjectItemView.getProjectTitle()).isEqualTo(dashboardProjectItem.getProjectTitle());
    assertThat(dashboardProjectItemView.getStatus()).isEqualTo(dashboardProjectItem.getStatus().getDisplayName());
    assertThat(dashboardProjectItemView.isUpdateRequested()).isEqualTo(dashboardProjectItem.isUpdateRequested());
    assertThat(dashboardProjectItemView.getUpdateDeadlineDate()).isEqualTo(DateUtil.formatDate(dashboardProjectItem.getUpdateDeadlineDate()));
    assertThat(dashboardProjectItemView.getProjectType()).isEqualTo(dashboardProjectItem.getProjectType());
    assertThat(dashboardProjectItemView.getDashboardLink()).isNotNull();
  }

  private void assertDashboardItemUrlIsExpected(DashboardProjectItem dashboardProjectItem,
                                                String expectedUrl) {

    final var dashboardUrl = InfrastructureProjectDashboardItemViewUtil.from(dashboardProjectItem)
        .getDashboardLink()
        .getUrl();

    assertThat(dashboardUrl).isEqualTo(expectedUrl);
  }

  private String getTaskListUrl(int projectId) {
    return ControllerUtils.getBackToTaskListUrl(projectId);
  }

  private String getManagementPageUrl(int projectId) {
    return ControllerUtils.getProjectManagementUrl(projectId);
  }

}