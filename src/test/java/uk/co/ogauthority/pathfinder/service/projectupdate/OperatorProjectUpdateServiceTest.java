package uk.co.ogauthority.pathfinder.service.projectupdate;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.Before;
import org.junit.Test;
import uk.co.ogauthority.pathfinder.controller.projectupdate.OperatorUpdateController;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

public class OperatorProjectUpdateServiceTest {

  private static final int PROJECT_ID = 1;

  private OperatorProjectUpdateService operatorProjectUpdateService;

  @Before
  public void setup() {
    operatorProjectUpdateService = new OperatorProjectUpdateService();
  }

  @Test
  public void getProjectUpdateModelAndView() {
    var modelAndView = operatorProjectUpdateService.getProjectUpdateModelAndView(PROJECT_ID);

    assertThat(modelAndView.getViewName()).isEqualTo(OperatorProjectUpdateService.START_PAGE_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("startActionUrl", ReverseRouter.route(on(OperatorUpdateController.class)
            .startUpdate(PROJECT_ID, null, null)))
    );
  }
}
