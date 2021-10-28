package uk.co.ogauthority.pathfinder.util.flash;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class FlashUtils {

  private FlashUtils() {
    throw new IllegalStateException("FlashUtils is a utility class and should not be instantiated");
  }

  public static void success(RedirectAttributes redirectAttributes, String title) {
    success(redirectAttributes, title, "");
  }

  public static void success(RedirectAttributes redirectAttributes, String title, String message) {
    redirectAttributes.addFlashAttribute("flashClass", "fds-flash--green");
    addTextAttributes(redirectAttributes, title, message);
  }

  private static void addTextAttributes(RedirectAttributes redirectAttributes, String title, String message) {
    redirectAttributes.addFlashAttribute("flashTitle", title);
    redirectAttributes.addFlashAttribute("flashMessage", message);
  }
}
