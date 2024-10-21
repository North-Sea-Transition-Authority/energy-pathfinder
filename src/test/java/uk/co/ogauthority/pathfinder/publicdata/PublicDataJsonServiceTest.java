package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublicDataJsonServiceTest {

  @Mock
  private InfrastructureProjectJsonService infrastructureProjectJsonService;

  @InjectMocks
  private PublicDataJsonService publicDataJsonService;

  @Test
  void getPublicDataJson() {
    var infrastructureProjectJsons = List.of(InfrastructureProjectJsonTestUtil.newBuilder().build());

    when(infrastructureProjectJsonService.getPublishedInfrastructureProjects()).thenReturn(infrastructureProjectJsons);

    assertThat(publicDataJsonService.getPublicDataJson()).isEqualTo(new PublicDataJson(infrastructureProjectJsons));
  }
}
