package uk.co.ogauthority.pathfinder.service.communication;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.communication.CommunicationJourneyController;
import uk.co.ogauthority.pathfinder.controller.communication.CommunicationSummaryController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.model.form.communication.CommunicationForm;
import uk.co.ogauthority.pathfinder.model.form.communication.OrganisationGroupSelectorForm;
import uk.co.ogauthority.pathfinder.model.team.TeamType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;

@Service
public class CommunicationModelService {

  public static final String COMMUNICATIONS_SUMMARY_PAGE_TITLE = "Communications";
  public static final String COMMUNICATION_SUMMARY_PAGE_TITLE = "Communication";
  public static final String COMMUNICATION_CONTENT_PAGE_TITLE = "Send new email";
  public static final String OPERATOR_SELECT_PAGE_TITLE = "Which operators should receive this email?";
  public static final String COMMUNICATION_CONFIRM_PAGE_TITLE = "Review and send";

  private final BreadcrumbService breadcrumbService;
  private final PortalOrganisationAccessor portalOrganisationAccessor;
  private final CommunicationViewService communicationViewService;

  @Autowired
  public CommunicationModelService(BreadcrumbService breadcrumbService,
                                   PortalOrganisationAccessor portalOrganisationAccessor,
                                   CommunicationViewService communicationViewService) {
    this.breadcrumbService = breadcrumbService;
    this.portalOrganisationAccessor = portalOrganisationAccessor;
    this.communicationViewService = communicationViewService;
  }

  public ModelAndView getCommunicationsSummaryModelAndView() {
    var modelAndView = new ModelAndView("communication/communicationsSummary")
        .addObject("pageTitle", CommunicationModelService.COMMUNICATIONS_SUMMARY_PAGE_TITLE)
        .addObject("addCommunicationUrl",
            ReverseRouter.route(on(CommunicationJourneyController.class).startCommunicationJourney(null))
        )
        .addObject("sentCommunicationViews", communicationViewService.getSentCommunicationViews());

    breadcrumbService.fromWorkArea(modelAndView, CommunicationModelService.COMMUNICATIONS_SUMMARY_PAGE_TITLE);

    return modelAndView;
  }

  public ModelAndView getCommunicationContentModelAndView(CommunicationForm communicationForm) {
    return new ModelAndView("communication/communication")
        .addObject("pageTitle", CommunicationModelService.COMMUNICATION_CONTENT_PAGE_TITLE)
        .addObject("recipientTypes", RecipientType.getAllAsMap())
        .addObject("form", communicationForm)
        .addObject("cancelUrl",
            ReverseRouter.route(on(CommunicationSummaryController.class).getCommunicationsSummary(null))
        );
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

    return new ModelAndView("communication/organisationGroupSelect")
        .addObject("pageTitle", CommunicationModelService.OPERATOR_SELECT_PAGE_TITLE)
        .addObject("previousUrl",
            ReverseRouter.route(on(CommunicationJourneyController.class).getCommunicationContent(
                communication.getId(),
                null,
                null
            ))
        )
        .addObject("organisationGroups", organisationGroupMap)
        .addObject("form", form);
  }

  public ModelAndView getCommunicationConfirmation(Communication communication) {

    final var communicationId = communication.getId();

    var modelAndView = new ModelAndView("communication/confirmation")
        .addObject("pageTitle", CommunicationModelService.COMMUNICATION_CONFIRM_PAGE_TITLE)
        .addObject("communicationView", communicationViewService.getCommunicationView(communication));

    var previousUrl = "";

    if (communication.getRecipientType().equals(RecipientType.OPERATORS)) {
      previousUrl = ReverseRouter.route(on(CommunicationJourneyController.class).getOperatorSelectForCommunication(
          communicationId,
          null,
          null
      ));
    } else {
      previousUrl = ReverseRouter.route(on(CommunicationJourneyController.class).getCommunicationContent(
          communicationId,
          null,
          null
      ));
    }

    modelAndView.addObject("previousUrl", previousUrl);

    return modelAndView;
  }

  public ModelAndView getCommunicationSummaryModelAndView(Communication communication) {
    var modelAndView = new ModelAndView("communication/communicationSummary")
        .addObject("pageTitle", COMMUNICATION_SUMMARY_PAGE_TITLE)
        .addObject("sentCommunicationView",
            communicationViewService.getSentCommunicationView(communication)
        )
        .addObject("communicationsUrl",
            ReverseRouter.route(on(CommunicationSummaryController.class).getCommunicationsSummary(null))
        );

    breadcrumbService.fromCommunicationsSummary(modelAndView, COMMUNICATION_SUMMARY_PAGE_TITLE);

    return modelAndView;
  }
}
