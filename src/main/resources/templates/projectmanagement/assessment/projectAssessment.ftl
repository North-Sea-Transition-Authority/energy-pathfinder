<#include '../../layout.ftl'>

<#if projectAssessmentView?has_content>
  <h2 class="govuk-heading-l">Assessment</h2>

  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.checkAnswersRowNoActions prompt="Ready to be published" value=projectAssessmentView.readyToBePublished?string("Yes", "No") />
    <#if projectAssessmentView.readyToBePublished && projectAssessmentView.updateRequired?has_content>
      <@checkAnswers.checkAnswersRowNoActions prompt="Update required" value=projectAssessmentView.updateRequired?string("Yes", "No") />
    </#if>
    <@checkAnswers.checkAnswersRowNoActions prompt="Assessment date" value=projectAssessmentView.assessmentDate />
    <@checkAnswers.checkAnswersRowNoActions prompt="Assessed by" value=projectAssessmentView.assessedByUser />
  </@fdsCheckAnswers.checkAnswers>
</#if>
