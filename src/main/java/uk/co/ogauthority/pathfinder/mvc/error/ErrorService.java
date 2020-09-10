package uk.co.ogauthority.pathfinder.mvc.error;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.model.enums.contact.ServiceContactDetail;

@Service
public class ErrorService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ErrorService.class);

  private final ServiceProperties serviceProperties;

  @Autowired
  public ErrorService(ServiceProperties serviceProperties) {
    this.serviceProperties = serviceProperties;
  }

  private String getErrorReference() {
    return RandomStringUtils.randomNumeric(10);
  }

  private boolean isStackTraceEnabled() {
    return serviceProperties.getStackTraceEnabled();
  }

  private void addStackTraceToModel(ModelAndView modelAndView, Throwable throwable) {
    if (isStackTraceEnabled() && throwable != null) {
      modelAndView.addObject("stackTrace", ExceptionUtils.getStackTrace(throwable));
    }
  }

  private void addErrorReference(ModelAndView modelAndView, Throwable throwable) {
    var errorReference = getErrorReference();
    modelAndView.addObject("errorRef", errorReference);
    LOGGER.error("Caught unhandled exception (errorRef {})", errorReference, throwable);
  }

  private void addContactDetails(ModelAndView modelAndView) {
    modelAndView.addObject("technicalSupportContact", ServiceContactDetail.TECHNICAL_SUPPORT);
  }

  private void addServiceProperties(ModelAndView modelAndView) {
    modelAndView.addObject("service", serviceProperties);
  }

  public ModelAndView addErrorAttributesToModel(ModelAndView modelAndView, Throwable throwable) {
    addStackTraceToModel(modelAndView, throwable);
    addErrorReference(modelAndView, throwable);
    addContactDetails(modelAndView);
    addServiceProperties(modelAndView);
    return modelAndView;
  }
}
