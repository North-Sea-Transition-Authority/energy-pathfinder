package uk.co.ogauthority.pathfinder.controller.feedback;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.form.feedback.FeedbackForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.feedback.FeedbackModelService;
import uk.co.ogauthority.pathfinder.service.feedback.FeedbackService;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@RunWith(SpringRunner.class)
@WebMvcTest(FeedbackController.class)
public class FeedbackControllerTest extends AbstractControllerTest {

  @MockBean
  FeedbackModelService feedbackModelService;

  @MockBean
  FeedbackService feedbackService;

  private static final AuthenticatedUserAccount AUTHENTICATED_USER = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.FEEDBACK_PRIVILEGES
  );

  private static final AuthenticatedUserAccount UNAUTHENTICATED_USER = UserTestingUtil.getAuthenticatedUserAccount();

  private static final String DUMMY_MODEL_NAME = "model-name";

  @Test
  public void getFeedback_whenAuthenticatedAndProjectDetailId_thenOk() throws Exception {
    mockMvc.perform(
        get(ReverseRouter.route(on(FeedbackController.class).getFeedback(10, null)))
        .with(authenticatedUserAndSession(AUTHENTICATED_USER))
    )
        .andExpect(status().isOk());
  }

  @Test
  public void getFeedback_whenAuthenticatedAndNoProjectDetailId_thenOk() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(FeedbackController.class).getFeedback(null, null)))
                .with(authenticatedUserAndSession(AUTHENTICATED_USER))
        )
        .andExpect(status().isOk());
  }

  @Test
  public void getFeedback_whenUnauthenticatedAndProjectDetailId_thenForbidden() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(FeedbackController.class).getFeedback(10, null)))
                .with(authenticatedUserAndSession(UNAUTHENTICATED_USER))
        )
        .andExpect(status().isForbidden());
  }

  @Test
  public void getFeedback_whenUnauthenticatedAndNoProjectDetailId_thenForbidden() throws Exception {
    mockMvc.perform(
        get(ReverseRouter.route(on(FeedbackController.class).getFeedback(null, null)))
        .with(authenticatedUserAndSession(UNAUTHENTICATED_USER))
    )
        .andExpect(status().isForbidden());
  }

  @Test
  public void getFeedback_whenUnauthenticated_thenForbidden() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(FeedbackController.class).getFeedback(null, null)))
                .with(authenticatedUserAndSession(UNAUTHENTICATED_USER))
        )
        .andExpect(status().isForbidden());
  }

  @Test
  public void submitFeedback_whenNoFormErrors_thenRedirect() throws Exception {

    var form = new FeedbackForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(feedbackService.validateFeedbackForm(any(), any())).thenReturn(bindingResult);

    when(feedbackModelService.getFeedbackModelAndView(any())).thenReturn(new ModelAndView(DUMMY_MODEL_NAME));

    mockMvc.perform(
        post(ReverseRouter.route(on(FeedbackController.class).getFeedback(null, null)))
            .with(authenticatedUserAndSession(AUTHENTICATED_USER))
            .with(csrf())
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name(String.format("redirect:%s", ControllerUtils.getWorkAreaUrl())));

    verify(feedbackService, times(1)).saveFeedback(form, AUTHENTICATED_USER.getLinkedPerson());
  }

  @Test
  public void submitFeedback_whenFormErrors_thenRedirectToFeedbackPage() throws Exception {

    var form = new FeedbackForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(feedbackService.validateFeedbackForm(any(), any())).thenReturn(bindingResult);

    when(feedbackModelService.getFeedbackModelAndView(any())).thenReturn(new ModelAndView(DUMMY_MODEL_NAME));

    mockMvc.perform(
        post(ReverseRouter.route(on(FeedbackController.class).getFeedback(null, null)))
            .with(authenticatedUserAndSession(AUTHENTICATED_USER))
            .with(csrf())
        )
        .andExpect(status().isOk())
        .andExpect(view().name(DUMMY_MODEL_NAME));

    verify(feedbackService, never()).saveFeedback(any(), any());
  }

  @Test
  public void submitFeedback_whenUnauthenticated_thenForbidden() throws Exception {

    var form = new FeedbackForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(feedbackService.validateFeedbackForm(any(), any())).thenReturn(bindingResult);

    when(feedbackModelService.getFeedbackModelAndView(any())).thenReturn(new ModelAndView(DUMMY_MODEL_NAME));

    mockMvc.perform(
            post(ReverseRouter.route(on(FeedbackController.class).getFeedback(null, null)))
                .with(authenticatedUserAndSession(UNAUTHENTICATED_USER))
                .with(csrf())
        )
        .andExpect(status().isForbidden());

    verify(feedbackService, never()).saveFeedback(any(), any());
  }

}