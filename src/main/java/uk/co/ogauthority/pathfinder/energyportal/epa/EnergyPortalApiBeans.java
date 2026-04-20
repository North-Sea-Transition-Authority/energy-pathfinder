package uk.co.ogauthority.pathfinder.energyportal.epa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.fivium.energyportalapi.client.EnergyPortal;
import uk.co.fivium.energyportalapi.client.organisation.OrganisationApi;
import uk.co.ogauthority.pathfinder.correlationid.CorrelationIdUtil;

@Configuration
public class EnergyPortalApiBeans {

  @Bean
  EnergyPortal energyPortal(
      EnergyPortalApiConfig energyPortalApiConfig,
      EpaRequestHandler epaRequestHandler
  ) {
    return EnergyPortal.customConfiguration(
        energyPortalApiConfig.url(),
        energyPortalApiConfig.preSharedKey(),
        EnergyPortal.DEFAULT_REQUEST_TIMEOUT_SECONDS,
        CorrelationIdUtil::getLogCorrelationId,
        epaRequestHandler
    );
  }

  @Bean
  public OrganisationApi organisationApi(EnergyPortal energyPortal) {
    return new OrganisationApi(energyPortal);
  }

}
