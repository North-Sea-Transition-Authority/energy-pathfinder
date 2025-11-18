package uk.co.ogauthority.pathfinder.service.team;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.co.fivium.energyportal.serviceproviders.epmq.messages.ServiceProviderTeamRolesEpasMessage;
import uk.co.fivium.energyportal.starter.configuration.EnergyPortalAccountsConfigurationProperties;
import uk.co.fivium.energyportal.starter.serviceproviders.EnergyPortalServiceProviderTeamRolesUpdateHandler;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;

@Component
public class TeamRolesUpdateHandler implements EnergyPortalServiceProviderTeamRolesUpdateHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(TeamRolesUpdateHandler.class);

  private final TeamService teamService;
  private final String serviceName;
  private final WebUserAccountService webUserAccountService;

  TeamRolesUpdateHandler(
      TeamService teamService,
      EnergyPortalAccountsConfigurationProperties energyPortalAccountsConfigurationProperties,
      WebUserAccountService webUserAccountService
  ) {
    this.teamService = teamService;
    this.serviceName = energyPortalAccountsConfigurationProperties.serviceName();
    this.webUserAccountService = webUserAccountService;
  }

  @Override
  public void accept(ServiceProviderTeamRolesEpasMessage serviceProviderTeamRolesEpasMessage) {
    if (!serviceName.equals(serviceProviderTeamRolesEpasMessage.getService())) {
      return;
    }

    var requesterWuaId = serviceProviderTeamRolesEpasMessage.getServiceProviderUserTeamRolesDto().wuaId();
    var optionalRequesterWebUserAccount = webUserAccountService.getWebUserAccount(
        (int) requesterWuaId
    );

    if (optionalRequesterWebUserAccount.isEmpty()) {
      LOGGER.error("Can't resolve web user account from id {} when trying to update user roles from EPAS", requesterWuaId);
      return;
    }

    var deciderWuaId = serviceProviderTeamRolesEpasMessage.getDeciderWuaId();
    var optionalDeciderWebUserAccount = webUserAccountService.getWebUserAccount(
        deciderWuaId.intValue()
    );

    if (optionalDeciderWebUserAccount.isEmpty()) {
      LOGGER.error("Can't resolve web user account from id {} when trying to update user roles from EPAS", deciderWuaId);
      return;
    }

    var team = teamService.getTeamByResId(
        Integer.parseInt(serviceProviderTeamRolesEpasMessage.getServiceProviderUserTeamRolesDto().teamId())
    );
    teamService.addPersonToTeamInRoles(
        team,
        optionalRequesterWebUserAccount.get().getLinkedPerson(),
        serviceProviderTeamRolesEpasMessage.getServiceProviderUserTeamRolesDto().roles(),
        optionalDeciderWebUserAccount.get()
    );
  }
}