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
import uk.co.ogauthority.pathfinder.model.entity.portal.LicenceBlock;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.repository.portal.CurrentLicenceBlocksRepository;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.testutil.LicenceBlockTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class LicenceBlockServiceTest {
  private static final String COMPOSITE_KEY = "12/34a1234a111";
  private static final List<String> COMPOSITE_KEY_LIST = List.of("12/34a1234a111", "12/45a1245a112", "12/56a1256a113");
  private static final List<LicenceBlock> BLOCKS  = List.of(
      LicenceBlockTestUtil.getBlock(COMPOSITE_KEY_LIST.get(0)),
      LicenceBlockTestUtil.getBlock(COMPOSITE_KEY_LIST.get(1)),
      LicenceBlockTestUtil.getBlock(COMPOSITE_KEY_LIST.get(2))
  );

  @Mock
  private CurrentLicenceBlocksRepository currentLicenceBlocksRepository;

  private final SearchSelectorService searchSelectorService = new SearchSelectorService();

  private LicenceBlocksService licenceBlockService;

  @Before
  public void setUp() throws Exception {
    licenceBlockService = new LicenceBlocksService(currentLicenceBlocksRepository, searchSelectorService);
  }

  @Test
  public void findCurrentByReference() {
    when(currentLicenceBlocksRepository.findAllByBlockLocationAndBlockReferenceContainingIgnoreCaseOrderBySortKeyAsc(any(), anyString())).thenReturn(
        Collections.singletonList(LicenceBlockTestUtil.getBlock())
    );
    var blocks = licenceBlockService.findCurrentByReference(LicenceBlockTestUtil.BLOCK_REFERENCE);
    assertThat(blocks.size()).isEqualTo(1);
    assertThat(blocks.get(0).getBlockReference()).isEqualTo(LicenceBlockTestUtil.BLOCK_REFERENCE);
  }

  @Test
  public void findAllByCompositeKeyIn() {
    when(currentLicenceBlocksRepository.findAllByCompositeKeyIn(COMPOSITE_KEY_LIST)).thenReturn(
        BLOCKS
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

  @Test
  public void searchLicenceBlocksWithReferenceContaining() {
    when(currentLicenceBlocksRepository.findAllByBlockLocationAndBlockReferenceContainingIgnoreCaseOrderBySortKeyAsc(any(), any())).thenReturn(
        BLOCKS
    );
    var searchItems = licenceBlockService.searchLicenceBlocksWithReferenceContaining("12");
    assertThat(searchItems.size()).isEqualTo(3);
    assertSearchItemMatchesBlock(searchItems.get(0), BLOCKS.get(0));
    assertSearchItemMatchesBlock(searchItems.get(1), BLOCKS.get(1));
    assertSearchItemMatchesBlock(searchItems.get(2), BLOCKS.get(2));
  }

  private void assertSearchItemMatchesBlock(RestSearchItem searchItem, LicenceBlock block) {
    assertThat(searchItem.getId()).isEqualTo(block.getCompositeKey());
    assertThat(searchItem.getText()).isEqualTo(block.getBlockReference());
  }
}
