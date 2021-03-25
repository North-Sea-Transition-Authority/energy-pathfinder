package uk.co.ogauthority.pathfinder.controller.communication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationJourneyStage;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CommunicationJourney {
  CommunicationJourneyStage journeyStage();
}
