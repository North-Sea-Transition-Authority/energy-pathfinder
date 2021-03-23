package uk.co.ogauthority.pathfinder.mvc.footer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@RunWith(MockitoJUnitRunner.class)
public class FooterServiceTest {

  private FooterService footerService;

  @Before
  public void setup() {
    footerService = new FooterService();
  }

  @Test
  public void addFooterUrlsToModelAndView_assertCommonProperties() {
    final var modelAndView = new ModelAndView();
    footerService.addFooterUrlsToModelAndView(modelAndView);
    assertThat(modelAndView.getModelMap()).containsExactly(
        entry(FooterService.CONTACT_URL_ATTR_NAME, ControllerUtils.getContactUrl()),
        entry(FooterService.ACCESSIBILITY_STATEMENT_URL_ATTR_NAME, ControllerUtils.getAccessibilityStatementUrl())
    );
  }
}