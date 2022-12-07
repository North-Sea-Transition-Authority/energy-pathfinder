package uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProjectContributorConverterTest {

  private ProjectContributorConverter projectContributorConverter;

  @Before
  public void setup() {
    projectContributorConverter = new ProjectContributorConverter();
  }

  @Test
  public void convertToDatabaseColumn_assertNull() {
    var input = List.of(1, 2, 3);
    String expectedOutput = null;

    assertThat(projectContributorConverter.convertToDatabaseColumn(input)).isEqualTo(expectedOutput);
  }

  @Test
  public void convertToEntityAttribute_inputIsNotNull_assertListOfIntegers() {
    var input = "1, 2, 3";
    var expectedOutput = List.of(1, 2, 3);

    assertThat(projectContributorConverter.convertToEntityAttribute(input)).isEqualTo(expectedOutput);
  }

  @Test
  public void convertToEntityAttribute_inputIsNull_assertEmptyListOfIntegers() {
    String input = null;

    assertThat(projectContributorConverter.convertToEntityAttribute(input)).isEmpty();
  }

  @Test
  public void convertToEntityAttribute_inputIsBlank_assertEmptyListOfIntegers() {
    var input = "";

    assertThat(projectContributorConverter.convertToEntityAttribute(input)).isEmpty();
  }
}