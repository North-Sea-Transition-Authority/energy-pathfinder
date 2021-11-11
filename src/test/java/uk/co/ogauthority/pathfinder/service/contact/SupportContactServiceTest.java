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
  public void getContactInformationModelAndView_whenOpensInNewTab_thenOpensInNewTabIsTrue() {
    assertExpectedModelProperties(true);
  }

  @Test
  public void getContactInformationModelAndView_whenDoesntOpensInNewTab_thenOpensInNewTabIsFalse() {
    assertExpectedModelProperties(false);
  }

  private void assertExpectedModelProperties(boolean showBackLink) {

    var modelAndView = supportContactService.getContactInformationModelAndView(showBackLink);

    assertThat(modelAndView.getViewName()).isEqualTo(SupportContactService.TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("contacts", supportContactService.getSupportContactList()),
        entry("opensInNewTab", showBackLink)
    );
  }
}
