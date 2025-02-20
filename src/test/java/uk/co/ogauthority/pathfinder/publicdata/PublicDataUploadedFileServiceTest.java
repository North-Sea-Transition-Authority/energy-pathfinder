package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.repository.file.ProjectDetailFileRepository;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.testutil.ProjectFileTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;

@ExtendWith(MockitoExtension.class)
class PublicDataUploadedFileServiceTest {

  @Mock
  private ProjectDetailsRepository projectDetailsRepository;

  @Mock
  private ProjectDetailFileRepository projectDetailFileRepository;

  @InjectMocks
  private PublicDataUploadedFileService publicDataUploadedFileService;

  @Test
  void getUploadedFilesForPublishedProjects() {
    var publishedProjectDetail1 = ProjectUtil.getProjectDetails();
    publishedProjectDetail1.setId(1);

    var publishedProjectDetail2 = ProjectUtil.getProjectDetails();
    publishedProjectDetail2.setId(2);

    var unpublishedProjectDetail = ProjectUtil.getProjectDetails();
    unpublishedProjectDetail.setId(3);

    var projectDetailFile1 = ProjectFileTestUtil.getProjectDetailFile(publishedProjectDetail1);
    projectDetailFile1.setUploadedFile(UploadedFileUtil.createUploadedFile("file-1"));

    var projectDetailFile2 = ProjectFileTestUtil.getProjectDetailFile(publishedProjectDetail2);
    projectDetailFile2.setUploadedFile(UploadedFileUtil.createUploadedFile("file-2"));

    var projectDetailFile3 = ProjectFileTestUtil.getProjectDetailFile(publishedProjectDetail2);
    projectDetailFile3.setUploadedFile(UploadedFileUtil.createUploadedFile("file-3"));

    var projectDetailFile4 = ProjectFileTestUtil.getProjectDetailFile(unpublishedProjectDetail);
    projectDetailFile4.setUploadedFile(UploadedFileUtil.createUploadedFile("file-4"));

    when(projectDetailsRepository.getAllPublishedProjectDetailsByProjectTypes(EnumSet.allOf(ProjectType.class)))
        .thenReturn(List.of(publishedProjectDetail1, publishedProjectDetail2));

    when(projectDetailFileRepository.findAll())
        .thenReturn(List.of(projectDetailFile1, projectDetailFile2, projectDetailFile3, projectDetailFile4));

    assertThat(publicDataUploadedFileService.getUploadedFilesForPublishedProjects()).containsExactlyInAnyOrder(
        projectDetailFile1.getUploadedFile(),
        projectDetailFile2.getUploadedFile(),
        projectDetailFile3.getUploadedFile()
    );
  }
}
