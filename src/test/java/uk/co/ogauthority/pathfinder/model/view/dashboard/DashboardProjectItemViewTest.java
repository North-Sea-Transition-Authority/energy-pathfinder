package uk.co.ogauthority.pathfinder.model.view.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.useraction.DashboardLink;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.testutil.DashboardProjectItemTestUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class DashboardProjectItemViewTest {

  private final DashboardProjectItem dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();

  @Test
  public void from_allFieldsSetCorrectly() {
    var view = DashboardProjectItemView.from(dashboardProjectItem);
    var title = dashboardProjectItem.getProjectTitle();
    var screenReaderText = String.format(DashboardProjectItemView.SCREEN_READER_TEXT, DateUtil.formatInstant(dashboardProjectItem.getCreatedDatetime()));

    assertThat(view.getProjectTitle()).isEqualTo(title);
    assertCommonFieldsMatch(dashboardProjectItem, view);
    assertLinkMatches(
        dashboardProjectItem,
        DashboardProjectItemView.getLink(dashboardProjectItem, title, screenReaderText),
        title,
        getTaskListUrl(dashboardProjectItem.getProjectId())
    );
  }

  @Test
  public void from_allFieldsSetCorrectly_noTitle() {
    dashboardProjectItem.setProjectTitle(null);
    var view = DashboardProjectItemView.from(dashboardProjectItem);
    var title = String.format(DashboardProjectItemView.TITLE_PLACEHOLDER, dashboardProjectItem.getStatus().getDisplayName(), DateUtil.formatInstant(dashboardProjectItem.getCreatedDatetime()));
    var screenReaderText = String.format(DashboardProjectItemView.SCREEN_READER_TEXT, DateUtil.formatInstant(dashboardProjectItem.getCreatedDatetime()));

    assertThat(view.getProjectTitle()).isEqualTo(title);
    assertCommonFieldsMatch(dashboardProjectItem, view);
    assertLinkMatches(
        dashboardProjectItem,
        DashboardProjectItemView.getLink(dashboardProjectItem, title, screenReaderText),
        title,
        getTaskListUrl(dashboardProjectItem.getProjectId())
    );
  }

  @Test
  public void from_allFieldsSetCorrectly_whenQA() {
    var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem(ProjectStatus.QA);

    var view = DashboardProjectItemView.from(dashboardProjectItem);
    var title = dashboardProjectItem.getProjectTitle();
    var screenReaderText = String.format(DashboardProjectItemView.SCREEN_READER_TEXT, DateUtil.formatInstant(dashboardProjectItem.getCreatedDatetime()));

    assertThat(view.getProjectTitle()).isEqualTo(title);
    assertCommonFieldsMatch(dashboardProjectItem, view);
    assertLinkMatches(
        dashboardProjectItem,
        DashboardProjectItemView.getLink(dashboardProjectItem, title, screenReaderText),
        title,
        getProjectManagementPageUrl(dashboardProjectItem.getProjectId())
    );
  }

  @Test
  public void from_allFieldsSetCorrectly_whenPublished() {
    var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem(ProjectStatus.PUBLISHED);

    var view = DashboardProjectItemView.from(dashboardProjectItem);
    var title = dashboardProjectItem.getProjectTitle();
    var screenReaderText = String.format(DashboardProjectItemView.SCREEN_READER_TEXT, DateUtil.formatInstant(dashboardProjectItem.getCreatedDatetime()));

    assertThat(view.getProjectTitle()).isEqualTo(title);
    assertCommonFieldsMatch(dashboardProjectItem, view);
    assertLinkMatches(
        dashboardProjectItem,
        DashboardProjectItemView.getLink(dashboardProjectItem, title, screenReaderText),
        title,
        getProjectManagementPageUrl(dashboardProjectItem.getProjectId())
    );
  }



  @Test
  public void getLink() {
    var title = dashboardProjectItem.getProjectTitle();
    var screenReaderText = String.format(DashboardProjectItemView.SCREEN_READER_TEXT, DateUtil.formatInstant(dashboardProjectItem.getCreatedDatetime()));
    assertLinkMatches(
        dashboardProjectItem,
        DashboardProjectItemView.getLink(dashboardProjectItem, title, screenReaderText),
        title,
        getTaskListUrl(dashboardProjectItem.getProjectId())
    );
  }

  @Test
  public void getLink_placeholderTitle() {
    var title = String.format(DashboardProjectItemView.TITLE_PLACEHOLDER, dashboardProjectItem.getStatus().getDisplayName(), DateUtil.formatInstant(dashboardProjectItem.getCreatedDatetime()));
    var screenReaderText = String.format(DashboardProjectItemView.SCREEN_READER_TEXT, DateUtil.formatInstant(dashboardProjectItem.getCreatedDatetime()));
    assertLinkMatches(
        dashboardProjectItem,
        DashboardProjectItemView.getLink(dashboardProjectItem, title, screenReaderText),
        title,
        getTaskListUrl(dashboardProjectItem.getProjectId())
    );
  }

  private void assertCommonFieldsMatch(DashboardProjectItem dashboardProjectItem, DashboardProjectItemView view) {
    assertThat(view.getFieldStage()).isEqualTo(
        dashboardProjectItem.getFieldStage() != null ? dashboardProjectItem.getFieldStage().getDisplayName() : ""
    );
    assertThat(view.getFieldName()).isEqualTo(dashboardProjectItem.getFieldName());
    assertThat(view.getOperatorName()).isEqualTo(dashboardProjectItem.getOperatorName());
    assertThat(view.getStatus()).isEqualTo(dashboardProjectItem.getStatus().getDisplayName());
    assertThat(view.isUpdateRequested()).isEqualTo(dashboardProjectItem.isUpdateRequested());
    assertThat(view.getUpdateDeadlineDate()).isEqualTo(DateUtil.formatDate(dashboardProjectItem.getUpdateDeadlineDate()));
  }

  private void assertLinkMatches(DashboardProjectItem dashboardProjectItem, DashboardLink link, String prompt, String url) {
    assertThat(link.getScreenReaderText()).isEqualTo(String.format(
        DashboardProjectItemView.SCREEN_READER_TEXT, DateUtil.formatInstant(dashboardProjectItem.getCreatedDatetime())
    ));
    assertThat(link.getPrompt()).isEqualTo(prompt);
    assertThat(link.getUrl()).isEqualTo(url);
  }

  private String getTaskListUrl(Integer projectId) {
    return ReverseRouter.route(on(TaskListController.class).viewTaskList(projectId, null));
  }

  private String getProjectManagementPageUrl(Integer projectId) {
    return ReverseRouter.route(on(ManageProjectController.class).getProject(projectId, null, null, null));
  }
}
