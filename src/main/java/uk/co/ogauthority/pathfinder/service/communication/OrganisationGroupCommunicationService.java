package uk.co.ogauthority.pathfinder.service.communication;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.entity.communication.OrganisationGroupCommunication;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.communication.OrganisationGroupSelectorForm;
import uk.co.ogauthority.pathfinder.repository.communication.OrganisationGroupCommunicationRepository;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class OrganisationGroupCommunicationService {

  private final OrganisationGroupCommunicationRepository organisationGroupCommunicationRepository;
  private final PortalOrganisationAccessor portalOrganisationAccessor;
  private final ValidationService validationService;

  @Autowired
  public OrganisationGroupCommunicationService(
      OrganisationGroupCommunicationRepository organisationGroupCommunicationRepository,
      PortalOrganisationAccessor portalOrganisationAccessor,
      ValidationService validationService
  ) {
    this.organisationGroupCommunicationRepository = organisationGroupCommunicationRepository;
    this.portalOrganisationAccessor = portalOrganisationAccessor;
    this.validationService = validationService;
  }

  public List<OrganisationGroupCommunication> getOrganisationGroupCommunications(Communication communication) {
    return organisationGroupCommunicationRepository.findByCommunication(communication);
  }

  @Transactional
  public List<OrganisationGroupCommunication> saveOrganisationGroupCommunication(OrganisationGroupSelectorForm form,
                                                                                 Communication communication) {
    final var organisationGroups = portalOrganisationAccessor.getOrganisationGroupsWhereIdIn(
        form.getOrganisationGroups()
    );

    if (organisationGroups.size() != form.getOrganisationGroups().size()) {
      throw new RuntimeException(String.format(
          "Not all organisation groups in OrganisationGroupSelectorForm retrieved by getOrganisationGroupsWhereIdIn. " +
              "Form size %d, Retrieved size %d",
          form.getOrganisationGroups().size(),
          organisationGroups.size()
      ));
    }

    deleteOrganisationGroupCommunications(communication);

    var organisationGroupCommunication = organisationGroups
        .stream()
        .map(portalOrganisationGroup -> convertToOrganisationGroupCommunication(communication, portalOrganisationGroup))
        .collect(Collectors.toList());

    return IterableUtils.toList(organisationGroupCommunicationRepository.saveAll(organisationGroupCommunication));
  }

  public OrganisationGroupSelectorForm getOrganisationGroupSelectorForm(Communication communication) {
    final var organisationGroupIds = getOrganisationGroupCommunications(communication)
        .stream()
        .map(organisationGroupCommunication -> organisationGroupCommunication.getOrganisationGroup().getOrgGrpId())
        .collect(Collectors.toList());

    var form = new OrganisationGroupSelectorForm();
    form.setOrganisationGroups(organisationGroupIds);
    return form;
  }

  public BindingResult validateOrganisationSelectionForm(OrganisationGroupSelectorForm organisationGroupSelectorForm,
                                                         BindingResult bindingResult,
                                                         ValidationType validationType) {
    return validationService.validate(organisationGroupSelectorForm, bindingResult, validationType);
  }

  public void deleteOrganisationGroupCommunications(Communication communication) {
    organisationGroupCommunicationRepository.deleteByCommunication(communication);
  }

  private OrganisationGroupCommunication convertToOrganisationGroupCommunication(
      Communication communication,
      PortalOrganisationGroup organisationGroup
  ) {
    var organisationGroupCommunication = new OrganisationGroupCommunication();
    organisationGroupCommunication.setCommunication(communication);
    organisationGroupCommunication.setOrganisationGroup(organisationGroup);
    return organisationGroupCommunication;
  }
}
