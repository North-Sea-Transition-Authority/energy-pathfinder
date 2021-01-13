<#include '../../layout.ftl'>

<#if regulatorUpdateRequestView?has_content>
  <h2 class="govuk-heading-l">${service.customerMnemonic} update request</h2>
  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.checkAnswersRowNoActions prompt="Reason for update" value=regulatorUpdateRequestView.updateReason />
    <@checkAnswers.checkAnswersRowNoActions prompt="Deadline date" value=regulatorUpdateRequestView.deadlineDate />
    <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Update requested by">
      <div>${regulatorUpdateRequestView.requestedByUserName}</div>
      <div>${regulatorUpdateRequestView.requestedByUserEmailAddress}</div>
    </@checkAnswers.checkAnswersRowNoActionsWithNested>
  </@fdsCheckAnswers.checkAnswers>
</#if>
