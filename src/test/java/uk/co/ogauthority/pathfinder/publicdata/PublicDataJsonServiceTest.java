package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;

@ExtendWith(MockitoExtension.class)
class PublicDataJsonServiceTest {

  @Mock
  private InfrastructureProjectJsonService infrastructureProjectJsonService;

  @InjectMocks
  private PublicDataJsonService publicDataJsonService;

  @Test
  void getPublicDataJson() {
    var infrastructureProjectJsons = List.of(new InfrastructureProjectJson(1, ProjectStatus.PUBLISHED, 1));

    when(infrastructureProjectJsonService.getPublishedInfrastructureProjects()).thenReturn(infrastructureProjectJsons);

    assertThat(publicDataJsonService.getPublicDataJson()).isEqualTo(new PublicDataJson(infrastructureProjectJsons));
  }
}
