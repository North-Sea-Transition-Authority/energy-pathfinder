package uk.co.ogauthority.pathfinder.service.project.location;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.model.entity.portal.LicenceBlock;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationForm;
import uk.co.ogauthority.pathfinder.model.view.location.ProjectLocationBlockView;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationBlockRepository;
import uk.co.ogauthority.pathfinder.service.portal.LicenceBlocksService;

@Service
public class ProjectLocationBlocksService {

  private final LicenceBlocksService licenceBlocksService;
  private final ProjectLocationBlockRepository projectLocationBlockRepository;

  @Autowired
  public ProjectLocationBlocksService(
      LicenceBlocksService licenceBlocksService,
      ProjectLocationBlockRepository projectLocationBlockRepository
  ) {
    this.licenceBlocksService = licenceBlocksService;
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
                  b.getBlockSuffix(),
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
    var existingBlocks = getBlocks(projectLocation);
    form.setLicenceBlocks(existingBlocks.stream().map(ProjectLocationBlock::getCompositeKey).collect(Collectors.toList()));
    return form;
  }

  public List<ProjectLocationBlockView> getBlockViewsFromForm(ProjectLocationForm form, ValidationType validationType) {
    var licenceBlocks = licenceBlocksService.findAllByCompositeKeyIn(form.getLicenceBlocks());
    var compositeKeys = licenceBlocks.stream().map(LicenceBlock::getCompositeKey).toList();
    var compositeKeysWhichExistInPortalData = getLicenceBlockCompositeKeysThatExistInPortalData(compositeKeys, validationType);

    return licenceBlocks.stream()
        .map(licenceBlock -> new ProjectLocationBlockView(
            licenceBlock,
            compositeKeysWhichExistInPortalData.contains(licenceBlock.getCompositeKey())
        ))
        .collect(Collectors.toList());
  }

  public List<ProjectLocationBlockView> getBlockViewsByProjectLocationAndCompositeKeyIn(
      ProjectLocation location,
      List<String> compositeKeys,
      ValidationType validationType
  ) {
    var projectLocationBlocks = projectLocationBlockRepository.findAllByProjectLocation(location);
    var projectLocationBlockCompositeKeys = projectLocationBlocks.stream().map(ProjectLocationBlock::getCompositeKey).toList();
    var compositeKeysWhichExistInPortalData =
        getLicenceBlockCompositeKeysThatExistInPortalData(projectLocationBlockCompositeKeys, validationType);

    return projectLocationBlocks.stream()
        .filter(plb -> compositeKeys.contains(plb.getCompositeKey()))
        .map(projectLocationBlock -> new ProjectLocationBlockView(
            projectLocationBlock,
            compositeKeysWhichExistInPortalData.contains(projectLocationBlock.getCompositeKey())
        ))
        .collect(Collectors.toList());
  }


  public List<ProjectLocationBlockView> getBlockViewsForLocation(ProjectLocation projectLocation, ValidationType validationType) {
    var projectLocationBlocks = projectLocationBlockRepository.findAllByProjectLocation(projectLocation);
    var projectLocationCompositeKeys = projectLocationBlocks.stream().map(ProjectLocationBlock::getCompositeKey).toList();
    var compositeKeysWhichExistInPortalData =
        getLicenceBlockCompositeKeysThatExistInPortalData(projectLocationCompositeKeys, validationType);

    return projectLocationBlocks.stream()
        .sorted(Comparator.comparing(ProjectLocationBlock::getSortKey))
        .map(projectLocationBlock -> new ProjectLocationBlockView(
             projectLocationBlock,
            compositeKeysWhichExistInPortalData.contains(projectLocationBlock.getCompositeKey())
        ))
        .collect(Collectors.toList());
  }

  public List<ProjectLocationBlock> getBlocks(ProjectLocation projectLocation) {
    return projectLocationBlockRepository.findAllByProjectLocation(projectLocation)
        .stream()
        .sorted(Comparator.comparing(ProjectLocationBlock::getSortKey))
        .collect(Collectors.toList());
  }

  public void deleteBlocks(ProjectLocation projectLocation) {
    projectLocationBlockRepository.deleteAllByProjectLocation(projectLocation);
  }

  public Set<String> getLicenceBlockCompositeKeysThatExistInPortalData(
      Collection<String> compositeKeys,
      ValidationType validationType
  ) {
    if (validationType == ValidationType.FULL) {
      return licenceBlocksService.getValidLicenceBlockCompositeKeys(compositeKeys);
    }

    return new HashSet<>(compositeKeys);
  }

}
