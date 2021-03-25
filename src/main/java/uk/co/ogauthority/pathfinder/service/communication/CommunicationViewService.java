package uk.co.ogauthority.pathfinder.service.communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.enums.communication.CommunicationStatus;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.model.view.communication.CommunicationView;
import uk.co.ogauthority.pathfinder.model.view.communication.EmailView;
import uk.co.ogauthority.pathfinder.model.view.communication.SentCommunicationView;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@Service
public class CommunicationViewService {

  private static final List<CommunicationStatus> VIEWABLE_COMMUNICATION_STATUSES = List.of(
      CommunicationStatus.SENDING,
      CommunicationStatus.SENT
  );

  private final CommunicationService communicationService;
  private final OrganisationGroupCommunicationService organisationGroupCommunicationService;
  private final ServiceProperties serviceProperties;
  private final WebUserAccountService webUserAccountService;

  @Autowired
  public CommunicationViewService(CommunicationService communicationService,
                                  OrganisationGroupCommunicationService organisationGroupCommunicationService,
                                  ServiceProperties serviceProperties,
                                  WebUserAccountService webUserAccountService) {
    this.communicationService = communicationService;
    this.organisationGroupCommunicationService = organisationGroupCommunicationService;
    this.serviceProperties = serviceProperties;
    this.webUserAccountService = webUserAccountService;
  }

  protected CommunicationView getCommunicationView(Communication communication) {
    final var emailView = getEmailView(communication);
    return new CommunicationView(
        communication.getId(),
        emailView,
        communication.getRecipientType() != null ? communication.getRecipientType().getDisplayName() : ""
    );
  }

  protected SentCommunicationView getSentCommunicationView(Communication communication) {
    final var userAccount = webUserAccountService.getWebUserAccountOrError(
        communication.getSubmittedByWuaId()
    );
    return getSentCommunicationView(communication, userAccount);
  }

  protected SentCommunicationView getSentCommunicationView(Communication communication, WebUserAccount userAccount) {

    final var communicationStatus = communication.getStatus();

    if (communicationStatus.equals(CommunicationStatus.SENT)
        || communicationStatus.equals(CommunicationStatus.SENDING)
    ) {

      final var emailView = getEmailView(communication);

      return new SentCommunicationView(
          communication.getId(),
          emailView,
          communication.getRecipientType().getDisplayName(),
          userAccount.getFullName(),
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

  protected List<SentCommunicationView> getSentCommunicationViews() {

    final var communications = communicationService.getCommunicationsWithStatuses(
        VIEWABLE_COMMUNICATION_STATUSES
    );

    if (!communications.isEmpty()) {

      // extract out the list of web user account ids and get them in one database call
      final var wuaIds = communications
          .stream()
          .map(Communication::getSubmittedByWuaId)
          .collect(Collectors.toList());

      final var webUserAccounts = webUserAccountService.getWebUserAccounts(wuaIds)
          .stream()
          .collect(Collectors.toMap(WebUserAccount::getWuaId, webUserAccount -> webUserAccount));

      return communications
          .stream()
          .sorted(Comparator.comparing(Communication::getSubmittedDatetime).reversed())
          .map(communication -> getSentCommunicationView(
              communication,
              webUserAccounts.get(communication.getSubmittedByWuaId())
          ))
          .collect(Collectors.toList());
    }

    return Collections.emptyList();
  }

  private List<String> getRecipientList(Communication communication) {

    List<String> recipientList = new ArrayList<>();

    if (communication.getRecipientType().equals(RecipientType.OPERATORS)) {
      recipientList = organisationGroupCommunicationService.getOrganisationGroupCommunications(communication)
          .stream()
          .map(organisationGroupCommunication -> organisationGroupCommunication.getOrganisationGroup().getName())
          .sorted()
          .collect(Collectors.toList());
    } else if (communication.getRecipientType().equals(RecipientType.SUBSCRIBERS)) {
      recipientList = List.of(String.format("%s subscribers", serviceProperties.getServiceName()));
    }

    return recipientList;
  }

  private EmailView getEmailView(Communication communication) {
    return new EmailView(
        serviceProperties.getServiceName(),
        getRecipientList(communication),
        communication.getEmailSubject(),
        communication.getGreetingText(),
        communication.getEmailBody(),
        communication.getSignOffText(),
        communication.getSignOffIdentifier()
    );
  }
}
