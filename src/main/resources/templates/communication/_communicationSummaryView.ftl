<#include '../layout.ftl'>

<#macro _communicationSummary senderName recipients subject greetingText body signOffText signOffIdentifier>
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
        <td class="communication-summary__value">${recipients}</td>
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

<#macro communicationSummary communicationView>
  <@_communicationSummary
    senderName=communicationView.senderName
    recipients=communicationView.recipientCsv
    subject=communicationView.subject
    greetingText=communicationView.greetingText
    body=communicationView.body
    signOffText=communicationView.signOffText
    signOffIdentifier=communicationView.signOffIdentifier
  />
</#macro>

<#macro sentCommunicationSummary sentCommunicationView>
  <#assign emailAddressLink>
    <@fdsAction.link
      linkText="${sentCommunicationView.submittedByEmailAddress}"
      linkUrl="mailto:${sentCommunicationView.submittedByEmailAddress}"
    />
  </#assign>
  <@fdsDataItems.dataItem>
    <@fdsDataItems.dataValues key="Date sent" value=sentCommunicationView.formattedDateSent />
    <@fdsDataItems.dataValues key="Sent by" value="${sentCommunicationView.submittedByUserName} ${emailAddressLink}" />
  </@fdsDataItems.dataItem>
  <@_communicationSummary
    senderName=sentCommunicationView.senderName
    recipients=sentCommunicationView.recipientCsv
    subject=sentCommunicationView.subject
    greetingText=sentCommunicationView.greetingText
    body=sentCommunicationView.body
    signOffText=sentCommunicationView.signOffText
    signOffIdentifier=sentCommunicationView.signOffIdentifier
  />
</#macro>