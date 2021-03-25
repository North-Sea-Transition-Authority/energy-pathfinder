package uk.co.ogauthority.pathfinder.service.communication;

import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;

public class CommunicationContext {

  private final Communication communication;

  public CommunicationContext(Communication communication) {
    this.communication = communication;
  }

  public Communication getCommunication() {
    return communication;
  }
}
