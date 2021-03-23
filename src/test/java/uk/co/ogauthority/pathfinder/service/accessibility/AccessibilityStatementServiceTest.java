package uk.co.ogauthority.pathfinder.service.accessibility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.enums.contact.ServiceContactDetail;

@RunWith(MockitoJUnitRunner.class)
public class AccessibilityStatementServiceTest {

  private AccessibilityStatementService accessibilityStatementService;

  @Before
  public void setup() {
    accessibilityStatementService = new AccessibilityStatementService();
  }

  @Test
  public void getAccessibilityStatementModelAndView_assertModelProperties() {
    final var modelAndView = accessibilityStatementService.getAccessibilityStatementModelAndView();
    assertThat(modelAndView.getModelMap()).containsExactly(
        entry("pageHeading", AccessibilityStatementService.PAGE_HEADING),
        entry("technicalSupport", ServiceContactDetail.TECHNICAL_SUPPORT)
    );
  }

}