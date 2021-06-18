package uk.co.ogauthority.pathfinder.service.projectmanagement.notification;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.model.view.projectupdate.RegulatorUpdateRequestView;
import uk.co.ogauthority.pathfinder.model.view.projectupdate.RegulatorUpdateRequestViewUtil;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorUpdateRequestService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUpdateTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementNotificationSectionServiceTest {

  @Mock
  private ProjectService projectService;

  @Mock
  private RegulatorUpdateRequestService regulatorUpdateRequestService;

  @Mock
  private WebUserAccountService webUserAccountService;

  @Mock
  private ServiceProperties serviceProperties;

  private ProjectManagementNotificationSectionService projectManagementNotificationSectionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectManagementNotificationSectionService = new ProjectManagementNotificationSectionService(
        projectService,
        regulatorUpdateRequestService,
        webUserAccountService,
        serviceProperties
    );
  }

  @Test
  public void getSection_whenUpdateInProgressAndNoRegulatorUpdateRequest() {

    var latestProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    when(projectService.getLatestDetailOrError(projectDetail.getProject().getId())).thenReturn(latestProjectDetail);

    var updateCreatedByUser = UserTestingUtil.getAuthenticatedUserAccount();

    when(webUserAccountService.getWebUserAccountOrError(latestProjectDetail.getCreatedByWua())).thenReturn(updateCreatedByUser);

    var section = projectManagementNotificationSectionService.getSection(projectDetail, authenticatedUser);
    assertCommonModelProperties(section);

    assertThat(section.getTemplateModel()).containsExactly(
        entry("showRegulatorUpdateRequestNotification", false),
        entry("updateCreatedByUserName", updateCreatedByUser.getFullName()),
        entry("updateCreatedByUserEmailAddress", updateCreatedByUser.getEmailAddress()),
        entry("showUpdateInProgressNotification", true),
        entry(ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR, projectDetail.getProjectType().getDisplayName()),
        entry(ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR, projectDetail.getProjectType().getLowercaseDisplayName())
    );

    verify(regulatorUpdateRequestService, never()).getUpdateRequest(latestProjectDetail);
  }

  @Test
  public void getSection_whenNoUpdateInProgressAndRegulatorUpdateRequest() {

    var latestProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.PUBLISHED);

    when(projectService.getLatestSubmittedDetailOrError(projectDetail.getProject().getId())).thenReturn(latestProjectDetail);

    var regulatorUpdateRequest = ProjectUpdateTestUtil.createRegulatorUpdateRequest();
    when(regulatorUpdateRequestService.getUpdateRequest(latestProjectDetail)).thenReturn(Optional.of(regulatorUpdateRequest));

    var requestedByUser = UserTestingUtil.getAuthenticatedUserAccount();
    when(webUserAccountService.getWebUserAccountOrError(regulatorUpdateRequest.getRequestedByWuaId())).thenReturn(requestedByUser);

    var section = projectManagementNotificationSectionService.getSection(projectDetail, authenticatedUser);

    assertModelPropertiesWhenRegulatorUpdateRequest(
        section,
        true,
        false,
        RegulatorUpdateRequestViewUtil.from(regulatorUpdateRequest, requestedByUser)
    );
  }

  @Test
  public void getSection_whenUpdateInProgressAndRegulatorUpdateRequest() {

    var latestProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);
    
    when(projectService.getLatestSubmittedDetailOrError(projectDetail.getProject().getId())).thenReturn(latestProjectDetail);

    var regulatorUpdateRequest = ProjectUpdateTestUtil.createRegulatorUpdateRequest();
    when(regulatorUpdateRequestService.getUpdateRequest(latestProjectDetail)).thenReturn(Optional.of(regulatorUpdateRequest));

    var requestedByUser = UserTestingUtil.getAuthenticatedUserAccount();
    when(webUserAccountService.getWebUserAccountOrError(regulatorUpdateRequest.getRequestedByWuaId())).thenReturn(requestedByUser);

    var section = projectManagementNotificationSectionService.getSection(projectDetail, authenticatedUser);

    assertModelPropertiesWhenRegulatorUpdateRequest(
        section,
        true,
        false,
        RegulatorUpdateRequestViewUtil.from(regulatorUpdateRequest, requestedByUser)
    );
  }

  @Test
  public void getSection_whenNoUpdateInProgressAndNoRegulatorUpdateRequest() {
    var latestProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.PUBLISHED);

    when(projectService.getLatestDetailOrError(projectDetail.getProject().getId())).thenReturn(latestProjectDetail);
    when(projectService.getLatestSubmittedDetailOrError(projectDetail.getProject().getId())).thenReturn(latestProjectDetail);
    when(regulatorUpdateRequestService.getUpdateRequest(latestProjectDetail)).thenReturn(Optional.empty());

    var section = projectManagementNotificationSectionService.getSection(projectDetail, authenticatedUser);
    assertCommonModelProperties(section);

    assertThat(section.getTemplateModel()).containsExactly(
        entry("showRegulatorUpdateRequestNotification", false),
        entry("showUpdateInProgressNotification", false),
        entry(ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR, projectDetail.getProjectType().getDisplayName()),
        entry(ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR, projectDetail.getProjectType().getLowercaseDisplayName())
    );
  }

  @Test
  public void getUpdateRequestTitle_whenDeadlineDateInFuture_thenFutureText() {
    final var daysToDeadlineDate = 1;
    final var dayText = StringDisplayUtil.pluralise("day", daysToDeadlineDate);
    var regulatorUpdateRequest = ProjectUpdateTestUtil.createRegulatorUpdateRequest();
    regulatorUpdateRequest.setDeadlineDate(LocalDate.now().plusDays(daysToDeadlineDate));

    final var returnedRequestTitle = projectManagementNotificationSectionService.getUpdateRequestTitle(regulatorUpdateRequest);
    assertThat(returnedRequestTitle).isEqualTo(String.format(ProjectManagementNotificationSectionService.FUTURE_UPDATE_REQUEST_TITLE, daysToDeadlineDate, dayText));
  }

  @Test
  public void getUpdateRequestTitle_whenDeadlineDateToday_thenTodayText() {
    var regulatorUpdateRequest = ProjectUpdateTestUtil.createRegulatorUpdateRequest();
    regulatorUpdateRequest.setDeadlineDate(LocalDate.now());

    final var returnedRequestTitle = projectManagementNotificationSectionService.getUpdateRequestTitle(regulatorUpdateRequest);
    assertThat(returnedRequestTitle).isEqualTo(ProjectManagementNotificationSectionService.TODAY_UPDATE_REQUEST_TITLE);
  }

  @Test
  public void getUpdateRequestTitle_whenDeadlineDateInPast_thenPastText() {
    final var daysPastDeadlineDate = 10;
    final var dayText = StringDisplayUtil.pluralise("day", daysPastDeadlineDate);
    var regulatorUpdateRequest = ProjectUpdateTestUtil.createRegulatorUpdateRequest();
    regulatorUpdateRequest.setDeadlineDate(LocalDate.now().minusDays(daysPastDeadlineDate));

    final var returnedRequestTitle = projectManagementNotificationSectionService.getUpdateRequestTitle(regulatorUpdateRequest);
    assertThat(returnedRequestTitle).isEqualTo(String.format(ProjectManagementNotificationSectionService.PAST_UPDATE_REQUEST_TITLE, daysPastDeadlineDate, dayText));
  }

  @Test
  public void getUpdateRequestTitle_whenNoDeadlineDate_thenEmptyString() {
    var regulatorUpdateRequest = ProjectUpdateTestUtil.createRegulatorUpdateRequest();
    regulatorUpdateRequest.setDeadlineDate(null);

    final var returnedRequestTitle = projectManagementNotificationSectionService.getUpdateRequestTitle(regulatorUpdateRequest);
    assertThat(returnedRequestTitle).isEmpty();
  }

  // This test is to ensure that when the deadline date is more than 1 month in the future
  // that the days between today and the deadline are correctly returned. A bug was spotted in
  // a previous implementation whereby the Period.class was being used and that resulted in a value in months being
  // returned instead of a value in days. This test is to ensure this erroneous behaviour is never reintroduced
  @Test
  public void getUpdateRequest_whenDateMoreThanOneMonthInFuture_thenFutureText() {
    final var daysToDeadlineDate = 60;
    final var dayText = StringDisplayUtil.pluralise("day", daysToDeadlineDate);
    var regulatorUpdateRequest = ProjectUpdateTestUtil.createRegulatorUpdateRequest();
    regulatorUpdateRequest.setDeadlineDate(LocalDate.now().plusDays(daysToDeadlineDate));

    final var returnedRequestTitle = projectManagementNotificationSectionService.getUpdateRequestTitle(regulatorUpdateRequest);
    assertThat(returnedRequestTitle).isEqualTo(String.format(ProjectManagementNotificationSectionService.FUTURE_UPDATE_REQUEST_TITLE, daysToDeadlineDate, dayText));
  }

  private void assertCommonModelProperties(ProjectManagementSection projectManagementSection) {
    assertThat(projectManagementSection.getTemplatePath()).isEqualTo(ProjectManagementNotificationSectionService.TEMPLATE_PATH);
    assertThat(projectManagementSection.getDisplayOrder()).isEqualTo(ProjectManagementNotificationSectionService.DISPLAY_ORDER);
    assertThat(projectManagementSection.getSectionType()).isEqualTo(ProjectManagementNotificationSectionService.SECTION_TYPE);
  }

  private void assertModelPropertiesWhenRegulatorUpdateRequest(ProjectManagementSection projectManagementSection,
                                                               boolean showRegulatorUpdateRequestNotification,
                                                               boolean showUpdateInProgressNotification,
                                                               RegulatorUpdateRequestView regulatorUpdateRequestView) {

    assertCommonModelProperties(projectManagementSection);

    final var templateModel = projectManagementSection.getTemplateModel();

    assertThat(templateModel).containsOnlyKeys(
        "showRegulatorUpdateRequestNotification",
        "regulatorUpdateRequestView",
        "service",
        "titleString",
        "showUpdateInProgressNotification",
        ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR,
        ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR
    )
        .containsEntry("showRegulatorUpdateRequestNotification", showRegulatorUpdateRequestNotification)
        .containsEntry("regulatorUpdateRequestView", regulatorUpdateRequestView)
        .containsEntry("service", serviceProperties)
        .containsEntry("showUpdateInProgressNotification", showUpdateInProgressNotification)
        .containsEntry(ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR, projectDetail.getProjectType().getDisplayName())
        .containsEntry(ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR, projectDetail.getProjectType().getLowercaseDisplayName());

    assertThat(templateModel.get("titleString")).isNotNull();
  }
}
