package uk.co.ogauthority.pathfinder.service.email.projectupdate.updaterequested;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.requested.ProjectUpdateRequestedEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class UpdateRequestedEmailPropertyServiceTest {

  @Mock
  private TestUpdateRequestedEmailPropertyService testUpdateRequestedEmailPropertyService;

  @Mock
  private LinkService linkService;

  private UpdateRequestedEmailPropertyService updateRequestedEmailPropertyService;

  @Before
  public void setup() {
    updateRequestedEmailPropertyService = new UpdateRequestedEmailPropertyService(
        List.of(testUpdateRequestedEmailPropertyService),
        linkService
    );
  }

  @Test
  public void getUpdateRequestedEmailProperties_whenProjectTypeImplementation_assertCustomProperties() {

    final var supportedProjectType = ProjectType.INFRASTRUCTURE;

    final var projectDetail = ProjectUtil.getProjectDetails(supportedProjectType);

    when(testUpdateRequestedEmailPropertyService.getSupportedProjectType()).thenReturn(supportedProjectType);

    final var updateReason = "update reason";
    final var formattedDeadlineDate = "deadline date";
    final var projectUrl = "project url";

    final var customIntroText = "custom intro text";
    final var customSubjectText = "custom subject text";

    final var expectedEmailProperties = new HashMap<String, Object>();
    expectedEmailProperties.put("UPDATE_REASON", updateReason);
    expectedEmailProperties.put("DEADLINE_TEXT", String.format("An update is due by %s.", formattedDeadlineDate));
    expectedEmailProperties.put("PROJECT_URL", projectUrl);
    expectedEmailProperties.put(ProjectUpdateRequestedEmailProperties.UPDATE_REQUESTED_INTRO_TEXT_MAIL_MERGE_FIELD_NAME, customIntroText);
    expectedEmailProperties.put(ProjectUpdateRequestedEmailProperties.UPDATE_REQUESTED_SUBJECT_MAIL_MERGE_FIELD_NAME, customSubjectText);

    final var emailProperties = new TestUpdateRequestedEmailProperties(
        updateReason,
        formattedDeadlineDate,
        projectUrl,
        customIntroText,
        customSubjectText
    );

    when(testUpdateRequestedEmailPropertyService.getUpdateRequestedEmailProperties(
        projectDetail,
        updateReason,
        formattedDeadlineDate
    )).thenReturn(emailProperties);

    final var resultingEmailProperties = updateRequestedEmailPropertyService.getUpdateRequestedEmailProperties(
        projectDetail,
        updateReason,
        formattedDeadlineDate
    );

    assertThat(resultingEmailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailProperties);
  }

  @Test
  public void getUpdateRequestedEmailProperties_whenNoProjectTypeImplementation_assertGenericProperties() {

    final var supportedProjectType = ProjectType.INFRASTRUCTURE;
    final var unsupportedProjectType = ProjectType.FORWARD_WORK_PLAN;

    final var projectDetail = ProjectUtil.getProjectDetails(unsupportedProjectType);

    when(testUpdateRequestedEmailPropertyService.getSupportedProjectType()).thenReturn(supportedProjectType);

    final var updateReason = "update reason";
    final var formattedDeadlineDate = "deadline date";
    final var projectUrl = "project url";

    when(linkService.generateProjectManagementUrl(projectDetail.getProject())).thenReturn(projectUrl);

    final var expectedEmailProperties = new HashMap<String, Object>();
    expectedEmailProperties.put("UPDATE_REASON", updateReason);
    expectedEmailProperties.put("DEADLINE_TEXT", String.format("An update is due by %s.", formattedDeadlineDate));
    expectedEmailProperties.put("PROJECT_URL", projectUrl);
    expectedEmailProperties.put(
        ProjectUpdateRequestedEmailProperties.UPDATE_REQUESTED_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format("%s.", ProjectUpdateRequestedEmailProperties.DEFAULT_UPDATE_REQUESTED_INTRO_TEXT)
    );
    expectedEmailProperties.put(
        ProjectUpdateRequestedEmailProperties.UPDATE_REQUESTED_SUBJECT_MAIL_MERGE_FIELD_NAME,
        String.format("%s", ProjectUpdateRequestedEmailProperties.DEFAULT_UPDATE_REQUESTED_SUBJECT_TEXT)
    );

    final var resultingEmailProperties = updateRequestedEmailPropertyService.getUpdateRequestedEmailProperties(
        projectDetail,
        updateReason,
        formattedDeadlineDate
    );

    assertThat(resultingEmailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailProperties);
  }

  static class TestUpdateRequestedEmailProperties extends ProjectUpdateRequestedEmailProperties {

    private final String customIntroText;

    private final String customSubjectText;

    public TestUpdateRequestedEmailProperties(String updateReason,
                                              String deadlineDate,
                                              String projectUrl,
                                              String customIntroText,
                                              String customSubjectText) {
      super(updateReason, deadlineDate, projectUrl);
      this.customIntroText = customIntroText;
      this.customSubjectText = customSubjectText;
    }

    @Override
    public Map<String, Object> getEmailPersonalisation() {
      final var emailProperties = super.getEmailPersonalisation();
      emailProperties.put(ProjectUpdateRequestedEmailProperties.UPDATE_REQUESTED_INTRO_TEXT_MAIL_MERGE_FIELD_NAME, customIntroText);
      emailProperties.put(ProjectUpdateRequestedEmailProperties.UPDATE_REQUESTED_SUBJECT_MAIL_MERGE_FIELD_NAME, customSubjectText);
      return emailProperties;
    }
  }

}