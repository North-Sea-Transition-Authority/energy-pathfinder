package uk.co.ogauthority.pathfinder.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.co.ogauthority.pathfinder.auth.CurrentUserView;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.model.entity.UserAccount;
import uk.co.ogauthority.pathfinder.service.FoxUrlService;

@ControllerAdvice
public class DefaultPageControllerAdvice {

  private final FoxUrlService foxUrlService;
  private final ServiceProperties serviceProperties;

  @Autowired
  public DefaultPageControllerAdvice(FoxUrlService foxUrlService,
                                     ServiceProperties serviceProperties) {
    this.foxUrlService = foxUrlService;
    this.serviceProperties = serviceProperties;
  }

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    // Trim whitespace from form fields
    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
  }

  @ModelAttribute
  public void addCommonModelAttributes(Model model) {
    addCurrentUserView(model);
    addLogoutUrl(model);
    addServiceSpecificAttributes(model);
  }

  private void addCurrentUserView(Model model) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.getPrincipal() instanceof UserAccount) {
      UserAccount user = (UserAccount) authentication.getPrincipal();
      model.addAttribute("currentUserView", CurrentUserView.authenticated(user));
    } else {
      model.addAttribute("currentUserView", CurrentUserView.unauthenticated());
    }
  }

  private void addLogoutUrl(Model model) {
    model.addAttribute("foxLogoutUrl", foxUrlService.getFoxLogoutUrl());
  }

  private void addServiceSpecificAttributes(Model model) {
    model.addAttribute("service", serviceProperties);
  }
}
