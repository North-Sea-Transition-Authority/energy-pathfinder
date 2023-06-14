package uk.co.ogauthority.pathfinder.service.difference;

import static org.junit.Assert.*;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.ext.beans.HashAdapter;
import freemarker.ext.beans.MapModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.Version;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import uk.co.ogauthority.pathfinder.model.view.ProjectSummaryItem;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.model.view.integratedrig.IntegratedRigView;

public class DifferenceFreemarkerServiceTest {

  private DifferenceFreemarkerService differenceFreemarkerService;
  private DifferenceService differenceService;

  @Before
  public void setUp() throws Exception {
    differenceFreemarkerService = new DifferenceFreemarkerService();
    differenceService = new DifferenceService();
  }

  @Test
  public void isDiffableFieldIgnored() {
    var allFields = IntegratedRigView.class.getFields();
    var ignoredFields = ProjectSummaryItem.class.getDeclaredFields();
    var expectedFieldsAreIgnored = Arrays.stream(allFields)
        .allMatch(field -> {
          var isIgnored = Arrays.stream(ignoredFields).anyMatch(ignored -> ignored.getName().equals(field.getName()));
          return differenceFreemarkerService.isDiffableFieldIgnored(field.getName()) == isIgnored;
        });

    assertTrue(expectedFieldsAreIgnored);
  }

  @Test
  public void areAllFieldsDeleted_deletedFields() throws NoSuchFieldException, IllegalAccessException {
    var originalView = new IntegratedRigView();
    originalView.setName("name");
    originalView.setStatus("status");
    originalView.setStructure(new StringWithTag("tag"));
    originalView.setIntentionToReactivate("intention");
    originalView.setSummaryLinks(List.of());

    var deletedView = new IntegratedRigView();
    deletedView.setSummaryLinks(List.of());

    var diffResult = differenceService.differentiate(deletedView, originalView);

    var areAllFieldsDeleted = differenceFreemarkerService.areAllFieldsDeleted(diffResult);

    assertTrue(areAllFieldsDeleted);
  }

  @Test
  public void areAllFieldsDeleted_blankFields() throws NoSuchFieldException, IllegalAccessException {
    var originalView = new IntegratedRigView();
    originalView.setName("");
    originalView.setStatus("status");
    originalView.setStructure(new StringWithTag("tag"));
    originalView.setIntentionToReactivate("intention");
    originalView.setSummaryLinks(List.of());

    var deletedView = new IntegratedRigView();
    deletedView.setName("");
    deletedView.setSummaryLinks(List.of());

    var diffResult = differenceService.differentiate(deletedView, originalView);

    var areAllFieldsDeleted = differenceFreemarkerService.areAllFieldsDeleted(diffResult);

    assertTrue(areAllFieldsDeleted);
  }

  @Test
  public void areAllFieldsDeleted_sameFields() throws NoSuchFieldException, IllegalAccessException {
    var originalView = new IntegratedRigView();
    originalView.setName("name");
    originalView.setStatus("status");
    originalView.setStructure(new StringWithTag("tag"));
    originalView.setIntentionToReactivate("intention");
    originalView.setSummaryLinks(List.of());

    var deletedView = new IntegratedRigView();
    deletedView.setName("name");
    deletedView.setStatus("status");
    deletedView.setStructure(new StringWithTag("tag"));
    deletedView.setIntentionToReactivate("intention");
    deletedView.setSummaryLinks(List.of());

    var diffResult = differenceService.differentiate(deletedView, originalView);

    var areAllFieldsDeleted = differenceFreemarkerService.areAllFieldsDeleted(diffResult);

    assertFalse(areAllFieldsDeleted);
  }

  @Test
  public void areAllFieldsDeleted_differentFields() throws NoSuchFieldException, IllegalAccessException {
    var originalView = new IntegratedRigView();
    originalView.setName(null);
    originalView.setStatus("status");
    originalView.setStructure(new StringWithTag("tag"));
    originalView.setIntentionToReactivate("intention");
    originalView.setSummaryLinks(List.of());

    var deletedView = new IntegratedRigView();
    deletedView.setName("name");
    deletedView.setStatus("new status");
    deletedView.setStructure(new StringWithTag("tag", Tag.NOT_FROM_LIST));
    deletedView.setIntentionToReactivate("another intention");
    deletedView.setSummaryLinks(List.of());

    var diffResult = differenceService.differentiate(deletedView, originalView);

    var areAllFieldsDeleted = differenceFreemarkerService.areAllFieldsDeleted(diffResult);

    assertFalse(areAllFieldsDeleted);
  }
}