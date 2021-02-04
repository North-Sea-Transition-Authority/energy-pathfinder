package uk.co.ogauthority.pathfinder.service.communication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.ModelMap;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.controller.communication.CommunicationController;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.entity.communication.OrganisationGroupCommunication;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.model.form.communication.CommunicationForm;
import uk.co.ogauthority.pathfinder.model.form.communication.OrganisationGroupSelectorForm;
import uk.co.ogauthority.pathfinder.model.team.TeamType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class CommunicationModelServiceTest {

  @Mock
  private BreadcrumbService breadcrumbService;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  @Mock
  private OrganisationGroupCommunicationService organisationGroupCommunicationService;

  @Mock
  private ServiceProperties serviceProperties;

  private CommunicationModelService communicationModelService;

  @Before
  public void setup() {
    communicationModelService = new CommunicationModelService(
        breadcrumbService,
        portalOrganisationAccessor,
        organisationGroupCommunicationService,
        serviceProperties
    );
  }

  @Test
  public void getCommunicationSummaryModelAndView() {
    var modelAndView = communicationModelService.getCommunicationSummaryModelAndView();
    assertThat(modelAndView.getModelMap()).containsExactly(
        entry("pageTitle", CommunicationModelService.COMMUNICATION_SUMMARY_PAGE_TITLE),
        entry("addCommunicationUrl", ReverseRouter.route(on(CommunicationController.class).startCommunicationJourney(null)))
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
        entry("cancelUrl", ReverseRouter.route(on(CommunicationController.class).getCommunicationSummary(null)))
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
        entry("previousUrl", ReverseRouter.route(on(CommunicationController.class).getCommunicationContent(
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

    var organisationGroupCommunication1 = new OrganisationGroupCommunication();
    organisationGroupCommunication1.setOrganisationGroup(TeamTestingUtil.generateOrganisationGroup(2, "B company", "B company"));

    var organisationGroupCommunication2 = new OrganisationGroupCommunication();
    organisationGroupCommunication2.setOrganisationGroup(TeamTestingUtil.generateOrganisationGroup(1, "A company", "A company"));

    final var organisationGroupCommunications = List.of(organisationGroupCommunication1,organisationGroupCommunication2);

    when(organisationGroupCommunicationService.getOrganisationGroupCommunications(communication))
       .thenReturn(organisationGroupCommunications);

    var modelAndView = communicationModelService.getCommunicationConfirmation(communication);

    var expectedRecipientList = List.of(
        organisationGroupCommunication2.getOrganisationGroup().getSelectionText(),
        organisationGroupCommunication1.getOrganisationGroup().getSelectionText()
    );

    assertConfirmationModelProperties(
        modelAndView.getModelMap(),
        communication,
        StringUtils.join(expectedRecipientList, ", "),
        ReverseRouter.route(on(CommunicationController.class).getOperatorSelectForCommunication(
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

    final var serviceName = "TEST SERVICE NAME";
    when(serviceProperties.getServiceName()).thenReturn(serviceName);

    var modelAndView = communicationModelService.getCommunicationConfirmation(communication);

    assertConfirmationModelProperties(
        modelAndView.getModelMap(),
        communication,
        String.format("%s subscribers", serviceName),
        ReverseRouter.route(on(CommunicationController.class).getCommunicationContent(
            communication.getId(),
            null,
            null
        ))
    );
  }

  private void assertConfirmationModelProperties(ModelMap modelMap,
                                                 Communication communication,
                                                 String recipientString,
                                                 String previousUrl) {
    assertThat(modelMap).containsExactly(
        entry("pageTitle", CommunicationModelService.COMMUNICATION_CONFIRM_PAGE_TITLE),
        entry("communication", communication),
        entry("recipientString", recipientString),
        entry("greetingText", EmailProperties.DEFAULT_GREETING_TEXT),
        entry("signOffText", EmailProperties.DEFAULT_SIGN_OFF_TEXT),
        entry("signOffIdentifier", EmailProperties.DEFAULT_SIGN_OFF_IDENTIFIER),
        entry("previousUrl", previousUrl)
    );
  }

}