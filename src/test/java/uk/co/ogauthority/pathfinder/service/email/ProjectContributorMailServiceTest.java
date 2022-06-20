package uk.co.ogauthority.pathfinder.service.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.OrganisationGroupMembership;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationGroupPersonMembershipService;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.contributor.AddedProjectContributorEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.contributor.RemovedProjectContributorEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution.ProjectContributor;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectContributorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectContributorMailServiceTest {

  private static final String PROJECT_TITLE = "project title";
  private static final String WORK_AREA_URL = "work-area.com";
  private static final String TITLE_PLACEHOLDER = "a %s project created on %s";

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();
  private final ProjectOperator projectOperator = ProjectOperatorTestUtil.getOperator();
  private final PortalOrganisationGroup portalOrganisationGroup1 = TeamTestingUtil.generateOrganisationGroup(1, "org",
      "org");
  private final PortalOrganisationGroup portalOrganisationGroup2 = TeamTestingUtil.generateOrganisationGroup(2, "org",
      "org");
  private final ProjectContributor projectContributor1 = ProjectContributorTestUtil.contributorWithGroupOrg(detail,
      portalOrganisationGroup1);
  private final ProjectContributor projectContributor2 = ProjectContributorTestUtil.contributorWithGroupOrg(detail,
      portalOrganisationGroup2);

  @Mock
  private EmailService emailService;

  @Mock
  private PortalOrganisationGroupPersonMembershipService portalOrganisationGroupPersonMembershipService;

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private LinkService linkService;

  private ProjectContributorMailService projectContributorMailService;

  @Before
  public void setup() {
    projectContributorMailService = new ProjectContributorMailService(
        emailService,
        portalOrganisationGroupPersonMembershipService,
        projectInformationService,
        projectOperatorService,
        linkService);
    when(projectInformationService.getProjectTitle(detail)).thenReturn(PROJECT_TITLE);
    when(projectOperatorService.getProjectOperatorByProjectDetail(detail)).thenReturn(Optional.of(projectOperator));
    when(linkService.getWorkAreaUrl()).thenReturn(WORK_AREA_URL);
  }

  @Test
  public void sendContributorsRemovedEmail_givenAContributor_thenAssertEmailProperties() {
    var membership1 = createOrganisationGroupMembershipForContributor(projectContributor1, 2);
    var membership2 = createOrganisationGroupMembershipForContributor(projectContributor2, 1);
    when(portalOrganisationGroupPersonMembershipService.getOrganisationGroupMembershipForOrganisationGroupIn(
        List.of(
            projectContributor1.getContributionOrganisationGroup(),
            projectContributor2.getContributionOrganisationGroup()
        )
    )).thenReturn(List.of(membership1, membership2));

    projectContributorMailService.sendContributorsRemovedEmail(List.of(projectContributor1, projectContributor2), detail);
    var emailPropertiesCaptor = ArgumentCaptor.forClass(RemovedProjectContributorEmailProperties.class);
    var emailAddressCaptor = ArgumentCaptor.forClass(String.class);

    var  numberOfTeamMembers = membership1.getTeamMembers().size() + membership2.getTeamMembers().size();
    verify(emailService, times(numberOfTeamMembers)).sendEmail(emailPropertiesCaptor.capture(), emailAddressCaptor.capture());

    var emailProperties = (RemovedProjectContributorEmailProperties) emailPropertiesCaptor.getValue();
    var emailAddress = (List<String>) emailAddressCaptor.getAllValues();

    List<String> expectedEmailAddresses = Stream.of(membership1.getTeamMembers(), membership2.getTeamMembers())
        .flatMap(Collection::stream)
        .map(Person::getEmailAddress)
        .collect(Collectors.toList());

    assertThat(emailAddress).containsExactlyElementsOf(expectedEmailAddresses);
    assertThat(emailProperties.getEmailPersonalisation()).contains(
        entry("PROJECT_TYPE_DISPLAY_NAME", ProjectService.getProjectTypeDisplayName(detail)),
        entry("OWNER_OPERATOR_NAME", projectOperator.getOrganisationGroup().getName()),
        entry("PROJECT_TITLE", PROJECT_TITLE)
    );
    assertThat(emailProperties.getEmailPersonalisation().get("RECIPIENT_IDENTIFIER")).isNotNull();
  }

  @Test
  public void sendContributorsEmail_projectTitleNotSet_thenAssertTitleIsProjectType() {
    var membership = createOrganisationGroupMembershipForContributor(projectContributor1, 1);
    detail.setProjectType(ProjectType.FORWARD_WORK_PLAN);
    var formattedTime = DateUtil.formatInstant(detail.getCreatedDatetime());
    var expectedTitle = String.format(TITLE_PLACEHOLDER, detail.getStatus().getDisplayName(), formattedTime);
    when(projectInformationService.getProjectTitle(detail)).thenReturn("");
    when(portalOrganisationGroupPersonMembershipService.getOrganisationGroupMembershipForOrganisationGroupIn(
        List.of(projectContributor1.getContributionOrganisationGroup())
    )).thenReturn(List.of(membership));

    projectContributorMailService.sendContributorsRemovedEmail(List.of(projectContributor1), detail);

    var emailPropertiesCaptor = ArgumentCaptor.forClass(RemovedProjectContributorEmailProperties.class);
    verify(emailService).sendEmail(emailPropertiesCaptor.capture(), any());
    var emailProperties = (RemovedProjectContributorEmailProperties) emailPropertiesCaptor.getValue();
    assertThat(emailProperties.getEmailPersonalisation()).containsEntry("PROJECT_TITLE", expectedTitle);
  }

  @Test
  public void sendContributorsEmail_noProjectOperator_thenProjectTitleDetailsReturned() {
    var membership = createOrganisationGroupMembershipForContributor(projectContributor1, 1);
    detail.setProjectType(ProjectType.FORWARD_WORK_PLAN);
    var expectedOperatorValue = String.format("The %s operator", ProjectService.getProjectTypeDisplayNameLowercase(detail));
    var emptyProjectOperator = new ProjectOperator();
    when(projectOperatorService.getProjectOperatorByProjectDetail(detail)).thenReturn(Optional.of(emptyProjectOperator));
    when(portalOrganisationGroupPersonMembershipService.getOrganisationGroupMembershipForOrganisationGroupIn(
        List.of(projectContributor1.getContributionOrganisationGroup())
    )).thenReturn(List.of(membership));

    projectContributorMailService.sendContributorsRemovedEmail(List.of(projectContributor1), detail);

    var emailPropertiesCaptor = ArgumentCaptor.forClass(RemovedProjectContributorEmailProperties.class);
    verify(emailService).sendEmail(emailPropertiesCaptor.capture(), any());
    var emailProperties = (RemovedProjectContributorEmailProperties) emailPropertiesCaptor.getValue();
    assertThat(emailProperties.getEmailPersonalisation()).containsEntry("OWNER_OPERATOR_NAME", expectedOperatorValue);
  }

  @Test
  public void sendContributorsAddedEmail_givenAContributorList_thenAssertEmailProperties() {
    var membership1 = createOrganisationGroupMembershipForContributor(projectContributor1, 2);
    var membership2 = createOrganisationGroupMembershipForContributor(projectContributor2, 1);
    when(portalOrganisationGroupPersonMembershipService.getOrganisationGroupMembershipForOrganisationGroupIn(
        List.of(
            projectContributor1.getContributionOrganisationGroup(),
            projectContributor2.getContributionOrganisationGroup()
        )
    )).thenReturn(List.of(membership1, membership2));

    projectContributorMailService.sendContributorsAddedEmail(List.of(projectContributor1, projectContributor2), detail);
    var emailPropertiesCaptor = ArgumentCaptor.forClass(AddedProjectContributorEmailProperties.class);
    var emailAddressCaptor = ArgumentCaptor.forClass(String.class);

    var  numberOfTeamMembers = membership1.getTeamMembers().size() + membership2.getTeamMembers().size();
    verify(emailService, times(numberOfTeamMembers)).sendEmail(emailPropertiesCaptor.capture(), emailAddressCaptor.capture());

    var emailProperties = (AddedProjectContributorEmailProperties) emailPropertiesCaptor.getValue();
    var emailAddress = (List<String>) emailAddressCaptor.getAllValues();

    List<String> expectedEmailAddresses = Stream.of(membership1.getTeamMembers(), membership2.getTeamMembers())
        .flatMap(Collection::stream)
        .map(Person::getEmailAddress)
        .collect(Collectors.toList());

    assertThat(emailAddress).containsExactlyElementsOf(expectedEmailAddresses);
    assertThat(emailProperties.getEmailPersonalisation()).contains(
        entry("PROJECT_TYPE_DISPLAY_NAME", ProjectService.getProjectTypeDisplayName(detail)),
        entry("OWNER_OPERATOR_NAME", projectOperator.getOrganisationGroup().getName()),
        entry("PROJECT_TITLE", PROJECT_TITLE),
        entry("SERVICE_LOGIN_URL", WORK_AREA_URL)
    );
    assertThat(emailProperties.getEmailPersonalisation().get("RECIPIENT_IDENTIFIER")).isNotNull();
  }

  private OrganisationGroupMembership createOrganisationGroupMembershipForContributor(
      ProjectContributor projectContributor,
      int numberOfTeamMembers) {
    var people = new ArrayList<Person>();
    IntStream.range(0, numberOfTeamMembers).forEach(personIndex -> {
      people.add(
          new Person(
              personIndex,
              "person",
              String.valueOf(personIndex),
              "person" + personIndex + "@person.com",
              "12345"
          )
      );
    });

    return new OrganisationGroupMembership(
        1,
        projectContributor.getContributionOrganisationGroup(),
        people
    );
  }
}