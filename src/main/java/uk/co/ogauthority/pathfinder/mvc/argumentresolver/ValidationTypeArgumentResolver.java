package uk.co.ogauthority.pathfinder.mvc.argumentresolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;

/**
 * This class provides a way to transform the results of clicking app form submit buttons (either 'Save and complete later'
 * or 'Save and complete' into the type of validation which should be performed for each.
 * Should only be used when posting a form page that is using the submitButtons macro.
 */
public class ValidationTypeArgumentResolver implements HandlerMethodArgumentResolver {

  public static final String SAVE_AND_COMPLETE_LATER = ValidationType.PARTIAL.getButtonText();
  public static final String COMPLETE = ValidationType.FULL.getButtonText();

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(ValidationType.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

    String saveAndCompleteLater = webRequest.getParameter(SAVE_AND_COMPLETE_LATER);
    String complete = webRequest.getParameter(COMPLETE);

    if (saveAndCompleteLater == null && complete == null) {
      throw new IllegalStateException("Cannot save and complete later or complete as both params are null.");
    }

    return complete != null ? ValidationType.FULL : ValidationType.PARTIAL;

  }

}
