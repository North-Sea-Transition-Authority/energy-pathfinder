package uk.co.ogauthority.pathfinder.service.rendering;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TemplateRenderingServiceTest {

  @Mock
  private Configuration mockFreemarkerConfig;

  @Mock
  private Template template;

  private TemplateRenderingService templateRenderingService;

  private final Map<String, Object> templateModel = new HashMap<>();

  @Before
  public void setup(){
    templateRenderingService = new TemplateRenderingService(mockFreemarkerConfig);

  }

  /**
   * Very basic test to ensure it doesnt throw an arbitrary error. Tests on specific templates containing expected output are more likely integration tests
   * @throws IOException when failed to load template
   */
  @Test
  public void render() throws IOException {
    final var templateName = "TEMPLATE.ftl";
    when(mockFreemarkerConfig.getTemplate(ArgumentMatchers.eq(templateName), any(Locale.class))).thenReturn(template);
    String renderedTemplate = templateRenderingService.render(templateName, templateModel, false);
    assertThat(renderedTemplate).isNotNull();
  }

}
