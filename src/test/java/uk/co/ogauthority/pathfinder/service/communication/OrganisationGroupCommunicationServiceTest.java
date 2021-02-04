package uk.co.ogauthority.pathfinder.service.communication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.communication.OrganisationGroupCommunication;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.communication.OrganisationGroupSelectorForm;
import uk.co.ogauthority.pathfinder.repository.communication.OrganisationGroupCommunicationRepository;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.CommunicationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class OrganisationGroupCommunicationServiceTest {

  @Mock
  private OrganisationGroupCommunicationRepository organisationGroupCommunicationRepository;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  @Mock
  private ValidationService validationService;

  private OrganisationGroupCommunicationService organisationGroupCommunicationService;

  @Before
  public void setup() {
    organisationGroupCommunicationService = new OrganisationGroupCommunicationService(
        organisationGroupCommunicationRepository,
        portalOrganisationAccessor,
        validationService
    );
  }

  @Test
  public void getOrganisationGroupCommunications_whenFound_thenPopulatedList() {
    final var communication = CommunicationTestUtil.getCompleteCommunication();
    final var organisationGroupCommunications = List.of(new OrganisationGroupCommunication());

    when(organisationGroupCommunicationRepository.findByCommunication(communication))
        .thenReturn(organisationGroupCommunications);

    var result = organisationGroupCommunicationService.getOrganisationGroupCommunications(communication);
    assertThat(result).isEqualTo(organisationGroupCommunications);
  }

  @Test
  public void getOrganisationGroupCommunications_whenNotFound_thenEmptyList() {
    final var communication = CommunicationTestUtil.getCompleteCommunication();

    when(organisationGroupCommunicationRepository.findByCommunication(communication))
        .thenReturn(Collections.emptyList());

    var result = organisationGroupCommunicationService.getOrganisationGroupCommunications(communication);
    assertThat(result).isEmpty();
  }

  @Test
  public void saveOrganisationGroupCommunication() {

    var form = new OrganisationGroupSelectorForm();
    form.setOrganisationGroups(List.of(1));

    final var organisationGroup = TeamTestingUtil.generateOrganisationGroup(
        1,
        "test",
        "test"
    );

    when(portalOrganisationAccessor.getOrganisationGroupsWhereIdIn(form.getOrganisationGroups())).thenReturn(
        List.of(organisationGroup)
    );

    final var communication = CommunicationTestUtil.getCompleteCommunication();

    var organisationGroupCommunication = new OrganisationGroupCommunication();
    organisationGroupCommunication.setCommunication(communication);
    organisationGroupCommunication.setOrganisationGroup(organisationGroup);

    organisationGroupCommunicationService.saveOrganisationGroupCommunication(form, communication);

    verify(organisationGroupCommunicationRepository, times(1)).deleteByCommunication(communication);

    verify(organisationGroupCommunicationRepository, times(1)).saveAll(List.of(organisationGroupCommunication));
  }

  @Test(expected = RuntimeException.class)
  public void saveOrganisationGroupCommunication_whenNotAllFormItemsFound_thenException() {
    var form = new OrganisationGroupSelectorForm();
    form.setOrganisationGroups(List.of(1, 2));

    final var organisationGroup = TeamTestingUtil.generateOrganisationGroup(
        1,
        "test",
        "test"
    );

    when(portalOrganisationAccessor.getOrganisationGroupsWhereIdIn(form.getOrganisationGroups())).thenReturn(
        List.of(organisationGroup)
    );

    final var communication = CommunicationTestUtil.getCompleteCommunication();

    organisationGroupCommunicationService.saveOrganisationGroupCommunication(form, communication);

    verify(organisationGroupCommunicationRepository, never()).deleteByCommunication(communication);

    verify(organisationGroupCommunicationRepository, never()).saveAll(any());

  }

  @Test
  public void getOrganisationGroupSelectorForm_whenOrganisationGroups_thenPopulatedList() {

    final var communication = CommunicationTestUtil.getCompleteCommunication();

    var organisationGroupCommunication = new OrganisationGroupCommunication();
    organisationGroupCommunication.setCommunication(communication);
    organisationGroupCommunication.setOrganisationGroup(TeamTestingUtil.generateOrganisationGroup(
        1,
        "test",
        "test"
    ));

    when(organisationGroupCommunicationRepository.findByCommunication(communication))
        .thenReturn(List.of(organisationGroupCommunication));

    var result = organisationGroupCommunicationService.getOrganisationGroupSelectorForm(communication);
    assertThat(result.getOrganisationGroups()).isEqualTo(
        List.of(organisationGroupCommunication.getOrganisationGroup().getOrgGrpId())
    );
  }

  @Test
  public void getOrganisationGroupSelectorForm_whenNoOrganisationGroups_thenEmptyList() {

    final var communication = CommunicationTestUtil.getCompleteCommunication();

    when(organisationGroupCommunicationRepository.findByCommunication(communication))
        .thenReturn(Collections.emptyList());

    var result = organisationGroupCommunicationService.getOrganisationGroupSelectorForm(communication);
    assertThat(result.getOrganisationGroups()).isEmpty();
  }

  @Test
  public void validateOrganisationSelectionForm() {
    final var form = new OrganisationGroupSelectorForm();

    var bindingResult = new BeanPropertyBindingResult(OrganisationGroupSelectorForm.class, "form");

    organisationGroupCommunicationService.validateOrganisationSelectionForm(
        form,
        bindingResult,
        ValidationType.FULL
    );

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void deleteOrganisationGroupCommunications() {
    final var communication = CommunicationTestUtil.getCompleteCommunication();
    organisationGroupCommunicationService.deleteOrganisationGroupCommunications(communication);
    verify(organisationGroupCommunicationRepository, times(1)).deleteByCommunication(communication);
  }
}