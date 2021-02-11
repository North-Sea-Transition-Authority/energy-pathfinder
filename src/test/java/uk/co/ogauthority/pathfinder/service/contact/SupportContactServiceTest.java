package uk.co.ogauthority.pathfinder.service.contact;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.enums.contact.ServiceContactDetail;

@RunWith(MockitoJUnitRunner.class)
public class SupportContactServiceTest {

  private SupportContactService supportContactService;

  @Before
  public void setup() throws Exception {
    supportContactService = new SupportContactService();
  }

  @Test
  public void getSupportContactList() {
    List<ServiceContactDetail> contacts = supportContactService.getSupportContactList();

    assertThat(contacts).containsExactly(ServiceContactDetail.BUSINESS_SUPPORT, ServiceContactDetail.TECHNICAL_SUPPORT);
  }

  @Test
  public void getContactInformationModelAndView() {
    var modelAndView = supportContactService.getContactInformationModelAndView();

    assertThat(modelAndView.getViewName()).isEqualTo(SupportContactService.TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("contacts", supportContactService.getSupportContactList())
    );
  }
}
