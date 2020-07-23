package uk.co.ogauthority.pathfinder.mvc.argumentresolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.util.SecurityUtil;

public class AuthenticatedUserAccountArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(AuthenticatedUserAccount.class);
  }

  @Override
  public AuthenticatedUserAccount resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
    return SecurityUtil.getAuthenticatedUserFromSecurityContext()
        .orElseThrow(() -> new RuntimeException("Failed to get AuthenticatedUserAccount from current authentication context"));
  }

}
