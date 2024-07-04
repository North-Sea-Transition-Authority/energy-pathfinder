package uk.co.ogauthority.pathfinder.controller.communication;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.communication.CommunicationStatus;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.model.form.communication.CommunicationForm;
import uk.co.ogauthority.pathfinder.model.form.communication.OrganisationGroupSelectorForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationContext;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationJourneyStage;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationJourneyStatus;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationModelService;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationService;
import uk.co.ogauthority.pathfinder.service.communication.OrganisationGroupCommunicationService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;

@Controller
@RequestMapping("/communications")
public class CommunicationJourneyController {

  private final CommunicationModelService communicationModelService;
  private final CommunicationService communicationService;
  private final ControllerHelperService controllerHelperService;
  private final OrganisationGroupCommunicationService organisationGroupCommunicationService;

  @Autowired
  public CommunicationJourneyController(CommunicationModelService communicationModelService,
                                        CommunicationService communicationService,
                                        ControllerHelperService controllerHelperService,
                                        OrganisationGroupCommunicationService organisationGroupCommunicationService) {
    this.communicationModelService = communicationModelService;
    this.communicationService = communicationService;
    this.controllerHelperService = controllerHelperService;
    this.organisationGroupCommunicationService = organisationGroupCommunicationService;
  }

  @PostMapping("/communication")
  public ModelAndView startCommunicationJourney(AuthenticatedUserAccount user) {
    final var communication = communicationService.createCommunication(
        new CommunicationForm(),
        CommunicationStatus.DRAFT,
        user
    );
    return ReverseRouter.redirect(on(CommunicationJourneyController.class).getCommunicationContent(
        communication.getId(),
        null,
        null
    ));
  }

  @GetMapping("/communication/{communicationId}/email-content")
  @CommunicationJourney(journeyStage = CommunicationJourneyStage.EMAIL_CONTENT)
  public ModelAndView getCommunicationContent(@PathVariable("communicationId") Integer communicationId,
                                              CommunicationContext communicationContext,
                                              AuthenticatedUserAccount user) {
    final var communicationForm = communicationService.getCommunicationForm(
        communicationContext.getCommunication()
    );
    return communicationModelService.getCommunicationContentModelAndView(communicationForm);
  }

  @PostMapping("/communication/{communicationId}/email-content")
  @CommunicationJourney(journeyStage = CommunicationJourneyStage.EMAIL_CONTENT)
  public ModelAndView saveCommunicationContent(@PathVariable("communicationId") Integer communicationId,
                                               @Valid @ModelAttribute("form") CommunicationForm communicationForm,
                                               BindingResult bindingResult,
                                               CommunicationContext communicationContext,
                                               AuthenticatedUserAccount user) {
    bindingResult = communicationService.validateCommunicationForm(
        communicationForm,
        bindingResult,
        ValidationType.FULL
    );

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        communicationModelService.getCommunicationContentModelAndView(communicationForm),
        communicationForm,
        () -> {
            final var updatedCommunication = communicationService.updateCommunication(
                communicationService.getCommunicationOrError(communicationId),
                communicationForm,
                CommunicationStatus.DRAFT,
                (RecipientType.OPERATORS.equals(communicationForm.getRecipientType())
                    ? CommunicationJourneyStatus.EMAIL_CONTENT_OPERATORS
                    : CommunicationJourneyStatus.EMAIL_CONTENT_SUBSCRIBERS)
            );
            return getCommunicationNextUrl(updatedCommunication);
        }
    );
  }

  @GetMapping("/communication/{communicationId}/operator-select")
  @CommunicationJourney(journeyStage = CommunicationJourneyStage.OPERATOR_SELECT)
  public ModelAndView getOperatorSelectForCommunication(@PathVariable("communicationId") Integer communicationId,
                                                        CommunicationContext communicationContext,
                                                        AuthenticatedUserAccount user) {
    final var communication = communicationContext.getCommunication();
    return communicationModelService.getOperatorSelectForCommunication(
        communication,
        organisationGroupCommunicationService.getOrganisationGroupSelectorForm(communication)
    );
  }

  @PostMapping("/communication/{communicationId}/operator-select")
  @CommunicationJourney(journeyStage = CommunicationJourneyStage.OPERATOR_SELECT)
  public ModelAndView saveOperatorSelectForCommunication(@PathVariable("communicationId") Integer communicationId,
                                                         @Valid @ModelAttribute("form") OrganisationGroupSelectorForm form,
                                                         BindingResult bindingResult,
                                                         CommunicationContext communicationContext,
                                                         AuthenticatedUserAccount user) {
    bindingResult = organisationGroupCommunicationService.validateOrganisationSelectionForm(
        form,
        bindingResult,
        ValidationType.FULL
    );

    final var communication = communicationContext.getCommunication();

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        communicationModelService.getOperatorSelectForCommunication(
            communication,
            form
        ),
        form,
        () -> {
          organisationGroupCommunicationService.saveOrganisationGroupCommunication(form, communication);
          communicationService.updateCommunicationJourneyStatus(communication, CommunicationJourneyStatus.OPERATOR_SELECT);
          return ReverseRouter.redirect(on(CommunicationJourneyController.class).getCommunicationConfirmation(
              communicationId,
              null,
              null
          ));
        }
    );
  }

  @GetMapping("/communication/{communicationId}/confirmation")
  @CommunicationJourney(journeyStage = CommunicationJourneyStage.REVIEW_AND_SEND)
  public ModelAndView getCommunicationConfirmation(@PathVariable("communicationId") Integer communicationId,
                                                   CommunicationContext communicationContext,
                                                   AuthenticatedUserAccount user) {
    return communicationModelService.getCommunicationConfirmation(communicationContext.getCommunication());
  }

  @PostMapping("/communication/{communicationId}/confirmation")
  @CommunicationJourney(journeyStage = CommunicationJourneyStage.REVIEW_AND_SEND)
  public ModelAndView saveCommunicationConfirmation(@PathVariable("communicationId") Integer communicationId,
                                                    CommunicationContext communicationContext,
                                                    AuthenticatedUserAccount user) {
    communicationService.finaliseCommunication(communicationContext.getCommunication(), user);
    return ReverseRouter.redirect(on(CommunicationSummaryController.class).getCommunicationsSummary(null));
  }

  private ModelAndView getCommunicationNextUrl(Communication communication) {
    if (RecipientType.OPERATORS.equals(communication.getRecipientType())) {
      return ReverseRouter.redirect(on(CommunicationJourneyController.class).getOperatorSelectForCommunication(
          communication.getId(),
          null,
          null
      ));
    } else {
      return ReverseRouter.redirect(on(CommunicationJourneyController.class).getCommunicationConfirmation(
          communication.getId(),
          null,
          null
      ));
    }
  }
}
