package uk.co.ogauthority.pathfinder.controller.feedback;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.feedback.FeedbackModelService;
import uk.co.ogauthority.pathfinder.feedback.FeedbackService;
import uk.co.ogauthority.pathfinder.model.form.feedback.FeedbackForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@RunWith(SpringRunner.class)
@WebMvcTest(FeedbackController.class)
public class FeedbackControllerTest extends AbstractControllerTest {

  @MockitoBean
  FeedbackModelService feedbackModelService;

  @MockitoBean
  FeedbackService feedbackService;

  private static final AuthenticatedUserAccount AUTHENTICATED_USER = UserTestingUtil.getAuthenticatedUserAccount();

  private static final String DUMMY_MODEL_NAME = "model-name";
  private static final String EXPECTED_LOGIN_URL = "login-url";

  @Test
  public void getFeedback_whenAuthenticatedAndProjectDetailId_thenOk() throws Exception {
    mockMvc.perform(
        get(ReverseRouter.route(on(FeedbackController.class).getFeedback(Optional.of(10), null)))
        .with(authenticatedUserAndSession(AUTHENTICATED_USER))
    )
        .andExpect(status().isOk());
  }

  @Test
  public void getFeedback_whenAuthenticatedAndNoProjectDetailId_thenOk() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(FeedbackController.class).getFeedback(Optional.empty(), null)))
                .with(authenticatedUserAndSession(AUTHENTICATED_USER))
        )
        .andExpect(status().isOk());
  }

  @Test
  public void getFeedback_whenUnauthenticated_thenRedirectionToLoginPage() throws Exception {
    when(foxUrlService.getFoxLoginUrl()).thenReturn(EXPECTED_LOGIN_URL);

    mockMvc.perform(
            get(ReverseRouter.route(on(FeedbackController.class).getFeedback(Optional.empty(), null)))
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(EXPECTED_LOGIN_URL));
  }

  @Test
  public void submitFeedback_whenNoFormErrors_thenRedirect() throws Exception {

    var form = new FeedbackForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(feedbackService.validateFeedbackForm(any(), any())).thenReturn(bindingResult);

    when(feedbackModelService.getFeedbackModelAndView(any())).thenReturn(new ModelAndView(DUMMY_MODEL_NAME));

    mockMvc.perform(
        post(ReverseRouter.route(on(FeedbackController.class).getFeedback(Optional.empty(), null)))
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
        post(ReverseRouter.route(on(FeedbackController.class).getFeedback(Optional.empty(), null)))
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

    when(foxUrlService.getFoxLoginUrl()).thenReturn(EXPECTED_LOGIN_URL);

    mockMvc.perform(
            post(ReverseRouter.route(on(FeedbackController.class).getFeedback(Optional.empty(), null)))
                .with(csrf())
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(EXPECTED_LOGIN_URL));

    verify(feedbackService, never()).saveFeedback(any(), any());
  }

}