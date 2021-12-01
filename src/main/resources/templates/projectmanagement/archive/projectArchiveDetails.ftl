<#include '../../layout.ftl'>

<#if projectArchiveDetailView?has_content>
  <h2 class="govuk-heading-l">Archive details</h2>

  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.checkAnswersRowNoActions prompt="Archive reason" value=projectArchiveDetailView.archiveReason />
    <@checkAnswers.checkAnswersRowNoActions prompt="Archived date" value=projectArchiveDetailView.archivedDate />
    <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Archived by">
      <div>${projectArchiveDetailView.archivedByUserName}</div>
      <div>${projectArchiveDetailView.archivedByUserEmailAddress}</div>
    </@checkAnswers.checkAnswersRowNoActionsWithNested>
  </@fdsCheckAnswers.checkAnswers>
</#if>
