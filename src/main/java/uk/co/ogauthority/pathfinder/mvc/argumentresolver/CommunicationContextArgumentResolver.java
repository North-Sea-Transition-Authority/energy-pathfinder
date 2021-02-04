package uk.co.ogauthority.pathfinder.mvc.argumentresolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationContext;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationJourneyService;
import uk.co.ogauthority.pathfinder.util.ArgumentResolverUtil;

@Component
public class CommunicationContextArgumentResolver implements HandlerMethodArgumentResolver {

  private final CommunicationJourneyService communicationJourneyService;

  @Autowired
  public CommunicationContextArgumentResolver(CommunicationJourneyService communicationJourneyService) {
    this.communicationJourneyService = communicationJourneyService;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(CommunicationContext.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter,
                                ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest,
                                WebDataBinderFactory binderFactory) throws Exception {
    final var communicationId = ArgumentResolverUtil.resolveIdFromRequest(
        webRequest,
        ArgumentResolverUtil.COMMUNICATION_ID_PARAM
    );
    final var communication = communicationJourneyService.getCommunicationOrError(communicationId);

    final var communicationJourneyStage = ArgumentResolverUtil.getCommunicationJourneyStage(parameter);

    return communicationJourneyService.checkJourneyStage(communication, communicationJourneyStage);
  }
}
