<#include '../../layout.ftl'>

<#if projectArchiveDetailView?has_content>
  <h2 class="govuk-heading-l">Project archive details</h2>

  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.checkAnswersRowNoActions prompt="Archive reason" value=projectArchiveDetailView.archiveReason />
    <@checkAnswers.checkAnswersRowNoActions prompt="Archived date" value=projectArchiveDetailView.archivedDate />
    <@checkAnswers.checkAnswersRowNoActions prompt="Archived by" value=projectArchiveDetailView.archivedByUser />
  </@fdsCheckAnswers.checkAnswers>
</#if>
