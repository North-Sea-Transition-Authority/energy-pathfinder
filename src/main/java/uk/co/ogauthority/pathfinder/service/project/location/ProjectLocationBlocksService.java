package uk.co.ogauthority.pathfinder.service.project.location;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationForm;
import uk.co.ogauthority.pathfinder.model.view.projectlocation.ProjectLocationBlockView;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationBlockRepository;
import uk.co.ogauthority.pathfinder.service.portal.LicenceBlocksService;

@Service
public class ProjectLocationBlocksService {

  private final LicenceBlocksService licenceBlocksService;
  private final LicenceBlockValidatorService licenceBlockValidatorService;
  private final ProjectLocationBlockRepository projectLocationBlockRepository;

  @Autowired
  public ProjectLocationBlocksService(LicenceBlocksService licenceBlocksService,
                                      LicenceBlockValidatorService licenceBlockValidatorService,
                                      ProjectLocationBlockRepository projectLocationBlockRepository
  ) {
    this.licenceBlocksService = licenceBlocksService;
    this.licenceBlockValidatorService = licenceBlockValidatorService;
    this.projectLocationBlockRepository = projectLocationBlockRepository;
  }

  /**
   * Create ProjectLocationBlocks for the given ProjectLocation.
   * Remove any blocks linked to this projectLocation not present in the list.
   * Check if any have already been added, add any which aren't already present.
   * @param licenceBlockIds List of licence block ids
   * @param projectLocation Project Location to create the linked blocks for
   */
  @Transactional
  public void createOrUpdateBlocks(List<String> licenceBlockIds, ProjectLocation projectLocation) {
    //get blocks from ids
    var existingBlocks = projectLocationBlockRepository.findAllByProjectLocation(projectLocation);

    //Get blocks to remove - any no longer in list
    var blocksToRemove = existingBlocks.stream().filter(
        plb -> !licenceBlockIds.contains(plb.getCompositeKey())).collect(Collectors.toList());

    //Get blocks to add - Only the ones that don't exist in list already
    var alreadyAddedIds = licenceBlockIds.stream()
        .filter(lbi -> existingBlocks.stream().anyMatch(plb -> plb.getCompositeKey().equals(lbi)))
        .collect(Collectors.toList());
    //remove the already added ones from the list of ids
    licenceBlockIds.removeAll(alreadyAddedIds);

    var blocksToAdd = licenceBlocksService.findAllByCompositeKeyIn(licenceBlockIds);

    //delete any that no longer exist
    if (!blocksToRemove.isEmpty()) {
      projectLocationBlockRepository.deleteAll(blocksToRemove);
    }

    if (!blocksToAdd.isEmpty()) {
      //Create blocks for the newly added ones
      projectLocationBlockRepository.saveAll(
          blocksToAdd.stream().map(b ->
              new ProjectLocationBlock(
                  projectLocation,
                  b.getPedLicenceId(),
                  b.getBlockReference(),
                  b.getBlockNumber(),
                  b.getQuadrantNumber(),
                  b.getSuffix(),
                  b.getBlockLocation()
              ))
              .collect(Collectors.toList())
      );
    }
  }


  /**
   * Add the licence blocks associated with this projectLocation to the form.
   * @param form form to populate with licenceBlock ids
   * @param projectLocation location to get blocks for
   */
  public ProjectLocationForm addBlocksToForm(ProjectLocationForm form, ProjectLocation projectLocation) {
    var existingBlocks = projectLocationBlockRepository.findAllByProjectLocation(projectLocation);
    form.setLicenceBlocks(existingBlocks.stream().map(ProjectLocationBlock::getCompositeKey)
        .collect(Collectors.toList()));
    return form;
  }

  public List<ProjectLocationBlockView> getBlockViewsFromForm(ProjectLocationForm form, ValidationType validationType) {
    return licenceBlocksService.findAllByCompositeKeyInOrdered(form.getLicenceBlocks()).stream()
        .map(plb -> new ProjectLocationBlockView(
             plb,
             isBlockReferenceValid(plb.getCompositeKey(), validationType)
        )).collect(Collectors.toList());
  }

  public List<ProjectLocationBlockView> getBlockViewsByProjectLocationAndCompositeKeyIn(ProjectLocation location,
                                                                                        List<String> compositeKeys,
                                                                                        ValidationType validationType) {
    return projectLocationBlockRepository.findAllByProjectLocation(location).stream()
        .filter(plb -> compositeKeys.contains(plb.getCompositeKey()))
        .map(plb -> new ProjectLocationBlockView(
            plb,
            isBlockReferenceValid(plb.getCompositeKey(), validationType)
        )).collect(Collectors.toList());
  }


  public List<ProjectLocationBlockView> getBlockViewsForLocation(ProjectLocation projectLocation, ValidationType validationType) {
    return projectLocationBlockRepository.findAllByProjectLocationOrderByBlockReference(projectLocation).stream()
        .map(plb -> new ProjectLocationBlockView(
             plb,
             isBlockReferenceValid(plb.getCompositeKey(), validationType)
        )).collect(Collectors.toList());
  }

  public List<ProjectLocationBlock> getBlocks(ProjectLocation projectLocation) {
    return projectLocationBlockRepository.findAllByProjectLocationOrderByBlockReference(projectLocation);
  }

  /**
   * Validate the licence block exists in the portal if the validation type is FULL.
   * @param compositeKey key to search for
   * @param validationType validation type - FULL if validating
   * @return true if the validationType is FULL and the block exists in the portal data.
   *       True if validationType is not FULL. False otherwise.
   */
  public boolean isBlockReferenceValid(String compositeKey, ValidationType validationType) {
    return !validationType.equals(ValidationType.FULL) || licenceBlockValidatorService.existsInPortalData(compositeKey);
  }
}
