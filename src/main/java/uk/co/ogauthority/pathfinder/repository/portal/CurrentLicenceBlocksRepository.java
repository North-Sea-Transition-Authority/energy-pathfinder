package uk.co.ogauthority.pathfinder.repository.portal;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.portal.LicenceBlock;
import uk.co.ogauthority.pathfinder.model.enums.portal.BlockLocation;

@Repository
public interface CurrentLicenceBlocksRepository extends CrudRepository<LicenceBlock, String> {

  List<LicenceBlock> findAllByBlockLocationAndBlockReferenceContainingIgnoreCase(BlockLocation blockLocation, String searchTerm);

  List<LicenceBlock> findAllByCompositeKeyIn(List<String> ids);
}
