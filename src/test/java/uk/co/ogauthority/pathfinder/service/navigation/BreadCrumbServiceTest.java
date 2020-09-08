package uk.co.ogauthority.pathfinder.service.navigation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

@RunWith(MockitoJUnitRunner.class)
public class BreadCrumbServiceTest {

  private BreadcrumbService breadcrumbService;

  @Before
  public void setUp() {
    breadcrumbService = new BreadcrumbService();
  }

  @Test
  public void fromWorkArea() {
    var modelAndView = new ModelAndView();
    breadcrumbService.fromWorkArea(modelAndView, "New page");
    assertThat(modelAndView.getModel()).containsOnlyKeys("crumbList", "currentPage");
    assertThat(modelAndView.getModel()).containsEntry("currentPage", "New page");
    var breadcrumbMap = (Map<String, String>) modelAndView.getModel().get("crumbList");
    assertThat(breadcrumbMap).containsValue("Work area");
  }
}
