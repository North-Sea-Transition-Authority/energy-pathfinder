package uk.co.ogauthority.pathfinder.service.communication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.entity.communication.OrganisationGroupCommunication;
import uk.co.ogauthority.pathfinder.model.enums.communication.CommunicationStatus;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.model.view.communication.CommunicationView;
import uk.co.ogauthority.pathfinder.testutil.CommunicationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class CommunicationViewServiceTest {

  @Mock
  private OrganisationGroupCommunicationService organisationGroupCommunicationService;

  @Mock
  private ServiceProperties serviceProperties;

  @Mock
  private WebUserAccountService webUserAccountService;

  private static final String SERVICE_NAME = "SERVICE_NAME";

  private CommunicationViewService communicationViewService;

  @Before
  public void setup() {
    communicationViewService = new CommunicationViewService(
        organisationGroupCommunicationService,
        serviceProperties,
        webUserAccountService
    );

    when(serviceProperties.getServiceName()).thenReturn(SERVICE_NAME);
  }

  @Test
  public void getCommunicationView_whenSubscriberType_checkConversion() {

    var communication = CommunicationTestUtil.getCompleteCommunication();
    communication.setRecipientType(RecipientType.SUBSCRIBERS);

    final var communicationView = communicationViewService.getCommunicationView(communication);
    assertCommonProperties(communication, communicationView);
    assertThat(communicationView.getRecipientCsv()).isEqualTo(String.format("%s subscribers", SERVICE_NAME));
  }

  @Test
  public void getCommunicationView_whenOperatorType_checkConversion() {

    var communication = CommunicationTestUtil.getCompleteCommunication();
    communication.setRecipientType(RecipientType.OPERATORS);

    var expectedRecipientList = getExpectedOperatorRecipientList(communication);

    final var communicationView = communicationViewService.getCommunicationView(communication);
    assertCommonProperties(communication, communicationView);
    assertThat(communicationView.getRecipientCsv()).isEqualTo(StringUtils.join(expectedRecipientList, ", "));
  }

  @Test
  public void getSentCommunicationView_whenSubscriberType_checkConversion() {

    var communication = CommunicationTestUtil.getCompleteCommunication();
    communication.setStatus(CommunicationStatus.COMPLETE);
    communication.setRecipientType(RecipientType.SUBSCRIBERS);
    communication.setSubmittedDatetime(Instant.now());
    communication.setSubmittedByWuaId(1);

    final var userAccount = UserTestingUtil.getWebUserAccount();
    when(webUserAccountService.getWebUserAccountOrError(communication.getSubmittedByWuaId())).thenReturn(userAccount);

    final var communicationView = communicationViewService.getSentCommunicationView(communication);
    assertCommonProperties(communication, communicationView);
    assertThat(communicationView.getRecipientCsv()).isEqualTo(String.format("%s subscribers", SERVICE_NAME));
    assertThat(communicationView.getFormattedDateSent()).isEqualTo(DateUtil.formatInstant(communication.getSubmittedDatetime()));
    assertThat(communicationView.getSubmittedByUserName()).isEqualTo(userAccount.getFullName());
    assertThat(communicationView.getSubmittedByEmailAddress()).isEqualTo(userAccount.getEmailAddress());
  }

  @Test
  public void getSentCommunicationView_whenOperatorType_checkConversion() {

    var communication = CommunicationTestUtil.getCompleteCommunication();
    communication.setRecipientType(RecipientType.OPERATORS);
    communication.setStatus(CommunicationStatus.COMPLETE);
    communication.setSubmittedDatetime(Instant.now());
    communication.setSubmittedByWuaId(1);

    final var userAccount = UserTestingUtil.getWebUserAccount();
    when(webUserAccountService.getWebUserAccountOrError(communication.getSubmittedByWuaId())).thenReturn(userAccount);

    var expectedRecipientList = getExpectedOperatorRecipientList(communication);

    final var communicationView = communicationViewService.getSentCommunicationView(communication);
    assertCommonProperties(communication, communicationView);
    assertThat(communicationView.getRecipientCsv()).isEqualTo(StringUtils.join(expectedRecipientList, ", "));
    assertThat(communicationView.getFormattedDateSent()).isEqualTo(DateUtil.formatInstant(communication.getSubmittedDatetime()));
    assertThat(communicationView.getSubmittedByUserName()).isEqualTo(userAccount.getFullName());
    assertThat(communicationView.getSubmittedByEmailAddress()).isEqualTo(userAccount.getEmailAddress());
  }

  @Test(expected = RuntimeException.class)
  public void getSentCommunicationView_whenDraftCommunication_thenException() {
    var communication = CommunicationTestUtil.getCompleteCommunication();
    communication.setStatus(CommunicationStatus.DRAFT);
    
    communicationViewService.getSentCommunicationView(communication);
  }

  private List<String> getExpectedOperatorRecipientList(Communication communication) {

    var organisationGroupCommunication1 = new OrganisationGroupCommunication();
    organisationGroupCommunication1.setOrganisationGroup(
        TeamTestingUtil.generateOrganisationGroup(2, "B company", "B company"));

    var organisationGroupCommunication2 = new OrganisationGroupCommunication();
    organisationGroupCommunication2.setOrganisationGroup(TeamTestingUtil.generateOrganisationGroup(1, "A company", "A company"));

    final var organisationGroupCommunications = List.of(organisationGroupCommunication1,organisationGroupCommunication2);

    when(organisationGroupCommunicationService.getOrganisationGroupCommunications(communication))
        .thenReturn(organisationGroupCommunications);

    return List.of(
        organisationGroupCommunication2.getOrganisationGroup().getSelectionText(),
        organisationGroupCommunication1.getOrganisationGroup().getSelectionText()
    );
  }

  private void assertCommonProperties(Communication communication,
                                      CommunicationView communicationView) {
    assertThat(communicationView.getSenderName()).isEqualTo(SERVICE_NAME);
    assertThat(communicationView.getSubject()).isEqualTo(communication.getEmailSubject());
    assertThat(communicationView.getBody()).isEqualTo(communication.getEmailBody());
    assertThat(communicationView.getGreetingText()).isEqualTo(EmailProperties.DEFAULT_GREETING_TEXT);
    assertThat(communicationView.getSignOffText()).isEqualTo(EmailProperties.DEFAULT_SIGN_OFF_TEXT);
    assertThat(communicationView.getSignOffIdentifier()).isEqualTo(EmailProperties.DEFAULT_SIGN_OFF_IDENTIFIER);

  }

}