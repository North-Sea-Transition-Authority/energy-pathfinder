package uk.co.ogauthority.pathfinder.controller.accessibility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.service.accessibility.AccessibilityStatementService;

@Controller
public class AccessibilityStatementController {

  private final AccessibilityStatementService accessibilityStatementService;

  @Autowired
  public AccessibilityStatementController(AccessibilityStatementService accessibilityStatementService) {
    this.accessibilityStatementService = accessibilityStatementService;
  }

  @GetMapping("/accessibility-statement")
  public ModelAndView getAccessibilityStatement() {
    return accessibilityStatementService.getAccessibilityStatementModelAndView();
  }
}
