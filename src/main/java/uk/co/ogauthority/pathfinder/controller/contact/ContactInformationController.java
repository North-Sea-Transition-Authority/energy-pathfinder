package uk.co.ogauthority.pathfinder.controller.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.service.contact.SupportContactService;

@Controller
public class ContactInformationController {

  private final SupportContactService supportContactService;

  @Autowired
  public ContactInformationController(SupportContactService supportContactService) {
    this.supportContactService = supportContactService;
  }

  @GetMapping("/contact")
  public ModelAndView getContactInformation(boolean opensInNewTab) {
    return supportContactService.getContactInformationModelAndView(opensInNewTab);
  }
}
