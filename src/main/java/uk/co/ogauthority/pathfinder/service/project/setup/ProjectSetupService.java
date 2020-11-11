package uk.co.ogauthority.pathfinder.service.project.setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.tasks.ProjectTaskListSetup;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.TaskListSectionAnswer;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.TaskListSectionQuestion;
import uk.co.ogauthority.pathfinder.model.form.project.setup.ProjectSetupForm;
import uk.co.ogauthority.pathfinder.repository.project.tasks.ProjectTaskListSetupRepository;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;

/**
 * The service used in the 'Set up your project' page to decide what optional features of a project should be included.
 */
@Service
public class ProjectSetupService {
  //TODO refactor into ProjectTaskListSetupService (?) which handles the entity and form specific stuff

  public static final String MODEL_AND_VIEW_PATH = "project/setup/projectSetup";

  private final ProjectTaskListSetupRepository projectTaskListSetupRepository;
  private final ProjectInformationService projectInformationService;

  @Autowired
  public ProjectSetupService(ProjectTaskListSetupRepository projectTaskListSetupRepository,
                             ProjectInformationService projectInformationService) {
    this.projectTaskListSetupRepository = projectTaskListSetupRepository;
    this.projectInformationService = projectInformationService;
  }

  public List<TaskListSectionQuestion> getSectionQuestionsForProjectDetail(ProjectDetail detail) {
    return projectInformationService.isDecomRelated(detail)
        ? TaskListSectionQuestion.getAllValues()
        : TaskListSectionQuestion.getNonDecommissioningRelatedValues();
  }

  public ModelAndView getProjectSetupModelAndView(ProjectDetail detail, ProjectSetupForm form) {
    return new ModelAndView(MODEL_AND_VIEW_PATH)
        .addObject("sections", getSectionQuestionsForProjectDetail(detail))
        .addObject("form", form);
  }

  @Transactional
  public ProjectTaskListSetup createOrUpdateProjectTaskListSetup(ProjectDetail detail, ProjectSetupForm form) {
    var taskListSetup = projectTaskListSetupRepository.findByProjectDetail(detail).orElse(
        new ProjectTaskListSetup()
    );
    taskListSetup.setProjectDetail(detail);
    taskListSetup.setTaskListAnswers(getTaskListSectionAnswersFromForm(form));
    taskListSetup.setTaskListSectionQuestions(getTaskListSectionQuestionsFromForm(form));
    return projectTaskListSetupRepository.save(taskListSetup);
  }

  public ProjectSetupForm getForm(ProjectDetail detail) {
    return projectTaskListSetupRepository.findByProjectDetail(detail)
        .map(setup -> {
          var form = new ProjectSetupForm();
          setFormFieldsFromProjectTaskListSetup(setup, form);
          return form;
        })
        .orElse(new ProjectSetupForm());
  }

  public void setFormFieldsFromProjectTaskListSetup(ProjectTaskListSetup taskListSetup, ProjectSetupForm form) {
    //stream list of TaskListSectionQuestionAnswers and match the first which corresponds to the form field
    //Set that as the form field value
    form.setUpcomingTendersIncluded(
      getAnswerForQuestion(taskListSetup.getTaskListAnswers(), TaskListSectionQuestion.UPCOMING_TENDERS)
    );

    form.setAwardedContractsIncluded(
      getAnswerForQuestion(taskListSetup.getTaskListAnswers(), TaskListSectionQuestion.AWARDED_CONTRACTS)
    );

    form.setCollaborationOpportunitiesIncluded(
      getAnswerForQuestion(taskListSetup.getTaskListAnswers(), TaskListSectionQuestion.COLLABORATION_OPPORTUNITIES)
    );

    form.setWellsIncluded(
      getAnswerForQuestion(taskListSetup.getTaskListAnswers(), TaskListSectionQuestion.WELLS)
    );

    form.setPlatformsFpsosIncluded(
      getAnswerForQuestion(taskListSetup.getTaskListAnswers(), TaskListSectionQuestion.PLATFORM_FPSO)
    );

    form.setSubseaInfrastructureIncluded(
      getAnswerForQuestion(taskListSetup.getTaskListAnswers(), TaskListSectionQuestion.SUBSEA_INFRASTRUCTURE)
    );

    form.setIntegratedRigsIncluded(
      getAnswerForQuestion(taskListSetup.getTaskListAnswers(), TaskListSectionQuestion.INTEGRATED_RIGS)
    );

    form.setPipelinesIncluded(
      getAnswerForQuestion(taskListSetup.getTaskListAnswers(), TaskListSectionQuestion.PIPELINES)
    );
  }

  private TaskListSectionAnswer getAnswerForQuestion(List<TaskListSectionAnswer> answers, TaskListSectionQuestion question) {
    return answers.stream()
        .filter(a -> containsYesOrNoAnswerForQuestion(question, a))
        .findFirst().orElse(null);
  }

  private boolean containsYesOrNoAnswerForQuestion(TaskListSectionQuestion question, TaskListSectionAnswer answer) {
    return question.getNoAnswer().equals(answer) || question.getYesAnswer().equals(answer);
  }

  public List<TaskListSectionQuestion> getTaskListSectionQuestionsFromForm(ProjectSetupForm form) {
    var taskListSectionQuestionAnswers = getTaskListSectionAnswersFromForm(form);

    return TaskListSectionQuestion.getAllValues().stream()
        .filter(tlq -> taskListSectionQuestionAnswers.contains(tlq.getYesAnswer()))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  /**
   * Get the fields from the form and map the set values to a {@link TaskListSectionAnswer }.
   * @param form ProjectSetupForm
   * @return The list of TaskListSectionAnswer values set in the form
   */
  private List<TaskListSectionAnswer> getTaskListSectionAnswersFromForm(ProjectSetupForm form) {
    return Arrays.stream(form.getClass().getDeclaredFields())
        .map(f -> {
          try {
            f.setAccessible(true); //TODO raise Jira about this or set fields to be public
            return (TaskListSectionAnswer) f.get(form);
          } catch (IllegalAccessException | NullPointerException e) {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  /**
   * Validate the projectLocationForm, calls custom validator first.
   * Validates dates if FDP or Decom program questions are true.
   */
//  public BindingResult validate(ProjectSetupForm form,
//                                BindingResult bindingResult,
//                                ValidationType validationType) {
//    var projectLocationValidationHint = createProjectLocationValidationHint(validationType);
//    projectLocationFormValidator.validate(form, bindingResult, projectLocationValidationHint);
//    return validationService.validate(form, bindingResult, validationType);
//  }

  @Transactional
  public void removeDecomSelectionsIfPresent(ProjectDetail detail) {
    //get the ProjectTaskListSetup
    //If not decom related
    //Filter the results of the setup to remove the decom ones
    //save result
    //TODO this would have to change the question answers if it was going from decom to not??
  }

}
