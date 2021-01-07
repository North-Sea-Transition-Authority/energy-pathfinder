package uk.co.ogauthority.pathfinder.service.portal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;
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
  /**
   * These blocks are intentionally in the wrong order, so we can verify that the
   * methods sort correctly. The correct sorted order is 9/25a, 12/5, 20/1
   */
  private static final List<LicenceBlock> BLOCKS  = List.of(
      LicenceBlockTestUtil.getBlock("20/1", "20", "1", ""),
      LicenceBlockTestUtil.getBlock("9/25a", "9", "25", "a"),
      LicenceBlockTestUtil.getBlock("12/5", "12", "5", "")
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
  public void searchLicenceBlocksWithReferenceContaining() {
    when(currentLicenceBlocksRepository.findAllByBlockLocationAndBlockReferenceContainingIgnoreCase(any(), any()))
        .thenReturn(BLOCKS);

    var searchItems = licenceBlockService.searchLicenceBlocksWithReferenceContaining("/");
    assertThat(searchItems.size()).isEqualTo(3);
    assertSearchItemMatchesBlock(searchItems.get(0), BLOCKS.get(1));
    assertSearchItemMatchesBlock(searchItems.get(1), BLOCKS.get(2));
    assertSearchItemMatchesBlock(searchItems.get(2), BLOCKS.get(0));
  }

  @Test
  public void findCurrentByReference() {
    when(currentLicenceBlocksRepository.findAllByBlockLocationAndBlockReferenceContainingIgnoreCase(any(), eq("/")))
        .thenReturn(BLOCKS);

    var blocks = licenceBlockService.findCurrentByReference("/");
    assertThat(blocks.size()).isEqualTo(3);
    assertThat(blocks.get(0).getBlockReference()).isEqualTo(BLOCKS.get(1).getBlockReference());
    assertThat(blocks.get(1).getBlockReference()).isEqualTo(BLOCKS.get(2).getBlockReference());
    assertThat(blocks.get(2).getBlockReference()).isEqualTo(BLOCKS.get(0).getBlockReference());
  }

  @Test
  public void findAllByCompositeKeyIn() {
    var compositeKeys = BLOCKS.stream()
        .map(LicenceBlock::getCompositeKey)
        .collect(Collectors.toList());

    when(currentLicenceBlocksRepository.findAllByCompositeKeyIn(compositeKeys)).thenReturn(
        BLOCKS
    );

    var blocks = licenceBlockService.findAllByCompositeKeyIn(compositeKeys);
    assertThat(blocks).isEqualTo(BLOCKS);
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

  private void assertSearchItemMatchesBlock(RestSearchItem searchItem, LicenceBlock block) {
    assertThat(searchItem.getId()).isEqualTo(block.getCompositeKey());
    assertThat(searchItem.getText()).isEqualTo(block.getBlockReference());
  }
}
