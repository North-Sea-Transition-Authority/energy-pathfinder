package uk.co.ogauthority.pathfinder.service.email.notify;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;
import uk.gov.service.notify.NotificationClient;

@RunWith(MockitoJUnitRunner.class)
public class NotifyTemplateServiceTest {

  @Mock
  private NotificationClient notificationClient;

  private NotifyTemplateService notifyTemplateService;

  @Before
  public void setup() {
    notifyTemplateService = new NotifyTemplateService(notificationClient);
  }

  @Test
  public void getNotifyTemplateByTemplateName_whenNoTemplateFound_thenEmptyOptional() {
    final var invalidTemplateName = "not-a-template";
    final var resultingTemplate = notifyTemplateService.getNotifyTemplateByTemplateName(invalidTemplateName);
    assertThat(resultingTemplate).isEmpty();
  }

  @Test
  public void getNotifyTemplateByTemplateName_whenTemplateFound_thenPopulatedOptional() {
    final var expectedTemplate = NotifyTemplate.EMAIL_DELIVERY_FAILED;
    final var resultingTemplate = notifyTemplateService.getNotifyTemplateByTemplateName(
        expectedTemplate.getTemplateName()
    );
    assertThat(resultingTemplate).contains(expectedTemplate);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getNotifyTemplateByTemplateNameOrError_whenNoTemplateFound_thenException() {
    final var invalidTemplateName = "not-a-template";
    notifyTemplateService.getNotifyTemplateByTemplateNameOrError(invalidTemplateName);
  }

  @Test
  public void getNotifyTemplateByTemplateNameOrError_whenTemplateFound_thenExpectedValueReturned() {
    final var expectedTemplate = NotifyTemplate.EMAIL_DELIVERY_FAILED;
    final var resultingTemplate = notifyTemplateService.getNotifyTemplateByTemplateNameOrError(
        expectedTemplate.getTemplateName()
    );
    assertThat(resultingTemplate).isEqualTo(expectedTemplate);
  }

}