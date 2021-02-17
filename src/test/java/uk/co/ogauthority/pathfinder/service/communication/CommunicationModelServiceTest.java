package uk.co.ogauthority.pathfinder.service.communication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.LinkedHashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.ModelMap;
import uk.co.ogauthority.pathfinder.controller.communication.CommunicationJourneyController;
import uk.co.ogauthority.pathfinder.controller.communication.CommunicationSummaryController;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.model.form.communication.CommunicationForm;
import uk.co.ogauthority.pathfinder.model.form.communication.OrganisationGroupSelectorForm;
import uk.co.ogauthority.pathfinder.model.team.TeamType;
import uk.co.ogauthority.pathfinder.model.view.communication.CommunicationView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.testutil.CommunicationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class CommunicationModelServiceTest {

  @Mock
  private BreadcrumbService breadcrumbService;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  @Mock
  private CommunicationViewService communicationViewService;

  private CommunicationModelService communicationModelService;

  @Before
  public void setup() {
    communicationModelService = new CommunicationModelService(
        breadcrumbService,
        portalOrganisationAccessor,
        communicationViewService
    );
  }

  @Test
  public void getCommunicationsSummaryModelAndView() {
    var modelAndView = communicationModelService.getCommunicationsSummaryModelAndView();
    assertThat(modelAndView.getModelMap()).containsExactly(
        entry("pageTitle", CommunicationModelService.COMMUNICATIONS_SUMMARY_PAGE_TITLE),
        entry("addCommunicationUrl", ReverseRouter.route(on(CommunicationJourneyController.class).startCommunicationJourney(null)))
    );
  }

  @Test
  public void getCommunicationContentModelAndView() {
    final var communicationForm = new CommunicationForm();
    var modelAndView = communicationModelService.getCommunicationContentModelAndView(communicationForm);
    assertThat(modelAndView.getModelMap()).containsExactly(
        entry("pageTitle", CommunicationModelService.COMMUNICATION_CONTENT_PAGE_TITLE),
        entry("recipientTypes", RecipientType.getAllAsMap()),
        entry("form", communicationForm),
        entry("cancelUrl", ReverseRouter.route(on(CommunicationSummaryController.class).getCommunicationsSummary(null)))
    );
  }

  @Test
  public void getOperatorSelectForCommunication() {

    final var organisationGroups = List.of(
        TeamTestingUtil.generateOrganisationGroup(1, "C company", "C company"),
        TeamTestingUtil.generateOrganisationGroup(2, "B company", "B company"),
        TeamTestingUtil.generateOrganisationGroup(3, "A company", "A company")
    );

    when(portalOrganisationAccessor.getAllOrganisationGroupsWithAssociatedTeamType(TeamType.ORGANISATION))
        .thenReturn(organisationGroups);

    var communication = new Communication();
    communication.setId(1);

    final var form = new OrganisationGroupSelectorForm();

    var modelAndView = communicationModelService.getOperatorSelectForCommunication(
        communication,
        form
    );

    final var organisationGroup1 = organisationGroups.get(2);
    final var organisationGroup2 = organisationGroups.get(1);
    final var organisationGroup3 = organisationGroups.get(0);
    var expectedOrganisationGroupMap = new LinkedHashMap<String, String>();
    expectedOrganisationGroupMap.put(organisationGroup1.getSelectionId(), organisationGroup1.getSelectionText());
    expectedOrganisationGroupMap.put(organisationGroup2.getSelectionId(), organisationGroup2.getSelectionText());
    expectedOrganisationGroupMap.put(organisationGroup3.getSelectionId(), organisationGroup3.getSelectionText());

    assertThat(modelAndView.getModelMap()).containsExactly(
        entry("pageTitle", CommunicationModelService.OPERATOR_SELECT_PAGE_TITLE),
        entry("previousUrl", ReverseRouter.route(on(CommunicationJourneyController.class).getCommunicationContent(
            communication.getId(),
            null,
            null
        ))),
        entry("organisationGroups", expectedOrganisationGroupMap),
        entry("form", form)
    );

    var resultingOrganisationGroupMap = (LinkedHashMap<String, String>) modelAndView.getModelMap().get("organisationGroups");

    assertThat(resultingOrganisationGroupMap).containsExactly(
        entry(organisationGroup1.getSelectionId(), organisationGroup1.getSelectionText()),
        entry(organisationGroup2.getSelectionId(), organisationGroup2.getSelectionText()),
        entry(organisationGroup3.getSelectionId(), organisationGroup3.getSelectionText())
    );
  }

  @Test
  public void getCommunicationConfirmation_whenOperatorRecipientType() {
    var communication = new Communication();
    communication.setId(1);
    communication.setRecipientType(RecipientType.OPERATORS);

    final var communicationView = CommunicationTestUtil.getCommunicationView();
    when(communicationViewService.getCommunicationView(communication)).thenReturn(communicationView);

    var modelAndView = communicationModelService.getCommunicationConfirmation(communication);

    assertConfirmationModelProperties(
        modelAndView.getModelMap(),
        communicationView,
        ReverseRouter.route(on(CommunicationJourneyController.class).getOperatorSelectForCommunication(
            communication.getId(),
            null,
            null
        ))
    );
  }

  @Test
  public void getCommunicationConfirmation_whenSubscriberRecipientType() {
    var communication = new Communication();
    communication.setId(1);
    communication.setRecipientType(RecipientType.SUBSCRIBERS);

    final var communicationView = CommunicationTestUtil.getCommunicationView();
    when(communicationViewService.getCommunicationView(communication)).thenReturn(communicationView);

    var modelAndView = communicationModelService.getCommunicationConfirmation(communication);

    assertConfirmationModelProperties(
        modelAndView.getModelMap(),
        communicationView,
        ReverseRouter.route(on(CommunicationJourneyController.class).getCommunicationContent(
            communication.getId(),
            null,
            null
        ))
    );
  }

  @Test
  public void getCommunicationSummaryModelAndView() {
    final var communication = CommunicationTestUtil.getCompleteCommunication();
    final var communicationView = CommunicationTestUtil.getSentCommunicationView();
    when(communicationViewService.getSentCommunicationView(communication)).thenReturn(communicationView);

    final var modelAndView = communicationModelService.getCommunicationSummaryModelAndView(communication);

    assertThat(modelAndView.getModelMap()).containsExactly(
        entry("pageTitle", CommunicationModelService.COMMUNICATION_SUMMARY_PAGE_TITLE),
        entry("sentCommunicationView", communicationView),
        entry("communicationsUrl", ReverseRouter.route(on(CommunicationSummaryController.class).getCommunicationsSummary(null)))
    );
  }

  private void assertConfirmationModelProperties(ModelMap modelMap,
                                                 CommunicationView communicationView,
                                                 String previousUrl) {
    assertThat(modelMap).containsExactly(
        entry("pageTitle", CommunicationModelService.COMMUNICATION_CONFIRM_PAGE_TITLE),
        entry("communicationView", communicationView),
        entry("previousUrl", previousUrl)
    );
  }

}