package uk.co.ogauthority.pathfinder.mvc.error;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

@Component
public class DefaultExceptionResolver extends SimpleMappingExceptionResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultExceptionResolver.class);

  private final ErrorService errorService;

  @Autowired
  public DefaultExceptionResolver(ErrorService errorService) {
    this.errorService = errorService;
    setDefaultErrorView(ErrorView.DEFAULT_ERROR.getViewName());
    setDefaultStatusCode(SC_INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ModelAndView getModelAndView(String viewName, Exception ex) {

    if (ex instanceof ClientAbortException) {
      //See https://mtyurt.net/post/spring-how-to-handle-ioexception-broken-pipe.html
      //ClientAbortException indicates a broken pipe/network error. Return null so it can be handled by the servlet,
      //otherwise Spring attempts to write to the broken response.
      LOGGER.trace("Suppressed ClientAbortException");
      return null;
    } else {
      var modelAndView = super.getModelAndView(viewName, ex);
      errorService.addErrorAttributesToModel(modelAndView, ex);

      return modelAndView;
    }
  }
}
