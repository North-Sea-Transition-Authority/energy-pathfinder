package uk.co.ogauthority.pathfinder.controller.communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationContext;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationJourneyStage;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationModelService;

@Controller
@RequestMapping("/communications")
public class CommunicationSummaryController {

  private final CommunicationModelService communicationModelService;

  @Autowired
  public CommunicationSummaryController(CommunicationModelService communicationModelService) {
    this.communicationModelService = communicationModelService;
  }

  @GetMapping
  public ModelAndView getCommunicationsSummary(AuthenticatedUserAccount user) {
    return communicationModelService.getCommunicationsSummaryModelAndView();
  }

  @GetMapping("/communication/{communicationId}/summary")
  @CommunicationJourney(journeyStage = CommunicationJourneyStage.SUMMARY)
  public ModelAndView getCommunicationSummary(@PathVariable("communicationId") Integer communicationId,
                                              CommunicationContext communicationContext,
                                              AuthenticatedUserAccount user) {
    return communicationModelService.getCommunicationSummaryModelAndView(communicationContext.getCommunication());
  }
}
