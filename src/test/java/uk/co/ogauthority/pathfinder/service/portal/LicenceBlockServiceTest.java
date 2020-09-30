package uk.co.ogauthority.pathfinder.service.portal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.repository.portal.CurrentLicenceBlocksRepository;
import uk.co.ogauthority.pathfinder.testutil.LicenceBlockTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class LicenceBlockServiceTest {
  private static final String COMPOSITE_KEY = "12/34a1234a111";
  private static final List<String> COMPOSITE_KEY_LIST = List.of("12/34a1234a111", "12/45a1245a112", "12/56a1256a113");

  @Mock
  private CurrentLicenceBlocksRepository currentLicenceBlocksRepository;

  private LicenceBlocksService licenceBlockService;

  @Before
  public void setUp() throws Exception {
    licenceBlockService = new LicenceBlocksService(currentLicenceBlocksRepository);
  }

  @Test
  public void findCurrentByReference() {
    when(currentLicenceBlocksRepository.findAllByBlockLocationAndBlockReferenceContainingIgnoreCase(any(), anyString())).thenReturn(
        Collections.singletonList(LicenceBlockTestUtil.getBlock())
    );
    var blocks = licenceBlockService.findCurrentByReference(LicenceBlockTestUtil.BLOCK_REFERENCE);
    assertThat(blocks.size()).isEqualTo(1);
    assertThat(blocks.get(0).getBlockReference()).isEqualTo(LicenceBlockTestUtil.BLOCK_REFERENCE);
  }

  @Test
  public void findAllByCompositeKeyIn() {
    when(currentLicenceBlocksRepository.findAllByCompositeKeyIn(COMPOSITE_KEY_LIST)).thenReturn(
        List.of(
            LicenceBlockTestUtil.getBlock(COMPOSITE_KEY_LIST.get(0)),
            LicenceBlockTestUtil.getBlock(COMPOSITE_KEY_LIST.get(1)),
            LicenceBlockTestUtil.getBlock(COMPOSITE_KEY_LIST.get(2))
        )
    );

    var blocks = licenceBlockService.findAllByCompositeKeyIn(COMPOSITE_KEY_LIST);
    assertThat(blocks.size()).isEqualTo(3);
    assertThat(blocks.get(0).getBlockReference()).isEqualTo(COMPOSITE_KEY_LIST.get(0));
    assertThat(blocks.get(1).getBlockReference()).isEqualTo(COMPOSITE_KEY_LIST.get(1));
    assertThat(blocks.get(2).getBlockReference()).isEqualTo(COMPOSITE_KEY_LIST.get(2));
  }

  @Test
  public void blockExists_whenNotExists() {
    when(currentLicenceBlocksRepository.existsByCompositeKey(COMPOSITE_KEY)).thenReturn(false);
    assertThat(licenceBlockService.blockExists(COMPOSITE_KEY)).isFalse();
  }

  @Test
  public void blockExists_whenExists() {
    when(currentLicenceBlocksRepository.existsByCompositeKey(COMPOSITE_KEY)).thenReturn(true);
    assertThat(licenceBlockService.blockExists(COMPOSITE_KEY)).isTrue();
  }
}
