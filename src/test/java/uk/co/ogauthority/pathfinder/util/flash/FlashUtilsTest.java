package uk.co.ogauthority.pathfinder.util.flash;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@RunWith(MockitoJUnitRunner.class)
public class FlashUtilsTest {

  private RedirectAttributes redirectAttributes;

  @Before
  public void setUp() {
    redirectAttributes = new RedirectAttributesModelMap();
  }

  @Test
  public void success_onlyTitle() {
    FlashUtils.success(redirectAttributes, "title");
    assertThat(getFlashAttributes())
        .containsExactly(
            entry("flashClass", "fds-flash--green"),
            entry("flashTitle", "title"),
            entry("flashMessage", "")
        );
  }

  @Test
  public void success_titleAndMessage() {
    FlashUtils.success(redirectAttributes, "title", "message");
    assertThat(getFlashAttributes())
        .containsExactly(
            entry("flashClass", "fds-flash--green"),
            entry("flashTitle", "title"),
            entry("flashMessage", "message")
        );
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getFlashAttributes() {
    return (Map<String, Object>) redirectAttributes.getFlashAttributes();
  }
}