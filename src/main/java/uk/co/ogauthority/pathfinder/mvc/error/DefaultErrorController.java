package uk.co.ogauthority.pathfinder.mvc.error;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

@Controller
public class DefaultErrorController implements ErrorController {

  private final ErrorService errorService;

  @Autowired
  public DefaultErrorController(ErrorService errorService) {
    this.errorService = errorService;
  }

  /**
   * Handles framework-level errors (404s, authorisation failures, filter exceptions) for browser clients. Errors thrown
   * by app code (controller methods and below) are handled in DefaultExceptionResolver.
   */
  @RequestMapping("/error")
  public ModelAndView handleError(HttpServletRequest request) {

    Optional<Integer> statusCode = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
        .map(Integer.class::cast);

    var viewName = statusCode.map(this::getViewName).orElse(ErrorView.DEFAULT_ERROR.getViewName());
    var modelAndView = new ModelAndView(viewName);

    //Look for the Spring specific exception first, fall back to the Servlet exception if not available
    Object dispatcherException = request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE);
    Object servletException = request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE);
    Throwable throwable = (Throwable) ObjectUtils.defaultIfNull(dispatcherException, servletException);

    errorService.addErrorAttributesToModel(modelAndView, throwable);

    return modelAndView;
  }

  private String getViewName(int statusCode) {
    switch (statusCode) {
      case SC_NOT_FOUND:
      case SC_METHOD_NOT_ALLOWED:
        return ErrorView.PAGE_NOT_FOUND.getViewName();
      case SC_FORBIDDEN:
      case SC_UNAUTHORIZED:
        return ErrorView.UNAUTHORISED.getViewName();
      default:
        return ErrorView.DEFAULT_ERROR.getViewName();
    }
  }
}
