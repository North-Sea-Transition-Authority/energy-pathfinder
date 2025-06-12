package uk.co.ogauthority.pathfinder.service.newsletters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.newsletter.NoProjectsUpdatedNewsletterEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.newsletter.ProjectsUpdatedNewsletterEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.newsletters.MonthlyNewsletter;
import uk.co.ogauthority.pathfinder.model.entity.subscription.Subscriber;
import uk.co.ogauthority.pathfinder.model.enums.NewsletterSendingResult;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.repository.newsletters.MonthlyNewsletterRepository;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.service.email.EmailService;
import uk.co.ogauthority.pathfinder.service.email.notify.DefaultEmailPersonalisationService;
import uk.co.ogauthority.pathfinder.service.subscription.SubscriberAccessor;
import uk.co.ogauthority.pathfinder.testutil.ReportableProjectTestUtil;
import uk.co.ogauthority.pathfinder.testutil.SubscriptionTestUtil;

@ExtendWith(MockitoExtension.class)
class NewsletterServiceTest {

  @Mock
  private SubscriberAccessor subscriberAccessor;

  @Mock
  private LinkService linkService;

  @Mock
  private EmailService emailService;

  @Mock
  private MonthlyNewsletterRepository monthlyNewsletterRepository;

  @Mock
  private NewsletterProjectService newsletterProjectService;

  @Mock
  private DefaultEmailPersonalisationService defaultEmailPersonalisationService;

  @Captor
  private ArgumentCaptor<MonthlyNewsletter> monthlyNewsletterArgumentCaptor;

  @InjectMocks
  private NewsletterService newsletterService;

  private static final Subscriber SUBSCRIBER = SubscriptionTestUtil.createSubscriber("someone@example.com");

  @Test
  void sendNewsletterToSubscribers_whenSuccessful_thenSuccessResult() {
    when(linkService.getManageSubscriptionUrl(any())).thenCallRealMethod();
    when(subscriberAccessor.getAllSubscribers()).thenReturn(Collections.singletonList(SUBSCRIBER));
    newsletterService.sendNewsletterToSubscribers();

    verify(linkService, times(1)).getManageSubscriptionUrl(SUBSCRIBER.getUuid().toString());
    verify(emailService, times(1)).sendEmail(any(), eq(SUBSCRIBER.getEmailAddress()));
    verify(monthlyNewsletterRepository, times(1)).save(monthlyNewsletterArgumentCaptor.capture());
    verify(newsletterProjectService, times(1)).getProjectsUpdatedInTheLastMonth();

    assertMonthlyNewsletterEntityHasExpectedProperties(
        monthlyNewsletterArgumentCaptor.getValue(),
        NewsletterSendingResult.SUCCESS
    );
  }

  @Test
  void sendNewsletterToSubscribers_whenFailure_thenFailureResult() {
    when(subscriberAccessor.getAllSubscribers()).thenThrow(new RuntimeException());
    newsletterService.sendNewsletterToSubscribers();

    verify(linkService, times(0)).getManageSubscriptionUrl(SUBSCRIBER.getUuid().toString());
    verify(emailService, times(0)).sendEmail(any(), any());
    verify(monthlyNewsletterRepository, times(1)).save(monthlyNewsletterArgumentCaptor.capture());
    verify(newsletterProjectService, times(1)).getProjectsUpdatedInTheLastMonth();

    assertMonthlyNewsletterEntityHasExpectedProperties(
        monthlyNewsletterArgumentCaptor.getValue(),
        NewsletterSendingResult.FAILURE
    );
  }

  @Test
  void sendNewsletterToSubscribers_whenNoProjectUpdated_assertCorrectEmailPropertiesUsed() {
    when(linkService.getManageSubscriptionUrl(any())).thenCallRealMethod();
    when(subscriberAccessor.getAllSubscribers()).thenReturn(Collections.singletonList(SUBSCRIBER));
    when(newsletterProjectService.getProjectsUpdatedInTheLastMonth()).thenReturn(Collections.emptyList());

    final var serviceName = "service name";
    when(defaultEmailPersonalisationService.getServiceName()).thenReturn(serviceName);

    final var customerMnemonic = "customer mnemonic";
    when(defaultEmailPersonalisationService.getCustomerMnemonic()).thenReturn(customerMnemonic);

    newsletterService.sendNewsletterToSubscribers();

    final var expectedEmailProperties = new NoProjectsUpdatedNewsletterEmailProperties(
        SUBSCRIBER.getForename(),
        linkService.getManageSubscriptionUrl(SUBSCRIBER.getUuid().toString()),
        serviceName,
        customerMnemonic
    );

    verify(emailService, times(1)).sendEmail(expectedEmailProperties, SUBSCRIBER.getEmailAddress());
  }

  @Test
  void sendNewsletterToSubscribers_whenProjectsUpdatedNotInSubscription_thenNoUpdateEmailSent() {
    var subscribers = Collections.singletonList(SUBSCRIBER);
    var subscriberFieldStage = SubscriptionTestUtil.createSubscriberFieldStages(
        List.of(FieldStage.OIL_AND_GAS), SUBSCRIBER.getUuid()
    );

    when(linkService.getManageSubscriptionUrl(any())).thenCallRealMethod();
    when(subscriberAccessor.getAllSubscribers()).thenReturn(subscribers);
    var fieldStage = FieldStage.HYDROGEN;
    var project = new NewsletterProjectView(ReportableProjectTestUtil.createReportableProject(fieldStage));
    when(newsletterProjectService.getProjectsUpdatedInTheLastMonth()).thenReturn(List.of(project));
    when(subscriberAccessor.getAllSubscriberFieldStages(subscribers)).thenReturn(subscriberFieldStage);

    final var serviceName = "service name";
    when(defaultEmailPersonalisationService.getServiceName()).thenReturn(serviceName);

    final var customerMnemonic = "customer mnemonic";
    when(defaultEmailPersonalisationService.getCustomerMnemonic()).thenReturn(customerMnemonic);

    newsletterService.sendNewsletterToSubscribers();

    final var expectedEmailProperties = new NoProjectsUpdatedNewsletterEmailProperties(
        SUBSCRIBER.getForename(),
        linkService.getManageSubscriptionUrl(SUBSCRIBER.getUuid().toString()),
        serviceName,
        customerMnemonic
    );

    verify(emailService).sendEmail(expectedEmailProperties, SUBSCRIBER.getEmailAddress());
  }

  @Test
  void sendNewsletterToSubscribers_whenProjectUpdatedInSubscription_thenUpdateEmailSent() {
    when(linkService.getManageSubscriptionUrl(any())).thenCallRealMethod();
    var fieldStage = FieldStage.HYDROGEN;
    var reportableProject = ReportableProjectTestUtil.createReportableProject(fieldStage);
    var project = new NewsletterProjectView(reportableProject);
    var projectNotSubscribedTo = new NewsletterProjectView(
        ReportableProjectTestUtil.createReportableProject(FieldStage.OIL_AND_GAS)
    );
    final var projectsUpdate = List.of(project, projectNotSubscribedTo);
    var subscribers = Collections.singletonList(SUBSCRIBER);
    var subscriberFieldStage = SubscriptionTestUtil.createSubscriberFieldStages(
        List.of(fieldStage), SUBSCRIBER.getUuid()
    );

    when(subscriberAccessor.getAllSubscribers()).thenReturn(subscribers);
    when(subscriberAccessor.getAllSubscriberFieldStages(subscribers)).thenReturn(subscriberFieldStage);
    when(newsletterProjectService.getProjectsUpdatedInTheLastMonth()).thenReturn(projectsUpdate);

    final var serviceName = "service name";
    when(defaultEmailPersonalisationService.getServiceName()).thenReturn(serviceName);

    final var customerMnemonic = "customer mnemonic";
    when(defaultEmailPersonalisationService.getCustomerMnemonic()).thenReturn(customerMnemonic);

    newsletterService.sendNewsletterToSubscribers();

    final var expectedEmailProperties = new ProjectsUpdatedNewsletterEmailProperties(
        SUBSCRIBER.getForename(),
        linkService.getManageSubscriptionUrl(SUBSCRIBER.getUuid().toString()),
        List.of(project.getProject()),
        serviceName,
        customerMnemonic
    );

    verify(emailService, times(1)).sendEmail(expectedEmailProperties, SUBSCRIBER.getEmailAddress());

    var expectedProject = String.format("%s - %s", reportableProject.getOperatorName(), reportableProject.getProjectDisplayName());
    assertThat(project.getProject()).isEqualTo(expectedProject);
  }

  private void assertMonthlyNewsletterEntityHasExpectedProperties(MonthlyNewsletter monthlyNewsletter,
                                                                  NewsletterSendingResult expectedSendingResult) {
    assertThat(monthlyNewsletter.getResult()).isEqualTo(expectedSendingResult);
    assertThat(monthlyNewsletter.getCreationDateTime()).isNotNull();
    assertThat(monthlyNewsletter.getResultDateTime()).isNotNull();
  }
}
