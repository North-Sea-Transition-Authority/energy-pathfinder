package uk.co.ogauthority.pathfinder.service.project.location;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationBlockRepository;
import uk.co.ogauthority.pathfinder.service.portal.LicenceBlocksService;
import uk.co.ogauthority.pathfinder.testutil.LicenceBlockTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectLocationBlockServiceTest {
  public static final String BLOCK_REF_1 = "12/34a";
  public static final String BLOCK_REF_2 = "12/34b";
  public static final String BLOCK_REF_3 = "34/56";
  public static final List<String> BLOCK_REFS = new ArrayList<>(Arrays.asList(BLOCK_REF_1, BLOCK_REF_2, BLOCK_REF_3));
  public static final List<LicenceBlock> BLOCKS  = List.of(
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
  private ArgumentCaptor<ProjectLocationBlock> projectLocationBlockRepositoryArgumentCaptor;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private ProjectLocation projectLocation = ProjectLocationUtil.getProjectLocation_withField(detail);

  @Before
  public void setUp() throws Exception {
    projectLocationBlocksService = new ProjectLocationBlocksService(
        licenceBlocksService,
        licenceBlockValidatorService,
        projectLocationBlockRepository
    );

//    when(projectLocationBlockRepository.save(any(ProjectLocationBlock.class)))
//        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createOrUpdateBlocks_noneAlreadyExist() {
    when(projectLocationBlockRepository.findAllByProjectLocation(projectLocation)).thenReturn(Collections.emptyList());
    when(licenceBlocksService.findAllByCompositeKeyIn(BLOCK_REFS)).thenReturn(BLOCKS);
    projectLocationBlocksService.createOrUpdateBlocks(BLOCK_REFS, projectLocation);
    verify(projectLocationBlockRepository, times(0)).deleteAll(any());
    verify(projectLocationBlockRepository, times(1)).saveAll(any());
  }

  @Test
  public void createOrUpdateBlocks_oneRemoved() {
    var projectLocationBlocks = List.of(
       LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, BLOCK_REF_1),
       LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, BLOCK_REF_2),
       LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, BLOCK_REF_3)
    );
    var licenceBlockIds = new ArrayList<>(Arrays.asList(BLOCKS.get(0).getCompositeKey(), BLOCKS.get(1).getCompositeKey()));
    when(projectLocationBlockRepository.findAllByProjectLocation(projectLocation)).thenReturn(
       projectLocationBlocks
    );
    projectLocationBlocksService.createOrUpdateBlocks(licenceBlockIds, projectLocation);
    verify(projectLocationBlockRepository, times(1)).deleteAll(any());
    verify(projectLocationBlockRepository, times(0)).saveAll(any());
  }

  @Test
  public void createOrUpdateBlocks_allExist() {
    var projectLocationBlocks = List.of(
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, BLOCK_REF_1),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, BLOCK_REF_2),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, BLOCK_REF_3)
    );
    var licenceBlockIds = new ArrayList<>(Arrays.asList(
        BLOCKS.get(0).getCompositeKey(),
        BLOCKS.get(1).getCompositeKey(),
        BLOCKS.get(2).getCompositeKey())
    );
    when(licenceBlocksService.findAllByCompositeKeyIn(any())).thenReturn(BLOCKS);
    when(projectLocationBlockRepository.findAllByProjectLocation(projectLocation)).thenReturn(
        projectLocationBlocks
    );
    projectLocationBlocksService.createOrUpdateBlocks(licenceBlockIds, projectLocation);
    verify(projectLocationBlockRepository, times(0)).deleteAll(any());
    verify(projectLocationBlockRepository, times(1)).saveAll(any());
  }

//TODO PAT-220  Now that saveAll is being used this will need to be updated to account for that.
//  @Test
//  public void createOrUpdateBlocks_verifySave() {
//    when(projectLocationBlockRepository.findAllByProjectLocation(projectLocation)).thenReturn(Collections.emptyList());
//    var block = LicenceBlockUtil.getBlock(BLOCK_REF_1);
//    when(licenceBlocksService.findAllByCompositeKeyIn(BLOCK_REFS)).thenReturn(
//        Collections.singletonList(block)
//    );
//    projectLocationBlocksService.createOrUpdateBlocks(BLOCK_REFS, projectLocation);
//    verify(projectLocationBlockRepository, times(0)).delete(any());
//    verify(projectLocationBlockRepository, times(1)).save(projectLocationBlockRepositoryArgumentCaptor.capture());
//    var projectLocationBlock = projectLocationBlockRepositoryArgumentCaptor.getValue();
//    assertThat(projectLocationBlock.getBlockReference()).isEqualTo(block.getBlockReference());
//    assertThat(projectLocationBlock.getCompositeKey()).isEqualTo(block.getCompositeKey());
//    assertThat(projectLocationBlock.getProjectLocation()).isEqualTo(projectLocation);
//    assertThat(projectLocationBlock.getBlockNumber()).isEqualTo(block.getBlockNumber());
//    assertThat(projectLocationBlock.getQuadrantNumber()).isEqualTo(block.getQuadrantNumber());
//    assertThat(projectLocationBlock.getBlockSuffix()).isEqualTo(block.getSuffix());
//    assertThat(projectLocationBlock.getBlockLocation()).isEqualTo(block.getBlockLocation());
//  }

  @Test
  public void addBlocksToForm() {

  }
}
