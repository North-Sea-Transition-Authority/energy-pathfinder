<#include '../layout.ftl'>

<#macro _communicationSummary senderName recipientList subject greetingText body signOffText signOffIdentifier>
  <table class="govuk-table communication-summary">
    <caption class="govuk-table__caption govuk-visually-hidden">
      The following information describes the details of an email with the subject ${subject}
    </caption>
    <tbody class="communication-summary__body">
      <tr class="communication-summary__row">
        <th class="communication-summary__key">From</th>
        <td class="communication-summary__value">${senderName}</td>
      </tr>
      <tr class="communication-summary__row">
        <th class="communication-summary__key">To</th>
        <td class="communication-summary__value">
          <div class="communication-summary__recipient-list">
            <#if recipientList?size &gt; 5>
              <@fdsDetails.summaryDetails summaryTitle="Show email recipients">
                <@_recipientList recipientList=recipientList />
              </@fdsDetails.summaryDetails>
              <#else>
                <@_recipientList recipientList=recipientList />
            </#if>
          </div>
        </td>
      </tr>
      <tr class="communication-summary__row">
        <th class="communication-summary__key">Subject</th>
        <td class="communication-summary__value">${subject}</td>
      </tr>
      <tr>
        <th class="govuk-visually-hidden">Body</th>
        <td class="communication-summary__value">
          <@multiLineText.multiLineText blockClass="govuk-body">
            <div>${greetingText} <span class="communication-summary__placeholder">[[Recipient name]]</span>,</div>
            <div>${body}</div>
            <div>${signOffText},</div>
            <div>${signOffIdentifier}</div>
          </@multiLineText.multiLineText>
        </td>
      </tr>
    </tbody>
  </table>
</#macro>

<#macro _recipientList recipientList>
  <ul class="govuk-list">
    <#list recipientList as recipient>
      <li>${recipient}</li>
    </#list>
  </ul>
</#macro>

<#macro communicationSummary communicationView>
  <#assign emailView = communicationView.emailView />
  <@_communicationSummary
    senderName=emailView.senderName
    recipientList=emailView.recipientList
    subject=emailView.subject
    greetingText=emailView.greetingText
    body=emailView.body
    signOffText=emailView.signOffText
    signOffIdentifier=emailView.signOffIdentifier
  />
</#macro>

<#macro sentCommunicationSummary sentCommunicationView>
  <@fdsDataItems.dataItem>
    <@fdsDataItems.dataValues key="Date sent" value=sentCommunicationView.formattedDateSent />
    <@fdsDataItems.dataValues key="Sent by" value=sentCommunicationView.submittedByUserName />
  </@fdsDataItems.dataItem>
  <#assign emailView = sentCommunicationView.emailView />
  <@_communicationSummary
    senderName=emailView.senderName
    recipientList=emailView.recipientList
    subject=emailView.subject
    greetingText=emailView.greetingText
    body=emailView.body
    signOffText=emailView.signOffText
    signOffIdentifier=emailView.signOffIdentifier
  />
</#macro>