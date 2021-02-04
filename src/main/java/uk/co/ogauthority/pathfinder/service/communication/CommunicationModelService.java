package uk.co.ogauthority.pathfinder.service.communication;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.controller.communication.CommunicationController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.model.form.communication.CommunicationForm;
import uk.co.ogauthority.pathfinder.model.form.communication.OrganisationGroupSelectorForm;
import uk.co.ogauthority.pathfinder.model.team.TeamType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;

@Service
public class CommunicationModelService {

  public static final String COMMUNICATION_SUMMARY_PAGE_TITLE = "Communications";
  public static final String COMMUNICATION_CONTENT_PAGE_TITLE = "Send new email";
  public static final String OPERATOR_BREADCRUMB_TITLE = "Operators";
  public static final String OPERATOR_SELECT_PAGE_TITLE = "Which operators should receive this email?";
  public static final String COMMUNICATION_CONFIRM_PAGE_TITLE = "Review and send";

  private final BreadcrumbService breadcrumbService;
  private final PortalOrganisationAccessor portalOrganisationAccessor;
  private final OrganisationGroupCommunicationService organisationGroupCommunicationService;
  private final ServiceProperties serviceProperties;

  @Autowired
  public CommunicationModelService(BreadcrumbService breadcrumbService,
                                   PortalOrganisationAccessor portalOrganisationAccessor,
                                   OrganisationGroupCommunicationService organisationGroupCommunicationService,
                                   ServiceProperties serviceProperties
                                   ) {
    this.breadcrumbService = breadcrumbService;
    this.portalOrganisationAccessor = portalOrganisationAccessor;
    this.organisationGroupCommunicationService = organisationGroupCommunicationService;
    this.serviceProperties = serviceProperties;
  }

  public ModelAndView getCommunicationSummaryModelAndView() {
    var modelAndView = new ModelAndView("communication/communicationSummary")
        .addObject("pageTitle", CommunicationModelService.COMMUNICATION_SUMMARY_PAGE_TITLE)
        .addObject("addCommunicationUrl",
            ReverseRouter.route(on(CommunicationController.class).startCommunicationJourney(null))
        );

    breadcrumbService.fromWorkArea(modelAndView, CommunicationModelService.COMMUNICATION_SUMMARY_PAGE_TITLE);

    return modelAndView;
  }

  public ModelAndView getCommunicationContentModelAndView(CommunicationForm communicationForm) {
    var modelAndView = new ModelAndView("communication/communication")
        .addObject("pageTitle", CommunicationModelService.COMMUNICATION_CONTENT_PAGE_TITLE)
        .addObject("recipientTypes", RecipientType.getAllAsMap())
        .addObject("form", communicationForm)
        .addObject("cancelUrl",
            ReverseRouter.route(on(CommunicationController.class).getCommunicationSummary(null))
        );

    breadcrumbService.fromCommunicationSummary(modelAndView, CommunicationModelService.COMMUNICATION_CONTENT_PAGE_TITLE);
    return modelAndView;
  }

  public ModelAndView getOperatorSelectForCommunication(Communication communication,
                                                        OrganisationGroupSelectorForm form) {

    final var organisationGroupMap = portalOrganisationAccessor.getAllOrganisationGroupsWithAssociatedTeamType(
        TeamType.ORGANISATION
    )
        .stream()
        .sorted(Comparator.comparing(PortalOrganisationGroup::getSelectionText))
        .collect(
            Collectors.toMap(
                PortalOrganisationGroup::getSelectionId,
                PortalOrganisationGroup::getSelectionText,
                (x, y) -> y,
                LinkedHashMap::new
            )
        );

    var modelAndView = new ModelAndView("communication/organisationGroupSelect")
        .addObject("pageTitle", CommunicationModelService.OPERATOR_SELECT_PAGE_TITLE)
        .addObject("previousUrl",
            ReverseRouter.route(on(CommunicationController.class).getCommunicationContent(
                communication.getId(),
                null,
                null
            ))
        )
        .addObject("organisationGroups", organisationGroupMap)
        .addObject("form", form);

    breadcrumbService.fromCommunicationEmailContent(
        communication.getId(),
        modelAndView,
        CommunicationModelService.OPERATOR_BREADCRUMB_TITLE
    );

    return modelAndView;
  }

  public ModelAndView getCommunicationConfirmation(Communication communication) {

    final var communicationId = communication.getId();

    var modelAndView = new ModelAndView("communication/confirmation")
        .addObject("pageTitle", CommunicationModelService.COMMUNICATION_CONFIRM_PAGE_TITLE)
        .addObject("communication", communication)
        .addObject("recipientString", getRecipientNames(communication))
        .addObject("greetingText", EmailProperties.DEFAULT_GREETING_TEXT)
        .addObject("signOffText", EmailProperties.DEFAULT_SIGN_OFF_TEXT)
        .addObject("signOffIdentifier", EmailProperties.DEFAULT_SIGN_OFF_IDENTIFIER);

    var previousUrl = ReverseRouter.route(on(CommunicationController.class).getCommunicationContent(
        communicationId,
        null,
        null
    ));

    if (communication.getRecipientType().equals(RecipientType.OPERATORS)) {
      breadcrumbService.fromCommunicationOrganisationGroupSelect(
          communicationId,
          modelAndView,
          CommunicationModelService.COMMUNICATION_CONFIRM_PAGE_TITLE
      );

      previousUrl = ReverseRouter.route(on(CommunicationController.class).getOperatorSelectForCommunication(
          communicationId,
          null,
          null
      ));
    } else {
      breadcrumbService.fromCommunicationEmailContent(
          communicationId,
          modelAndView,
          CommunicationModelService.COMMUNICATION_CONFIRM_PAGE_TITLE
      );
    }

    modelAndView.addObject("previousUrl", previousUrl);

    return modelAndView;
  }

  private String getRecipientNames(Communication communication) {

    var recipientNames = "";

    if (communication.getRecipientType().equals(RecipientType.OPERATORS)) {
      var organisationRecipients = organisationGroupCommunicationService.getOrganisationGroupCommunications(communication)
          .stream()
          .map(organisationGroupCommunication -> organisationGroupCommunication.getOrganisationGroup().getName())
          .sorted()
          .collect(Collectors.toList());

      recipientNames = StringUtils.join(organisationRecipients, ", ");
    } else if (communication.getRecipientType().equals(RecipientType.SUBSCRIBERS)) {
      recipientNames = String.format("%s subscribers", serviceProperties.getServiceName());
    } else {
      throw new RuntimeException(
          String.format("Could not determine recipient type for communication with id %d", communication.getId())
      );
    }

    return recipientNames;
  }

}
