<#include '../../layout.ftl'>

<#if noUpdateNotificationView?has_content>
  <h2 class="govuk-heading-l">No change summary</h2>
  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.checkAnswersRowNoActions prompt="Reason for the supply chain" value=noUpdateNotificationView.supplyChainReason />
    <#if noUpdateNotificationView.regulatorReason?has_content>
      <@checkAnswers.checkAnswersRowNoActions prompt="Reason for the ${service.customerMnemonic}" value=noUpdateNotificationView.regulatorReason />
    </#if>
    <@checkAnswers.checkAnswersRowNoActions prompt="No change submission date" value=noUpdateNotificationView.submittedDate />
    <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="No change submitted by">
      <div>${noUpdateNotificationView.submittedByUserName}</div>
      <div><@mailTo.mailToLink mailToEmailAddress=noUpdateNotificationView.submittedByUserEmailAddress/></div>
    </@checkAnswers.checkAnswersRowNoActionsWithNested>
  </@fdsCheckAnswers.checkAnswers>
</#if>
