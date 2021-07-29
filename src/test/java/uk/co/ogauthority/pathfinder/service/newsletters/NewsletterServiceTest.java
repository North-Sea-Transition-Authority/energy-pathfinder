package uk.co.ogauthority.pathfinder.service.newsletters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.newsletter.NoProjectsUpdatedNewsletterEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.newsletter.ProjectsUpdatedNewsletterEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.newsletters.MonthlyNewsletter;
import uk.co.ogauthority.pathfinder.model.entity.subscription.Subscriber;
import uk.co.ogauthority.pathfinder.model.enums.NewsletterSendingResult;
import uk.co.ogauthority.pathfinder.repository.newsletters.MonthlyNewsletterRepository;
import uk.co.ogauthority.pathfinder.service.email.EmailLinkService;
import uk.co.ogauthority.pathfinder.service.email.EmailService;
import uk.co.ogauthority.pathfinder.service.email.notify.DefaultEmailPersonalisationService;
import uk.co.ogauthority.pathfinder.service.subscription.SubscriberAccessor;
import uk.co.ogauthority.pathfinder.testutil.SubscriptionTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class NewsletterServiceTest {

  @Mock
  private SubscriberAccessor subscriberAccessor;

  @Mock
  private EmailLinkService emailLinkService;

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

  private NewsletterService newsletterService;

  private static final Subscriber SUBSCRIBER = SubscriptionTestUtil.createSubscriber("someone@example.com");

  @Before
  public void setUp() throws Exception {
    newsletterService = new NewsletterService(
        subscriberAccessor,
        emailLinkService,
        emailService,
        monthlyNewsletterRepository,
        newsletterProjectService,
        defaultEmailPersonalisationService
    );

    when(emailLinkService.getUnsubscribeUrl(any())).thenCallRealMethod();
  }

  @Test
  public void sendNewsletterToSubscribers_whenSuccessful_thenSuccessResult() {
    when(subscriberAccessor.getAllSubscribers()).thenReturn(Collections.singletonList(SUBSCRIBER));
    newsletterService.sendNewsletterToSubscribers();

    verify(emailLinkService, times(1)).getUnsubscribeUrl(SUBSCRIBER.getUuid().toString());
    verify(emailService, times(1)).sendEmail(any(), eq(SUBSCRIBER.getEmailAddress()));
    verify(monthlyNewsletterRepository, times(1)).save(monthlyNewsletterArgumentCaptor.capture());
    verify(newsletterProjectService, times(1)).getProjectsUpdatedInTheLastMonth();

    assertMonthlyNewsletterEntityHasExpectedProperties(
        monthlyNewsletterArgumentCaptor.getValue(),
        NewsletterSendingResult.SUCCESS
    );
  }

  @Test
  public void sendNewsletterToSubscribers_whenFailure_thenFailureResult() {
    when(subscriberAccessor.getAllSubscribers()).thenThrow(new RuntimeException());
    newsletterService.sendNewsletterToSubscribers();

    verify(emailLinkService, times(0)).getUnsubscribeUrl(SUBSCRIBER.getUuid().toString());
    verify(emailService, times(0)).sendEmail(any(), any());
    verify(monthlyNewsletterRepository, times(1)).save(monthlyNewsletterArgumentCaptor.capture());
    verify(newsletterProjectService, times(1)).getProjectsUpdatedInTheLastMonth();

    assertMonthlyNewsletterEntityHasExpectedProperties(
        monthlyNewsletterArgumentCaptor.getValue(),
        NewsletterSendingResult.FAILURE
    );
  }

  @Test
  public void sendNewsletterToSubscribers_whenNoProjectUpdated_assertCorrectEmailPropertiesUsed() {

    when(subscriberAccessor.getAllSubscribers()).thenReturn(Collections.singletonList(SUBSCRIBER));
    when(newsletterProjectService.getProjectsUpdatedInTheLastMonth()).thenReturn(Collections.emptyList());

    final var serviceName = "service name";
    when(defaultEmailPersonalisationService.getServiceName()).thenReturn(serviceName);

    final var customerMnemonic = "customer mnemonic";
    when(defaultEmailPersonalisationService.getCustomerMnemonic()).thenReturn(customerMnemonic);

    newsletterService.sendNewsletterToSubscribers();

    final var expectedEmailProperties = new NoProjectsUpdatedNewsletterEmailProperties(
        SUBSCRIBER.getForename(),
        emailLinkService.getUnsubscribeUrl(SUBSCRIBER.getUuid().toString()),
        serviceName,
        customerMnemonic
    );

    verify(emailService, times(1)).sendEmail(expectedEmailProperties, SUBSCRIBER.getEmailAddress());
  }

  @Test
  public void sendNewsletterToSubscribers_whenProjectUpdated_assertCorrectEmailPropertiesUsed() {

    final var projectsUpdate = List.of("example");

    when(subscriberAccessor.getAllSubscribers()).thenReturn(Collections.singletonList(SUBSCRIBER));
    when(newsletterProjectService.getProjectsUpdatedInTheLastMonth()).thenReturn(projectsUpdate);

    final var serviceName = "service name";
    when(defaultEmailPersonalisationService.getServiceName()).thenReturn(serviceName);

    final var customerMnemonic = "customer mnemonic";
    when(defaultEmailPersonalisationService.getCustomerMnemonic()).thenReturn(customerMnemonic);

    newsletterService.sendNewsletterToSubscribers();

    final var expectedEmailProperties = new ProjectsUpdatedNewsletterEmailProperties(
        SUBSCRIBER.getForename(),
        emailLinkService.getUnsubscribeUrl(SUBSCRIBER.getUuid().toString()),
        projectsUpdate,
        serviceName,
        customerMnemonic
    );

    verify(emailService, times(1)).sendEmail(expectedEmailProperties, SUBSCRIBER.getEmailAddress());
  }

  private void assertMonthlyNewsletterEntityHasExpectedProperties(MonthlyNewsletter monthlyNewsletter,
                                                                  NewsletterSendingResult expectedSendingResult) {
    assertThat(monthlyNewsletter.getResult()).isEqualTo(expectedSendingResult);
    assertThat(monthlyNewsletter.getCreationDateTime()).isNotNull();
    assertThat(monthlyNewsletter.getResultDateTime()).isNotNull();
  }
}
