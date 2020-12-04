package uk.co.ogauthority.pathfinder.service.projectupdate;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.controller.projectupdate.OperatorUpdateController;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.ProvideNoUpdateForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@RunWith(MockitoJUnitRunner.class)
public class OperatorProjectUpdateServiceTest {

  private static final int PROJECT_ID = 1;

  @Mock
  private ValidationService validationService;

  @Mock
  private BreadcrumbService breadcrumbService;

  private OperatorProjectUpdateService operatorProjectUpdateService;

  @Before
  public void setup() {
    operatorProjectUpdateService = new OperatorProjectUpdateService(validationService, breadcrumbService);
  }

  @Test
  public void validate() {
    var form = new ProvideNoUpdateForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    operatorProjectUpdateService.validate(form, bindingResult);

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
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

  @Test
  public void getProjectProvideNoUpdateModelAndView() {
    var form = new ProvideNoUpdateForm();

    var modelAndView = operatorProjectUpdateService.getProjectProvideNoUpdateModelAndView(PROJECT_ID, form);

    assertThat(modelAndView.getViewName()).isEqualTo(OperatorProjectUpdateService.PROVIDE_NO_UPDATE_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("form", form),
        entry("confirmActionUrl", ReverseRouter.route(on(OperatorUpdateController.class)
            .provideNoUpdate(PROJECT_ID, null, null, null, null))),
        entry("cancelUrl", ReverseRouter.route(on(ManageProjectController.class).getProject(PROJECT_ID, null, null, null)))
    );

    verify(breadcrumbService, times(1)).fromManageProject(PROJECT_ID, modelAndView, OperatorUpdateController.NO_UPDATE_REQUIRED_PAGE_NAME);
  }
}
