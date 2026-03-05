package uk.co.ogauthority.pathfinder.service.project.location;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.ogauthority.pathfinder.service.portal.LicenceBlocksService;

@Service
public class LicenceBlockValidatorService {
  public static final String BLOCK_NOT_FOUND = "A licence block is no longer valid";

  private final LicenceBlocksService licenceBlocksService;

  @Autowired
  public LicenceBlockValidatorService(LicenceBlocksService licenceBlocksService) {
    this.licenceBlocksService = licenceBlocksService;
  }

  /**
   * Add an error to the provided fieldId if any of the blocks linked to it do not exist.
   * @param compositeKeys list of licenceBlock composite keys
   * @param errors errors to update
   * @param fieldId id of field to add the error to
   */
  public void addErrorsForInvalidBlocks(List<String> compositeKeys, Errors errors, String fieldId) {
    var validLicenceBlocks = licenceBlocksService.getValidLicenceBlockCompositeKeys(compositeKeys);

    for (var compositeKey : compositeKeys) {
      if (!validLicenceBlocks.contains(compositeKey)) {
        errors.rejectValue(fieldId, fieldId + ".notPresent", BLOCK_NOT_FOUND);
        return;
      }
    }
  }
}
