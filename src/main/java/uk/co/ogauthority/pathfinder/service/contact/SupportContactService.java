package uk.co.ogauthority.pathfinder.service.contact;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.model.enums.contact.ServiceContactDetail;

@Service
public class SupportContactService {

  public static final String TEMPLATE_PATH = "contact/contactDetails";

  public List<ServiceContactDetail> getSupportContactList() {
    return Arrays.stream(ServiceContactDetail.values())
        .filter(ServiceContactDetail::isShownOnContactPage)
        .sorted(Comparator.comparing(ServiceContactDetail::getDisplayOrder))
        .collect(Collectors.toList());
  }

  public ModelAndView getContactInformationModelAndView() {
    return new ModelAndView(TEMPLATE_PATH)
        .addObject("contacts", getSupportContactList());
  }
}
