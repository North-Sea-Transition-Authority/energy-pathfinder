package uk.co.ogauthority.pathfinder.controller.contact;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.contact.SupportContactService;

@RunWith(SpringRunner.class)
@WebMvcTest(ContactInformationController.class)
public class ContactInformationControllerTest extends AbstractControllerTest {

  @MockBean
  private SupportContactService supportContactService;

  @Test
  public void getContactInformation() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(ContactInformationController.class).getContactInformation())))
        .andExpect(status().isOk());
  }
}
