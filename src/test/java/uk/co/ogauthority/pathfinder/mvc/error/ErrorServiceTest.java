package uk.co.ogauthority.pathfinder.mvc.error;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.model.enums.contact.ServiceContactDetail;
import uk.co.ogauthority.pathfinder.mvc.footer.FooterService;

@RunWith(MockitoJUnitRunner.class)
public class ErrorServiceTest {

  @Mock
  private ServiceProperties serviceProperties;

  @Mock
  private FooterService footerService;

  private ErrorService errorService;

  @Before
  public void setup() {
    errorService = new ErrorService(serviceProperties, footerService);
  }

  @Test
  public void addErrorAttributesToModel_whenThrowableError_assertExpectedModelAttributes() {
    final var resultingModelMap =  errorService.addErrorAttributesToModel(
        new ModelAndView(),
        new NullPointerException()
    ).getModelMap();

    assertThat(resultingModelMap).containsOnlyKeys(
        "errorRef",
        "technicalSupportContact",
        "service"
    );
    assertThat(resultingModelMap.get("errorRef")).isNotNull();
    assertCommonModelProperties(resultingModelMap);
  }

  @Test
  public void addErrorAttributesToModel_whenNoThrowableError_assertExpectedModelAttributes() {
    final var resultingModelMap =  errorService.addErrorAttributesToModel(
        new ModelAndView(),
        null
    ).getModelMap();

    assertThat(resultingModelMap).containsOnlyKeys(
        "technicalSupportContact",
        "service"
    );
    assertCommonModelProperties(resultingModelMap);
  }

  private void assertCommonModelProperties(ModelMap modelMap) {
    assertThat(modelMap.get("technicalSupportContact")).isEqualTo(ServiceContactDetail.TECHNICAL_SUPPORT);
    assertThat(modelMap.get("service")).isEqualTo(serviceProperties);
  }

}