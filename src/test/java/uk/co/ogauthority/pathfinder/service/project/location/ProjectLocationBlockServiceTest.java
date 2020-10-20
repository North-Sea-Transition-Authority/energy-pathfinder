package uk.co.ogauthority.pathfinder.service.project.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.portal.LicenceBlock;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationForm;
import uk.co.ogauthority.pathfinder.model.view.projectlocation.ProjectLocationBlockView;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationBlockRepository;
import uk.co.ogauthority.pathfinder.service.portal.LicenceBlocksService;
import uk.co.ogauthority.pathfinder.testutil.LicenceBlockTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectLocationBlockServiceTest {
  private static final String BLOCK_REF_1 = "12/34a";
  private static final String BLOCK_REF_2 = "12/34b";
  private static final String BLOCK_REF_3 = "34/56";
  private static final List<String> BLOCK_REFS = new ArrayList<>(Arrays.asList(BLOCK_REF_1, BLOCK_REF_2, BLOCK_REF_3));
  private static final List<LicenceBlock> BLOCKS  = List.of(
      LicenceBlockTestUtil.getBlock(BLOCK_REF_1),
      LicenceBlockTestUtil.getBlock(BLOCK_REF_2),
      LicenceBlockTestUtil.getBlock(BLOCK_REF_3)
  );

  @Mock
  private LicenceBlocksService licenceBlocksService;

  @Mock
  private LicenceBlockValidatorService licenceBlockValidatorService;

  @Mock
  private ProjectLocationBlockRepository projectLocationBlockRepository;

  private ProjectLocationBlocksService projectLocationBlocksService;

  @Captor
  private ArgumentCaptor<List<ProjectLocationBlock>> projectLocationBlockRepositoryArgumentCaptor;

  private static final ProjectDetail DETAIL = ProjectUtil.getProjectDetails();

  private static final ProjectLocation PROJECT_LOCATION = ProjectLocationTestUtil.getProjectLocation_withField(DETAIL);

  public static final List<ProjectLocationBlock> PROJECT_LOCATION_BLOCKS = List.of(
      LicenceBlockTestUtil.getProjectLocationBlock(PROJECT_LOCATION, BLOCK_REF_1),
      LicenceBlockTestUtil.getProjectLocationBlock(PROJECT_LOCATION, BLOCK_REF_2),
      LicenceBlockTestUtil.getProjectLocationBlock(PROJECT_LOCATION, BLOCK_REF_3)
  );

  @Before
  public void setUp() throws Exception {
    projectLocationBlocksService = new ProjectLocationBlocksService(
        licenceBlocksService,
        licenceBlockValidatorService,
        projectLocationBlockRepository
    );
  }

  @Test
  public void createOrUpdateBlocks_noneAlreadyExist() {
    when(projectLocationBlockRepository.findAllByProjectLocation(PROJECT_LOCATION)).thenReturn(Collections.emptyList());
    when(licenceBlocksService.findAllByCompositeKeyIn(BLOCK_REFS)).thenReturn(BLOCKS);
    projectLocationBlocksService.createOrUpdateBlocks(BLOCK_REFS, PROJECT_LOCATION);
    verify(projectLocationBlockRepository, times(0)).deleteAll(any());
    verify(projectLocationBlockRepository, times(1)).saveAll(any());
  }

  @Test
  public void createOrUpdateBlocks_oneRemoved() {
    var licenceBlockIds = new ArrayList<>(Arrays.asList(BLOCKS.get(0).getCompositeKey(), BLOCKS.get(1).getCompositeKey()));
    when(projectLocationBlockRepository.findAllByProjectLocation(PROJECT_LOCATION)).thenReturn(
        PROJECT_LOCATION_BLOCKS
    );
    projectLocationBlocksService.createOrUpdateBlocks(licenceBlockIds, PROJECT_LOCATION);
    verify(projectLocationBlockRepository, times(1)).deleteAll(any());
    verify(projectLocationBlockRepository, times(0)).saveAll(any());
  }

  @Test
  public void createOrUpdateBlocks_allExist() {
    var licenceBlockIds = new ArrayList<>(Arrays.asList(
        BLOCKS.get(0).getCompositeKey(),
        BLOCKS.get(1).getCompositeKey(),
        BLOCKS.get(2).getCompositeKey())
    );
    when(licenceBlocksService.findAllByCompositeKeyIn(any())).thenReturn(BLOCKS);
    when(projectLocationBlockRepository.findAllByProjectLocation(PROJECT_LOCATION)).thenReturn(
        PROJECT_LOCATION_BLOCKS
    );
    projectLocationBlocksService.createOrUpdateBlocks(licenceBlockIds, PROJECT_LOCATION);
    verify(projectLocationBlockRepository, times(0)).deleteAll(any());
    verify(projectLocationBlockRepository, times(1)).saveAll(any());
  }

  @Test
  public void createOrUpdateBlocks_verifySave() {
    when(projectLocationBlockRepository.findAllByProjectLocation(PROJECT_LOCATION)).thenReturn(Collections.emptyList());
    var block = LicenceBlockTestUtil.getBlock(BLOCK_REF_1);
    when(licenceBlocksService.findAllByCompositeKeyIn(BLOCK_REFS)).thenReturn(
        Collections.singletonList(block)
    );
    projectLocationBlocksService.createOrUpdateBlocks(BLOCK_REFS, PROJECT_LOCATION);
    verify(projectLocationBlockRepository, times(0)).deleteAll(any());
    verify(projectLocationBlockRepository, times(1)).saveAll(projectLocationBlockRepositoryArgumentCaptor.capture());
    var projectLocationBlocks = projectLocationBlockRepositoryArgumentCaptor.getValue();
    assertThat(projectLocationBlocks.size()).isEqualTo(1);
    var projectLocationBlock = projectLocationBlocks.get(0);
    assertThat(projectLocationBlock.getBlockReference()).isEqualTo(block.getBlockReference());
    assertThat(projectLocationBlock.getCompositeKey()).isEqualTo(block.getCompositeKey());
    assertThat(projectLocationBlock.getProjectLocation()).isEqualTo(PROJECT_LOCATION);
    assertThat(projectLocationBlock.getBlockNumber()).isEqualTo(block.getBlockNumber());
    assertThat(projectLocationBlock.getQuadrantNumber()).isEqualTo(block.getQuadrantNumber());
    assertThat(projectLocationBlock.getBlockSuffix()).isEqualTo(block.getSuffix());
    assertThat(projectLocationBlock.getBlockLocation()).isEqualTo(block.getBlockLocation());
  }

  @Test
  public void createOrUpdateBlocks_verifyDelete() {
    var projectLocationBlocks = List.of(
        LicenceBlockTestUtil.getProjectLocationBlock(PROJECT_LOCATION, BLOCK_REF_1)
    );
    var block = LicenceBlockTestUtil.getBlock(BLOCK_REF_1);

    when(projectLocationBlockRepository.findAllByProjectLocation(PROJECT_LOCATION)).thenReturn(
        projectLocationBlocks
    );

    projectLocationBlocksService.createOrUpdateBlocks(BLOCK_REFS, PROJECT_LOCATION);

    verify(projectLocationBlockRepository, times(1)).deleteAll(projectLocationBlockRepositoryArgumentCaptor.capture());
    verify(projectLocationBlockRepository, times(0)).saveAll(any());
    var deletedBlocks = projectLocationBlockRepositoryArgumentCaptor.getValue();
    assertThat(deletedBlocks.size()).isEqualTo(1);

    var projectLocationBlock = projectLocationBlocks.get(0);
    assertThat(projectLocationBlock.getBlockReference()).isEqualTo(block.getBlockReference());
    assertThat(projectLocationBlock.getCompositeKey()).isEqualTo(block.getCompositeKey());
    assertThat(projectLocationBlock.getProjectLocation()).isEqualTo(PROJECT_LOCATION);
    assertThat(projectLocationBlock.getBlockNumber()).isEqualTo(block.getBlockNumber());
    assertThat(projectLocationBlock.getQuadrantNumber()).isEqualTo(block.getQuadrantNumber());
    assertThat(projectLocationBlock.getBlockSuffix()).isEqualTo(block.getSuffix());
    assertThat(projectLocationBlock.getBlockLocation()).isEqualTo(block.getBlockLocation());
  }

  @Test
  public void addBlocksToForm() {
    var form = new ProjectLocationForm();
    when(projectLocationBlockRepository.findAllByProjectLocation(PROJECT_LOCATION)).thenReturn(
        PROJECT_LOCATION_BLOCKS
    );

    projectLocationBlocksService.addBlocksToForm(form, PROJECT_LOCATION);

    assertThat(form.getLicenceBlocks().size()).isEqualTo(3);
    assertThat(form.getLicenceBlocks()).containsExactlyInAnyOrder(
        PROJECT_LOCATION_BLOCKS.get(0).getCompositeKey(),
        PROJECT_LOCATION_BLOCKS.get(1).getCompositeKey(),
        PROJECT_LOCATION_BLOCKS.get(2).getCompositeKey()
    );
  }

  @Test
  public void addBlocksToForm_noBlocksOnProject() {
    var form = new ProjectLocationForm();
    when(projectLocationBlockRepository.findAllByProjectLocation(PROJECT_LOCATION)).thenReturn(
        Collections.emptyList()
    );
    projectLocationBlocksService.addBlocksToForm(form, PROJECT_LOCATION);
    assertThat(form.getLicenceBlocks()).isEmpty();
  }


  @Test
  public void getBlockViewsFromForm_noValidation_allValid() {
    when(licenceBlocksService.findAllByCompositeKeyInOrdered(any())).thenReturn(
        BLOCKS
    );
    var form = new ProjectLocationForm();
    var blockViews = projectLocationBlocksService.getBlockViewsFromForm(form, ValidationType.NO_VALIDATION);
    assertThat(blockViews.size()).isEqualTo(3);
    assertBlockViewMatchesBlock(blockViews.get(0), BLOCKS.get(0), true);
    assertBlockViewMatchesBlock(blockViews.get(1), BLOCKS.get(1), true);
    assertBlockViewMatchesBlock(blockViews.get(2), BLOCKS.get(2), true);
  }

  @Test
  public void getBlockViewsFromForm_withValidation_CorrectlyValid() {
    when(licenceBlocksService.findAllByCompositeKeyInOrdered(any())).thenReturn(
        BLOCKS
    );
    when(licenceBlockValidatorService.existsInPortalData(BLOCKS.get(0).getCompositeKey())).thenReturn(false);
    when(licenceBlockValidatorService.existsInPortalData(BLOCKS.get(1).getCompositeKey())).thenReturn(true);
    when(licenceBlockValidatorService.existsInPortalData(BLOCKS.get(2).getCompositeKey())).thenReturn(false);
    var form = new ProjectLocationForm();
    var blockViews = projectLocationBlocksService.getBlockViewsFromForm(form, ValidationType.FULL);
    assertThat(blockViews.size()).isEqualTo(3);
    assertBlockViewMatchesBlock(blockViews.get(0), BLOCKS.get(0), false);
    assertBlockViewMatchesBlock(blockViews.get(1), BLOCKS.get(1), true);
    assertBlockViewMatchesBlock(blockViews.get(2), BLOCKS.get(2), false);
  }

  @Test
  public void getBlockViewsForLocation_noValidation_allValid() {
    when(projectLocationBlockRepository.findAllByProjectLocationOrderByBlockReference(any())).thenReturn(
        PROJECT_LOCATION_BLOCKS
    );
    var form = new ProjectLocationForm();
    var blockViews = projectLocationBlocksService.getBlockViewsForLocation(PROJECT_LOCATION, ValidationType.NO_VALIDATION);
    assertThat(blockViews.size()).isEqualTo(3);
    assertBlockViewMatchesProjectLocationBlock(blockViews.get(0), PROJECT_LOCATION_BLOCKS.get(0), true);
    assertBlockViewMatchesProjectLocationBlock(blockViews.get(1), PROJECT_LOCATION_BLOCKS.get(1), true);
    assertBlockViewMatchesProjectLocationBlock(blockViews.get(2), PROJECT_LOCATION_BLOCKS.get(2), true);
  }

  @Test
  public void getBlockViewsForLocation_withValidation_CorrectlyValid() {
    when(projectLocationBlockRepository.findAllByProjectLocationOrderByBlockReference(any())).thenReturn(
        PROJECT_LOCATION_BLOCKS
    );
    when(licenceBlockValidatorService.existsInPortalData(BLOCKS.get(0).getCompositeKey())).thenReturn(false);
    when(licenceBlockValidatorService.existsInPortalData(BLOCKS.get(1).getCompositeKey())).thenReturn(true);
    when(licenceBlockValidatorService.existsInPortalData(BLOCKS.get(2).getCompositeKey())).thenReturn(false);
    var form = new ProjectLocationForm();
    var blockViews = projectLocationBlocksService.getBlockViewsForLocation(PROJECT_LOCATION, ValidationType.FULL);
    assertThat(blockViews.size()).isEqualTo(3);
    assertBlockViewMatchesProjectLocationBlock(blockViews.get(0), PROJECT_LOCATION_BLOCKS.get(0), false);
    assertBlockViewMatchesProjectLocationBlock(blockViews.get(1), PROJECT_LOCATION_BLOCKS.get(1), true);
    assertBlockViewMatchesProjectLocationBlock(blockViews.get(2), PROJECT_LOCATION_BLOCKS.get(2), false);
  }

  @Test
  public void getBlockViewsByProjectLocationAndCompositeKeyIn_allFound() {
    when(projectLocationBlockRepository.findAllByProjectLocation(PROJECT_LOCATION)).thenReturn(
        PROJECT_LOCATION_BLOCKS
    );
    when(licenceBlockValidatorService.existsInPortalData(BLOCKS.get(0).getCompositeKey())).thenReturn(true);
    when(licenceBlockValidatorService.existsInPortalData(BLOCKS.get(1).getCompositeKey())).thenReturn(true);
    when(licenceBlockValidatorService.existsInPortalData(BLOCKS.get(2).getCompositeKey())).thenReturn(false);
    var form = new ProjectLocationForm();
    var blockViews = projectLocationBlocksService.getBlockViewsByProjectLocationAndCompositeKeyIn(
        PROJECT_LOCATION,
        PROJECT_LOCATION_BLOCKS.stream().map(ProjectLocationBlock::getCompositeKey).collect(Collectors.toList()),
        ValidationType.FULL
    );
    assertThat(blockViews.size()).isEqualTo(3);
    assertBlockViewMatchesBlock(blockViews.get(0), BLOCKS.get(0), true);
    assertBlockViewMatchesBlock(blockViews.get(1), BLOCKS.get(1), true);
    assertBlockViewMatchesBlock(blockViews.get(2), BLOCKS.get(2), false);
  }

  @Test
  public void getBlockViewsByProjectLocationAndCompositeKeyIn_oneNotFound() {
    when(projectLocationBlockRepository.findAllByProjectLocation(PROJECT_LOCATION)).thenReturn(
        PROJECT_LOCATION_BLOCKS
    );
    when(licenceBlockValidatorService.existsInPortalData(BLOCKS.get(0).getCompositeKey())).thenReturn(true);
    when(licenceBlockValidatorService.existsInPortalData(BLOCKS.get(2).getCompositeKey())).thenReturn(false);
    var blockViews = projectLocationBlocksService.getBlockViewsByProjectLocationAndCompositeKeyIn(
        PROJECT_LOCATION,
        List.of(PROJECT_LOCATION_BLOCKS.get(0).getCompositeKey(), PROJECT_LOCATION_BLOCKS.get(2).getCompositeKey()),
        ValidationType.FULL
    );
    assertThat(blockViews.size()).isEqualTo(2);
    assertBlockViewMatchesBlock(blockViews.get(0), BLOCKS.get(0), true);
    assertBlockViewMatchesBlock(blockViews.get(1), BLOCKS.get(2), false);
  }

  @Test
  public void isBlockReferenceValid_fullValidation_existsInPortalData() {
    when(licenceBlockValidatorService.existsInPortalData(anyString())).thenReturn(true);
    assertThat(projectLocationBlocksService.isBlockReferenceValid(BLOCK_REF_1, ValidationType.FULL)).isTrue();
  }

  @Test
  public void isBlockReferenceValid_fullValidation_doesNotExistInPortalData() {
    when(licenceBlockValidatorService.existsInPortalData(anyString())).thenReturn(true);
    assertThat(projectLocationBlocksService.isBlockReferenceValid(BLOCK_REF_1, ValidationType.FULL)).isTrue();
  }

  private void assertBlockViewMatchesProjectLocationBlock(ProjectLocationBlockView view, ProjectLocationBlock block, Boolean isValidExpectation) {
    assertThat(view.getBlockReference()).isEqualTo(block.getBlockReference());
    assertThat(view.getCompositeKey()).isEqualTo(block.getCompositeKey());
    assertThat(view.isValid()).isEqualTo(isValidExpectation);
  }

  private void assertBlockViewMatchesBlock(ProjectLocationBlockView view, LicenceBlock block, Boolean isValidExpectation) {
    assertThat(view.getBlockReference()).isEqualTo(block.getBlockReference());
    assertThat(view.getCompositeKey()).isEqualTo(block.getCompositeKey());
    assertThat(view.isValid()).isEqualTo(isValidExpectation);
  }
}
