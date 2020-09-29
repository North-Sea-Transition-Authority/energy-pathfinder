package uk.co.ogauthority.pathfinder.service.portal;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.portal.LicenceBlock;
import uk.co.ogauthority.pathfinder.model.enums.portal.BlockLocation;
import uk.co.ogauthority.pathfinder.repository.portal.CurrentLicenceBlocksRepository;

@Service
public class LicenceBlocksService {

  private final CurrentLicenceBlocksRepository licenceBlocksRepository;

  @Autowired
  public LicenceBlocksService(CurrentLicenceBlocksRepository licenceBlocksRepository) {
    this.licenceBlocksRepository = licenceBlocksRepository;
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
