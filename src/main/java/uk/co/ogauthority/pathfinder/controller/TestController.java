package uk.co.ogauthority.pathfinder.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.model.entity.UserAccount;

@Controller
public class TestController {

  @GetMapping("/session-info")
  public ModelAndView test(UserAccount userAccount) {
    return new ModelAndView("test/sessionInfo", "user", userAccount);
  }

}
