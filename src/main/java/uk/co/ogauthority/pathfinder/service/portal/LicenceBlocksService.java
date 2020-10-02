package uk.co.ogauthority.pathfinder.service.portal;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.portal.LicenceBlock;
import uk.co.ogauthority.pathfinder.model.enums.portal.BlockLocation;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.repository.portal.CurrentLicenceBlocksRepository;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Service
public class LicenceBlocksService {

  private final CurrentLicenceBlocksRepository licenceBlocksRepository;
  private final SearchSelectorService searchSelectorService;

  @Autowired
  public LicenceBlocksService(CurrentLicenceBlocksRepository licenceBlocksRepository,
                              SearchSelectorService searchSelectorService) {
    this.licenceBlocksRepository = licenceBlocksRepository;
    this.searchSelectorService = searchSelectorService;
  }

  public List<RestSearchItem> searchLicenceBlocksWithReferenceContaining(String searchTerm) {
    var searchableList = findCurrentByReference(searchTerm);
    return searchSelectorService.search(searchTerm, searchableList)
        .stream()
        .sorted(Comparator.comparing(RestSearchItem::getText))
        .collect(Collectors.toList());
  }


  public List<LicenceBlock> findCurrentByReference(String blockReference) {
    return licenceBlocksRepository.findAllByBlockLocationAndBlockReferenceContainingIgnoreCase(
        BlockLocation.OFFSHORE,
        blockReference
      );
  }

  public List<LicenceBlock> findAllByCompositeKeyIn(List<String> ids) {
    return licenceBlocksRepository.findAllByCompositeKeyIn(ids);
  }

  public List<LicenceBlock> findAllByCompositeKeyInOrdered(List<String> ids) {
    return licenceBlocksRepository.findAllByCompositeKeyInOrderByBlockReference(ids);
  }

  public boolean blockExists(String compositeKey) {
    return licenceBlocksRepository.existsByCompositeKey(compositeKey);
  }
}
