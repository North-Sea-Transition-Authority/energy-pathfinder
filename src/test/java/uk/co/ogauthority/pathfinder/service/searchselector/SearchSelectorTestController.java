package uk.co.ogauthority.pathfinder.service.searchselector;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SearchSelectorTestController {

  private static final String SEARCH_TERM_PARAM_NAME = SearchSelectorService.SEARCH_TERM_PARAM_NAME;

  @GetMapping("/ends-with-{urlEndingCharacter}")
  public ModelAndView getUrlEndsWith(@PathVariable("urlEndingCharacter") String urlEndingCharacter,
                                     @Nullable @RequestParam(SEARCH_TERM_PARAM_NAME) String searchTerm) {
    return new ModelAndView();
  }

  @GetMapping("/get-with-term-param")
  public ModelAndView getWithTermParam(@Nullable @RequestParam(SEARCH_TERM_PARAM_NAME) String searchTerm) {
    return new ModelAndView();
  }
}