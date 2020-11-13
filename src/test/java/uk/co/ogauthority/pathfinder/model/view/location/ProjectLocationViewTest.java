package uk.co.ogauthority.pathfinder.model.view.location;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProjectLocationViewTest {

  @Test
  public void newProjectLocationView_hasInitializedLicenceBlocks() {
    var projectLocationView = new ProjectLocationView();
    assertThat(projectLocationView.getLicenceBlocks()).isNotNull();
  }
}
