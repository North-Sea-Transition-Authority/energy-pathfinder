<#include '../../layout.ftl'>

<#if projectTransferView?has_content>
  <h2 class="govuk-heading-l">Operator change details</h2>
  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.checkAnswersRowNoActions prompt="Previous operator" value=projectTransferView.oldOperator />
    <@checkAnswers.checkAnswersRowNoActions prompt="New operator" value=projectTransferView.newOperator />
    <@checkAnswers.checkAnswersRowNoActions prompt="Reason for operator change" value=projectTransferView.transferReason />
    <@checkAnswers.checkAnswersRowNoActions prompt="Operator change date" value=projectTransferView.transferDate />
    <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Operator changed by">
      <div>${projectTransferView.transferredByUserName}</div>
      <div>${projectTransferView.transferredByUserEmailAddress}</div>
    </@checkAnswers.checkAnswersRowNoActionsWithNested>
  </@fdsCheckAnswers.checkAnswers>
</#if>
