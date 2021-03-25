package uk.co.ogauthority.pathfinder.service.accessibility;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.model.enums.contact.ServiceContactDetail;

@Service
public class AccessibilityStatementService {

  protected static final String TEMPLATE_PATH = "accessibility/accessibilityStatement";
  protected static final String PAGE_HEADING = "Accessibility statement";

  public ModelAndView getAccessibilityStatementModelAndView() {
    return new ModelAndView(TEMPLATE_PATH)
        .addObject("pageHeading", PAGE_HEADING)
        .addObject("technicalSupport", ServiceContactDetail.TECHNICAL_SUPPORT);
  }
}
