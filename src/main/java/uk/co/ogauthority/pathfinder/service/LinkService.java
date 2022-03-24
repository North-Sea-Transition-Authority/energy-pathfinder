package uk.co.ogauthority.pathfinder.service;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.subscription.SubscriptionController;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@Service
public class LinkService {

  @Value("${pathfinder.url.base}")
  private String pathfinderUrlBase;

  @Value("${context-path}")
  private String contextPath;

  public String generateProjectManagementUrl(Project project) {
    return pathfinderUrlBase + contextPath + ControllerUtils.getProjectManagementUrl(project.getId());
  }

  public String getWorkAreaUrl() {
    return pathfinderUrlBase + contextPath + ControllerUtils.getWorkAreaUrl();
  }

  public String getUnsubscribeUrl(String subscriberUuid) {
    return pathfinderUrlBase + contextPath + ReverseRouter.route(on(SubscriptionController.class).unsubscribe(subscriberUuid, Optional.empty()));
  }
}
