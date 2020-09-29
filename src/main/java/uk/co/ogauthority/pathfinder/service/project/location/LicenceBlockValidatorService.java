package uk.co.ogauthority.pathfinder.service.project.location;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.ogauthority.pathfinder.service.portal.LicenceBlocksService;

@Service
public class LicenceBlockValidatorService {
  public static final String BLOCK_NOT_FOUND = "This block is no longer valid";

  private final LicenceBlocksService licenceBlocksService;

  @Autowired
  public LicenceBlockValidatorService(LicenceBlocksService licenceBlocksService) {
    this.licenceBlocksService = licenceBlocksService;
  }


  /**
   * Return true if the block composite id matches current offshore licence block data.
   * @param compositeKey key to search for
   * @return true if the block composite id matches current offshore licence block data.
   */
  public boolean existsInPortalData(String compositeKey) {
    return licenceBlocksService.blockExists(compositeKey);
  }

  /**
   * Add an error to the provided fieldId if the block linked to it does not exist.
   * @param licenceBlocks list of licenceBlock composite keys
   * @param errors errors to update
   * @param fieldId id of field to add the error to
   */
  public void addErrorsForInvalidBlocks(List<String> licenceBlocks, Errors errors, String fieldId) {
    licenceBlocks.forEach(ck -> {
      if (!existsInPortalData(ck)) {
        errors.rejectValue(fieldId, fieldId + ".notPresent", BLOCK_NOT_FOUND);
      }
    });
  }
}
