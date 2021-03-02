package uk.co.ogauthority.pathfinder.service.communication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
  private CommunicationService communicationService;

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
        communicationService,
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
    assertThat(communicationView.getEmailView().getRecipientList()).isEqualTo(List.of(String.format("%s subscribers", SERVICE_NAME)));
    assertThat(communicationView.isOperatorRecipientType()).isFalse();
    assertThat(communicationView.isSubscriberRecipientType()).isTrue();
  }

  @Test
  public void getCommunicationView_whenOperatorType_checkConversion() {

    var communication = CommunicationTestUtil.getCompleteCommunication();
    communication.setRecipientType(RecipientType.OPERATORS);

    var expectedRecipientList = getExpectedOperatorRecipientList(communication);

    final var communicationView = communicationViewService.getCommunicationView(communication);
    assertCommonProperties(communication, communicationView);
    assertThat(communicationView.isOperatorRecipientType()).isTrue();
    assertThat(communicationView.isSubscriberRecipientType()).isFalse();
    assertThat(communicationView.getEmailView().getRecipientList()).isEqualTo(expectedRecipientList);
  }

  @Test
  public void getSentCommunicationView_whenSubscriberType_checkConversion() {

    var communication = CommunicationTestUtil.getCompleteCommunication();
    communication.setStatus(CommunicationStatus.SENT);
    communication.setRecipientType(RecipientType.SUBSCRIBERS);
    communication.setSubmittedDatetime(Instant.now());
    communication.setSubmittedByWuaId(1);

    final var userAccount = UserTestingUtil.getWebUserAccount();
    when(webUserAccountService.getWebUserAccountOrError(communication.getSubmittedByWuaId())).thenReturn(userAccount);

    final var communicationView = communicationViewService.getSentCommunicationView(communication);
    assertCommonProperties(communication, communicationView);
    assertThat(communicationView.getEmailView().getRecipientList()).isEqualTo(List.of(String.format("%s subscribers", SERVICE_NAME)));
    assertThat(communicationView.getFormattedDateSent()).isEqualTo(DateUtil.formatInstant(communication.getSubmittedDatetime()));
    assertThat(communicationView.getSubmittedByUserName()).isEqualTo(userAccount.getFullName());
    assertThat(communicationView.isOperatorRecipientType()).isFalse();
    assertThat(communicationView.isSubscriberRecipientType()).isTrue();
  }

  @Test
  public void getSentCommunicationView_whenOperatorType_checkConversion() {

    var communication = CommunicationTestUtil.getCompleteCommunication();
    communication.setRecipientType(RecipientType.OPERATORS);
    communication.setStatus(CommunicationStatus.SENDING);
    communication.setSubmittedDatetime(Instant.now());
    communication.setSubmittedByWuaId(1);

    final var userAccount = UserTestingUtil.getWebUserAccount();
    when(webUserAccountService.getWebUserAccountOrError(communication.getSubmittedByWuaId())).thenReturn(userAccount);

    var expectedRecipientList = getExpectedOperatorRecipientList(communication);

    final var communicationView = communicationViewService.getSentCommunicationView(communication);
    assertCommonProperties(communication, communicationView);
    assertThat(communicationView.getEmailView().getRecipientList()).isEqualTo(expectedRecipientList);
    assertThat(communicationView.getFormattedDateSent()).isEqualTo(DateUtil.formatInstant(communication.getSubmittedDatetime()));
    assertThat(communicationView.getSubmittedByUserName()).isEqualTo(userAccount.getFullName());
    assertThat(communicationView.isOperatorRecipientType()).isTrue();
    assertThat(communicationView.isSubscriberRecipientType()).isFalse();
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
    final var emailView = communicationView.getEmailView();
    assertThat(emailView.getSenderName()).isEqualTo(SERVICE_NAME);
    assertThat(emailView.getSubject()).isEqualTo(communication.getEmailSubject());
    assertThat(emailView.getBody()).isEqualTo(communication.getEmailBody());
    assertThat(emailView.getGreetingText()).isEqualTo(EmailProperties.DEFAULT_GREETING_TEXT);
    assertThat(emailView.getSignOffText()).isEqualTo(EmailProperties.DEFAULT_SIGN_OFF_TEXT);
    assertThat(emailView.getSignOffIdentifier()).isEqualTo(EmailProperties.DEFAULT_SIGN_OFF_IDENTIFIER);
    assertThat(communicationView.getCommunicationId()).isEqualTo(communication.getId());
    assertThat(communicationView.getRecipientType()).isEqualTo(communication.getRecipientType().getDisplayName().toUpperCase());
  }

  @Test
  public void getSentCommunicationViews_whenNoSentCommunications_thenEmptyList() {
    when(communicationService.getCommunicationsWithStatuses(
        List.of(CommunicationStatus.SENDING, CommunicationStatus.SENT))
    ).thenReturn(List.of());

    final var result = communicationViewService.getSentCommunicationViews();
    assertThat(result).isEmpty();

    verify(webUserAccountService, never()).getWebUserAccounts(any());
  }

  @Test
  public void getSentCommunicationViews_whenSentCommunications_thenPopulatedList() {

    final var communication = CommunicationTestUtil.getCompleteCommunication();

    when(communicationService.getCommunicationsWithStatuses(
        List.of(CommunicationStatus.SENDING, CommunicationStatus.SENT))
    ).thenReturn(List.of(communication));

    when(webUserAccountService.getWebUserAccounts(List.of(communication.getSubmittedByWuaId())))
        .thenReturn(List.of(UserTestingUtil.getWebUserAccount()));

    final var sentCommunicationViews = communicationViewService.getSentCommunicationViews();
    assertThat(sentCommunicationViews).hasSize(1);

    assertCommonProperties(communication, sentCommunicationViews.get(0));
  }

  @Test
  public void getSentCommunicationViews_confirmSortOrdering() {

    final var earliestCommunication = CommunicationTestUtil.getCompleteCommunication();
    earliestCommunication.setId(10);
    earliestCommunication.setSubmittedDatetime(Instant.now().minus(1, ChronoUnit.DAYS));

    final var latestCommunication = CommunicationTestUtil.getCompleteCommunication();
    latestCommunication.setId(20);
    latestCommunication.setSubmittedDatetime(Instant.now());

    when(communicationService.getCommunicationsWithStatuses(
        List.of(CommunicationStatus.SENDING, CommunicationStatus.SENT))
    ).thenReturn(List.of(earliestCommunication, latestCommunication));

    when(webUserAccountService.getWebUserAccounts(any())).thenReturn(List.of(UserTestingUtil.getWebUserAccount()));

    final var sentCommunicationViews = communicationViewService.getSentCommunicationViews();
    assertThat(sentCommunicationViews).hasSize(2);
    assertThat(sentCommunicationViews.get(0).getCommunicationId()).isEqualTo(latestCommunication.getId());
    assertThat(sentCommunicationViews.get(1).getCommunicationId()).isEqualTo(earliestCommunication.getId());
  }
}