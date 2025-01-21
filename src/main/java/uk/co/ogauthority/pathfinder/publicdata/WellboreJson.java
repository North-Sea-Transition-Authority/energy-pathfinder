package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.wellbore.Wellbore;

record WellboreJson(
    String registrationNumber,
    String mechanicalStatus
) {

  static WellboreJson from(Wellbore wellbore) {
    var registrationNumber = wellbore.getRegistrationNo();
    var mechanicalStatus = wellbore.getMechanicalStatus();

    return new WellboreJson(
        registrationNumber,
        mechanicalStatus
    );
  }
}
