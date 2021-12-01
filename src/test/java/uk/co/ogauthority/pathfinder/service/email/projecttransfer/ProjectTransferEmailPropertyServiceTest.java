package uk.co.ogauthority.pathfinder.service.email.projecttransfer;

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
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.IncomingOperatorProjectTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.OutgoingOperatorProjectTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.email.EmailLinkService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTransferEmailPropertyServiceTest {

  @Mock
  private TestProjectTransferEmailPropertyProvider testProjectTransferEmailPropertyProvider;

  @Mock
  private EmailLinkService emailLinkService;

  private ProjectTransferEmailPropertyService projectTransferEmailPropertyService;

  @Before
  public void setup() {
    projectTransferEmailPropertyService = new ProjectTransferEmailPropertyService(
        List.of(testProjectTransferEmailPropertyProvider),
        emailLinkService
    );
  }

  @Test
  public void getIncomingOperatorProjectTransferEmailProperties_whenProjectTypeImplementationFound_thenAssertCustomProperties() {

    final var supportedProjectType = ProjectType.INFRASTRUCTURE;

    final var projectDetail = ProjectUtil.getProjectDetails(supportedProjectType);
    final var transferReason = "transfer reason";
    final var previousOperatorName = "previous operator name";
    final var projectUrl = "project url";

    final var customIntroText = "custom intro text";
    final var customSubjectText = "custom subject text";

    when(testProjectTransferEmailPropertyProvider.getSupportedProjectType()).thenReturn(supportedProjectType);

    when(testProjectTransferEmailPropertyProvider.getIncomingOperatorTransferEmailProperties(
        projectDetail,
        transferReason,
        previousOperatorName
    )).thenReturn(
        new TestIncomingOperatorTransferEmailProperties(
            transferReason,
            previousOperatorName,
            projectUrl,
            customIntroText,
            customSubjectText
        )
    );

    final var expectedEmailPersonalisation = new HashMap<String, Object>();
    expectedEmailPersonalisation.put("PREVIOUS_OPERATOR_NAME", previousOperatorName);
    expectedEmailPersonalisation.put("PROJECT_URL", projectUrl);
    expectedEmailPersonalisation.put("TRANSFER_REASON", transferReason);

    expectedEmailPersonalisation.put(
        IncomingOperatorProjectTransferEmailProperties.INCOMING_OPERATOR_SUBJECT_TEXT_MAIL_MERGE_FIELD_NAME,
        customSubjectText
    );

    expectedEmailPersonalisation.put(
        IncomingOperatorProjectTransferEmailProperties.INCOMING_OPERATOR_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        customIntroText
    );

    final var resultingEmailProperties = projectTransferEmailPropertyService
        .getIncomingOperatorProjectTransferEmailProperties(
            projectDetail,
            transferReason,
            previousOperatorName
    );

    assertThat(resultingEmailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailPersonalisation);
  }

  @Test
  public void getIncomingOperatorProjectTransferEmailProperties_whenNoProjectTypeImplementation_thenAssertGenericProperties() {

    final var supportedProjectType = ProjectType.INFRASTRUCTURE;
    final var unsupportedProjectType = ProjectType.FORWARD_WORK_PLAN;

    final var projectDetail = ProjectUtil.getProjectDetails(unsupportedProjectType);

    final var transferReason = "transfer reason";
    final var previousOperatorName = "previous operator name";
    final var projectUrl = "project url";

    when(testProjectTransferEmailPropertyProvider.getSupportedProjectType()).thenReturn(supportedProjectType);
    when(emailLinkService.generateProjectManagementUrl(projectDetail.getProject())).thenReturn(projectUrl);

    final var expectedEmailPersonalisation = new HashMap<String, Object>();
    expectedEmailPersonalisation.put("PREVIOUS_OPERATOR_NAME", previousOperatorName);
    expectedEmailPersonalisation.put("PROJECT_URL", projectUrl);
    expectedEmailPersonalisation.put("TRANSFER_REASON", transferReason);

    expectedEmailPersonalisation.put(
        IncomingOperatorProjectTransferEmailProperties.INCOMING_OPERATOR_SUBJECT_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format("%s", IncomingOperatorProjectTransferEmailProperties.DEFAULT_INCOMING_OPERATOR_SUBJECT_TEXT)
    );

    expectedEmailPersonalisation.put(
        IncomingOperatorProjectTransferEmailProperties.INCOMING_OPERATOR_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format("%s.", IncomingOperatorProjectTransferEmailProperties.DEFAULT_INCOMING_OPERATOR_INTRO_TEXT)
    );

    final var resultingEmailProperties = projectTransferEmailPropertyService
        .getIncomingOperatorProjectTransferEmailProperties(
            projectDetail,
            transferReason,
            previousOperatorName
        );

    assertThat(resultingEmailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailPersonalisation);
  }

  @Test
  public void getOutgoingOperatorProjectTransferEmailProperties_whenProjectTypeImplementationFound_thenAssertCustomProperties() {

    final var supportedProjectType = ProjectType.INFRASTRUCTURE;

    final var projectDetail = ProjectUtil.getProjectDetails(supportedProjectType);
    final var transferReason = "transfer reason";
    final var currentOperatorName = "current operator name";

    final var customIntroText = "custom intro text";
    final var customSubjectText = "custom subject text";

    when(testProjectTransferEmailPropertyProvider.getSupportedProjectType()).thenReturn(supportedProjectType);

    when(testProjectTransferEmailPropertyProvider.getOutgoingOperatorTransferEmailProperties(
        projectDetail,
        transferReason,
        currentOperatorName
    )).thenReturn(
        new TestOutgoingOperatorTransferEmailProperties(
            transferReason,
            currentOperatorName,
            customIntroText,
            customSubjectText
        )
    );

    final var expectedEmailPersonalisation = new HashMap<String, Object>();
    expectedEmailPersonalisation.put("NEW_OPERATOR_NAME", currentOperatorName);
    expectedEmailPersonalisation.put("TRANSFER_REASON", transferReason);

    expectedEmailPersonalisation.put(
        OutgoingOperatorProjectTransferEmailProperties.OUTGOING_OPERATOR_SUBJECT_TEXT_MAIL_MERGE_FIELD_NAME,
        customSubjectText
    );

    expectedEmailPersonalisation.put(
        OutgoingOperatorProjectTransferEmailProperties.OUTGOING_OPERATOR_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        customIntroText
    );

    final var resultingEmailProperties = projectTransferEmailPropertyService
        .getOutgoingOperatorProjectTransferEmailProperties(
            projectDetail,
            transferReason,
            currentOperatorName
        );

    assertThat(resultingEmailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailPersonalisation);
  }

  @Test
  public void getOutgoingOperatorProjectTransferEmailProperties_whenProjectTypeImplementationFound_thenAssertGenericProperties() {

    final var supportedProjectType = ProjectType.INFRASTRUCTURE;
    final var unsupportedProjectType = ProjectType.FORWARD_WORK_PLAN;

    final var projectDetail = ProjectUtil.getProjectDetails(unsupportedProjectType);
    final var transferReason = "transfer reason";
    final var currentOperatorName = "current operator name";

    when(testProjectTransferEmailPropertyProvider.getSupportedProjectType()).thenReturn(supportedProjectType);

    final var expectedEmailPersonalisation = new HashMap<String, Object>();
    expectedEmailPersonalisation.put("NEW_OPERATOR_NAME", currentOperatorName);
    expectedEmailPersonalisation.put("TRANSFER_REASON", transferReason);

    expectedEmailPersonalisation.put(
        OutgoingOperatorProjectTransferEmailProperties.OUTGOING_OPERATOR_SUBJECT_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format("%s", OutgoingOperatorProjectTransferEmailProperties.DEFAULT_OUTGOING_OPERATOR_SUBJECT_TEXT)
    );

    expectedEmailPersonalisation.put(
        OutgoingOperatorProjectTransferEmailProperties.OUTGOING_OPERATOR_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format("%s.", OutgoingOperatorProjectTransferEmailProperties.DEFAULT_OUTGOING_OPERATOR_INTRO_TEXT)
    );

    final var resultingEmailProperties = projectTransferEmailPropertyService
        .getOutgoingOperatorProjectTransferEmailProperties(
            projectDetail,
            transferReason,
            currentOperatorName
        );

    assertThat(resultingEmailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailPersonalisation);
  }

  static class TestIncomingOperatorTransferEmailProperties extends IncomingOperatorProjectTransferEmailProperties {

    private final String customIntroText;

    private final String customSubjectText;

    public TestIncomingOperatorTransferEmailProperties(String transferReason,
                                                       String previousOperatorName,
                                                       String projectUrl,
                                                       String customIntroText,
                                                       String customSubjectText) {
      super(transferReason, previousOperatorName, projectUrl);
      this.customIntroText = customIntroText;
      this.customSubjectText = customSubjectText;
    }

    @Override
    public Map<String, Object> getEmailPersonalisation() {
      final var emailProperties = super.getEmailPersonalisation();
      emailProperties.put(IncomingOperatorProjectTransferEmailProperties.INCOMING_OPERATOR_INTRO_TEXT_MAIL_MERGE_FIELD_NAME, customIntroText);
      emailProperties.put(IncomingOperatorProjectTransferEmailProperties.INCOMING_OPERATOR_SUBJECT_TEXT_MAIL_MERGE_FIELD_NAME, customSubjectText);
      return emailProperties;
    }
  }

  static class TestOutgoingOperatorTransferEmailProperties extends OutgoingOperatorProjectTransferEmailProperties {

    private final String customIntroText;

    private final String customSubjectText;

    public TestOutgoingOperatorTransferEmailProperties(String transferReason,
                                                       String newOperatorName,
                                                       String customIntroText,
                                                       String customSubjectText) {
      super(transferReason, newOperatorName);
      this.customIntroText = customIntroText;
      this.customSubjectText = customSubjectText;
    }

    @Override
    public Map<String, Object> getEmailPersonalisation() {
      final var emailProperties = super.getEmailPersonalisation();
      emailProperties.put(OutgoingOperatorProjectTransferEmailProperties.OUTGOING_OPERATOR_INTRO_TEXT_MAIL_MERGE_FIELD_NAME, customIntroText);
      emailProperties.put(OutgoingOperatorProjectTransferEmailProperties.OUTGOING_OPERATOR_SUBJECT_TEXT_MAIL_MERGE_FIELD_NAME, customSubjectText);
      return emailProperties;
    }
  }
}
