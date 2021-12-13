package uk.co.ogauthority.pathfinder.service.email.projecttransfer.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.IncomingOperatorProjectTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.infrastructure.InfrastructureOutgoingOperatorTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureProjectTransferEmailPropertyServiceTest {

  @Mock
  private ServiceProperties serviceProperties;

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private LinkService linkService;

  private InfrastructureProjectTransferEmailPropertyService infrastructureProjectTransferEmailPropertyService;

  @Before
  public void setup() {
    infrastructureProjectTransferEmailPropertyService = new InfrastructureProjectTransferEmailPropertyService(
        serviceProperties,
        projectInformationService,
        linkService
    );
  }

  @Test
  public void getSupportedProjectType_assertInfrastructure() {
    final var supportedProjectType = infrastructureProjectTransferEmailPropertyService.getSupportedProjectType();
    assertThat(supportedProjectType).isEqualTo(ProjectType.INFRASTRUCTURE);
  }

  @Test
  public void getIncomingOperatorTransferEmailProperties_assertEmailProperties() {

    final var projectUrl = "project url";
    final var projectDetail = ProjectUtil.getProjectDetails();
    final var transferReason = "transfer reason";
    final var previousOperatorName = "previous operator name";
    final var projectTitle = "project title";
    final var customerMnemonic = "customer mnemonic";

    when(linkService.generateProjectManagementUrl(projectDetail.getProject())).thenReturn(projectUrl);
    when(projectInformationService.getProjectTitle(projectDetail)).thenReturn(projectTitle);
    when(serviceProperties.getCustomerMnemonic()).thenReturn(customerMnemonic);

    final var expectedEmailPersonalisation = new HashMap<String, Object>();
    expectedEmailPersonalisation.put("PREVIOUS_OPERATOR_NAME", previousOperatorName);
    expectedEmailPersonalisation.put("PROJECT_URL", projectUrl);
    expectedEmailPersonalisation.put("TRANSFER_REASON", transferReason);

    final var projectTypeLowerCaseDisplayName = ProjectType.INFRASTRUCTURE.getLowercaseDisplayName();

    expectedEmailPersonalisation.put(
        IncomingOperatorProjectTransferEmailProperties.INCOMING_OPERATOR_SUBJECT_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "You have been added as the operator for the %s: %s",
            projectTypeLowerCaseDisplayName,
            projectTitle
        )
    );

    expectedEmailPersonalisation.put(
        IncomingOperatorProjectTransferEmailProperties.INCOMING_OPERATOR_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "The %s have added you as the operator for the %s: %s.",
            customerMnemonic,
            projectTypeLowerCaseDisplayName,
            projectTitle
        )
    );

    final var resultingEmailProperties = infrastructureProjectTransferEmailPropertyService
        .getIncomingOperatorTransferEmailProperties(
            projectDetail,
            transferReason,
            previousOperatorName
        );

    assertThat(resultingEmailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailPersonalisation);
  }

  @Test
  public void getOutgoingOperatorTransferEmailProperties_assertEmailProperties() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var transferReason = "transfer reason";
    final var currentOperatorName = "current operator name";
    final var projectTitle = "project title";
    final var customerMnemonic = "customer mnemonic";

    when(projectInformationService.getProjectTitle(projectDetail)).thenReturn(projectTitle);
    when(serviceProperties.getCustomerMnemonic()).thenReturn(customerMnemonic);

    final var expectedEmailPersonalisation = new HashMap<String, Object>();
    expectedEmailPersonalisation.put("NEW_OPERATOR_NAME", currentOperatorName);
    expectedEmailPersonalisation.put("TRANSFER_REASON", transferReason);

    final var projectTypeLowerCaseDisplayName = ProjectType.INFRASTRUCTURE.getLowercaseDisplayName();

    expectedEmailPersonalisation.put(
        InfrastructureOutgoingOperatorTransferEmailProperties.OUTGOING_OPERATOR_SUBJECT_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "You have been removed as the operator for the %s: %s",
            projectTypeLowerCaseDisplayName,
            projectTitle
        )
    );

    expectedEmailPersonalisation.put(
        InfrastructureOutgoingOperatorTransferEmailProperties.OUTGOING_OPERATOR_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "The %s have removed you as the operator of the %s: %s.",
            customerMnemonic,
            projectTypeLowerCaseDisplayName,
            projectTitle
        )
    );

    final var resultingEmailProperties = infrastructureProjectTransferEmailPropertyService
        .getOutgoingOperatorTransferEmailProperties(
            projectDetail,
            transferReason,
            currentOperatorName
        );

    assertThat(resultingEmailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailPersonalisation);
  }

}