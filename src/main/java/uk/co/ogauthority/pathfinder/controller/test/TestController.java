package uk.co.ogauthority.pathfinder.controller.test;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;

@Controller
@Profile("development")
public class TestController {

  @GetMapping("/session-info")
  public ModelAndView getSessionInfo(AuthenticatedUserAccount userAccount) {
    return new ModelAndView("test/sessionInfo", "user", userAccount);
  }

}
