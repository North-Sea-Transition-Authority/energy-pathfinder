package uk.co.ogauthority.pathfinder.service.communication;

import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.enums.communication.CommunicationStatus;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.model.view.communication.CommunicationView;
import uk.co.ogauthority.pathfinder.model.view.communication.SentCommunicationView;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@Service
public class CommunicationViewService {

  private final OrganisationGroupCommunicationService organisationGroupCommunicationService;
  private final ServiceProperties serviceProperties;
  private final WebUserAccountService webUserAccountService;

  @Autowired
  public CommunicationViewService(OrganisationGroupCommunicationService organisationGroupCommunicationService,
                                  ServiceProperties serviceProperties,
                                  WebUserAccountService webUserAccountService) {
    this.organisationGroupCommunicationService = organisationGroupCommunicationService;
    this.serviceProperties = serviceProperties;
    this.webUserAccountService = webUserAccountService;
  }

  public CommunicationView getCommunicationView(Communication communication) {
    return new CommunicationView(
        serviceProperties.getServiceName(),
        getRecipientNames(communication),
        communication.getEmailSubject(),
        communication.getEmailBody(),
        EmailProperties.DEFAULT_GREETING_TEXT,
        EmailProperties.DEFAULT_SIGN_OFF_TEXT,
        EmailProperties.DEFAULT_SIGN_OFF_IDENTIFIER
    );
  }

  public SentCommunicationView getSentCommunicationView(Communication communication) {

    final var communicationStatus = communication.getStatus();

    if (communicationStatus.equals(CommunicationStatus.COMPLETE)
        || communicationStatus.equals(CommunicationStatus.SENDING)
    ) {

      var userAccount = webUserAccountService.getWebUserAccountOrError(communication.getSubmittedByWuaId());

      return new SentCommunicationView(
          serviceProperties.getServiceName(),
          getRecipientNames(communication),
          communication.getEmailSubject(),
          communication.getEmailBody(),
          EmailProperties.DEFAULT_GREETING_TEXT,
          EmailProperties.DEFAULT_SIGN_OFF_TEXT,
          EmailProperties.DEFAULT_SIGN_OFF_IDENTIFIER,
          userAccount.getFullName(),
          userAccount.getEmailAddress(),
          DateUtil.formatInstant(communication.getSubmittedDatetime())
      );
    } else {
      throw new RuntimeException(String.format(
          "Cannot construct SentCommunicationView for communication with id %d and status %s",
          communication.getId(),
          communication.getStatus()
      ));
    }
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
    }

    return recipientNames;
  }
}
