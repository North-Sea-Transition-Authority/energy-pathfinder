package uk.co.ogauthority.pathfinder.controller.communication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import org.junit.Before;
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
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.model.form.communication.CommunicationForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationContext;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationModelService;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationService;
import uk.co.ogauthority.pathfinder.service.communication.OrganisationGroupCommunicationService;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(CommunicationJourneyController.class)
public class CommunicationJourneyControllerTest extends AbstractControllerTest {

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.COMMUNICATION_PRIVILEGES
  );

  private final AuthenticatedUserAccount unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.WORK_AREA_PRIVILEGES
  );

  private final Integer communicationId = 1;

  private Communication communication;

  @MockBean
  protected CommunicationService communicationService;

  @MockBean
  protected CommunicationModelService communicationModelService;

  @MockBean
  protected OrganisationGroupCommunicationService organisationGroupCommunicationService;

  @Before
  public void setup() {
    communication = new Communication();
    communication.setId(1);

    //TODO Remove this ones controller tests are updated as part of PAT-441
    when(communicationJourneyService.checkJourneyStage(any(), any()))
        .thenReturn(new CommunicationContext(communication));
  }

  @Test
  public void getCommunicationContent_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(CommunicationJourneyController.class).getCommunicationContent(
        communicationId,
        null,
        null
    )))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getCommunicationContent_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(CommunicationJourneyController.class).getCommunicationContent(
        communicationId,
        null,
        null
    )))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveCommunicationContent_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(post(ReverseRouter.route(on(CommunicationJourneyController.class).saveCommunicationContent(
        null,
        null,
        null,
        null,
        null
    )))
        .with(authenticatedUserAndSession(unauthenticatedUser))
        .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveCommunicationContent_whenAuthenticatedAndValidForm_thenRedirectOnCompletion() throws Exception {

    var bindingResult = new BeanPropertyBindingResult(CommunicationForm.class, "form");
    when(communicationService.validateCommunicationForm(any(), any(), any())).thenReturn(bindingResult);

    communication.setRecipientType(RecipientType.OPERATORS);
    when(communicationService.getCommunicationOrError(communicationId)).thenReturn(communication);
    when(communicationService.updateCommunication(any(), any(), any(), any())).thenReturn(communication);

    mockMvc.perform(post(ReverseRouter.route(on(CommunicationJourneyController.class).saveCommunicationContent(
        communicationId,
        null,
        null,
        null,
        null
    )))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf()))
        .andExpect(status().is3xxRedirection());

    verify(communicationService, times(1)).updateCommunication(any(), any(), any(), any());
  }

  @Test
  public void saveCommunicationContent_whenAuthenticatedAndInvalidForm_thenNoSave() throws Exception {

    var bindingResult = new BeanPropertyBindingResult(CommunicationForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(communicationService.validateCommunicationForm(any(), any(), any())).thenReturn(bindingResult);

    communication.setRecipientType(RecipientType.SUBSCRIBERS);
    when(communicationService.getCommunicationOrError(communicationId)).thenReturn(communication);
    when(communicationService.updateCommunication(any(), any(), any(), any())).thenReturn(communication);

    when(communicationModelService.getCommunicationContentModelAndView(any())).thenReturn(new ModelAndView());

    mockMvc.perform(post(ReverseRouter.route(on(CommunicationJourneyController.class).saveCommunicationContent(
        communicationId,
        null,
        null,
        null,
        null
    )))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf()))
        .andExpect(status().isOk());

    verify(communicationService, times(0)).updateCommunication(any(), any(), any(), any());
  }

  @Test
  public void saveCommunicationContent_whenOperatorRecipientType_thenRedirectToOrganisationSelect() throws Exception {

    communication.setRecipientType(RecipientType.OPERATORS);
    when(communicationService.getCommunicationOrError(communicationId)).thenReturn(communication);

    var bindingResult = new BeanPropertyBindingResult(CommunicationForm.class, "form");
    when(communicationService.validateCommunicationForm(any(), any(), any())).thenReturn(bindingResult);

    when(communicationService.updateCommunication(any(), any(), any(), any())).thenReturn(communication);

    mockMvc.perform(post(ReverseRouter.route(on(CommunicationJourneyController.class).saveCommunicationContent(
        communicationId,
        null,
        null,
        null,
        null
    )))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(result -> assertThat(result.getModelAndView().getViewName()).isEqualTo(
            String.format("redirect:/communications/communication/%s/operator-select", communication.getId())
        ));
  }

  @Test
  public void saveCommunicationContent_whenSubscriberRecipientType_thenRedirectToConfirmation() throws Exception {

    communication.setRecipientType(RecipientType.SUBSCRIBERS);
    when(communicationService.getCommunicationOrError(communicationId)).thenReturn(communication);

    var bindingResult = new BeanPropertyBindingResult(CommunicationForm.class, "form");
    when(communicationService.validateCommunicationForm(any(), any(), any())).thenReturn(bindingResult);

    when(communicationService.updateCommunication(any(), any(), any(), any())).thenReturn(communication);

    mockMvc.perform(post(ReverseRouter.route(on(CommunicationJourneyController.class).saveCommunicationContent(
        communicationId,
        null,
        null,
        null,
        null
    )))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(result -> assertThat(result.getModelAndView().getViewName()).isEqualTo(
            String.format("redirect:/communications/communication/%s/confirmation", communication.getId())
        ));
  }

  @Test
  public void getOperatorSelectForCommunication_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(CommunicationJourneyController.class).getOperatorSelectForCommunication(
        communicationId,
        null,
        null
    )))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getOperatorSelectForCommunication_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(CommunicationJourneyController.class).getOperatorSelectForCommunication(
        communicationId,
        null,
        null
    )))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveOperatorSelectForCommunication_whenUnauthenticated_thenNoAccess() throws Exception {

    var bindingResult = new BeanPropertyBindingResult(CommunicationForm.class, "form");
    when(organisationGroupCommunicationService.validateOrganisationSelectionForm(any(), any(), any())).thenReturn(bindingResult);

    when(communicationService.getCommunicationOrError(communicationId)).thenReturn(communication);

    mockMvc.perform(post(ReverseRouter.route(on(CommunicationJourneyController.class).saveOperatorSelectForCommunication(
        communicationId,
        null,
        null,
        null,
        null
    )))
        .with(authenticatedUserAndSession(unauthenticatedUser))
        .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveOperatorSelectForCommunication_whenAuthenticatedAndValidForm_thenSaveAndRedirect() throws Exception {

    var bindingResult = new BeanPropertyBindingResult(CommunicationForm.class, "form");
    when(organisationGroupCommunicationService.validateOrganisationSelectionForm(any(), any(), any())).thenReturn(bindingResult);

    when(communicationService.getCommunicationOrError(communicationId)).thenReturn(communication);

    mockMvc.perform(post(ReverseRouter.route(on(CommunicationJourneyController.class).saveOperatorSelectForCommunication(
        communicationId,
        null,
        null,
        null,
        null
    )))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf()))
        .andExpect(status().is3xxRedirection());

    verify(organisationGroupCommunicationService, times(1)).saveOrganisationGroupCommunication(any(), any());
  }

  @Test
  public void saveOperatorSelectForCommunication_whenAuthenticatedAndInvalidForm_thenSaveAndRedirect() throws Exception {

    var bindingResult = new BeanPropertyBindingResult(CommunicationForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(organisationGroupCommunicationService.validateOrganisationSelectionForm(any(), any(), any())).thenReturn(bindingResult);

    when(communicationService.getCommunicationOrError(communicationId)).thenReturn(communication);

    when(communicationModelService.getOperatorSelectForCommunication(any(), any())).thenReturn(new ModelAndView());

    mockMvc.perform(post(ReverseRouter.route(on(CommunicationJourneyController.class).saveOperatorSelectForCommunication(
        communicationId,
        null,
        null,
        null,
        null
    )))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf()))
        .andExpect(status().isOk());

    verify(organisationGroupCommunicationService, times(0)).saveOrganisationGroupCommunication(any(), any());
  }

  @Test
  public void getCommunicationConfirmation_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(CommunicationJourneyController.class).getCommunicationConfirmation(
        communicationId,
        null,
        null
    )))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getCommunicationConfirmation_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(CommunicationJourneyController.class).getCommunicationConfirmation(
        communicationId,
        null,
        null
    )))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveCommunicationConfirmation_whenAuthenticated_thenAccess() throws Exception {

    when(communicationService.getCommunicationOrError(any())).thenReturn(communication);

    mockMvc.perform(post(ReverseRouter.route(on(CommunicationJourneyController.class).saveCommunicationConfirmation(
        communicationId,
        null,
        null
    )))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf()))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  public void saveCommunicationConfirmation_whenunAuthenticated_thenNoAccess() throws Exception {

    when(communicationService.getCommunicationOrError(any())).thenReturn(communication);

    mockMvc.perform(post(ReverseRouter.route(on(CommunicationJourneyController.class).saveCommunicationConfirmation(
        communicationId,
        null,
        null
    )))
        .with(authenticatedUserAndSession(unauthenticatedUser))
        .with(csrf()))
        .andExpect(status().isForbidden());
  }
}