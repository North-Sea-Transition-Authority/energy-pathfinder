<#include '../layout.ftl'>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle topNavigation=true twoThirdsColumn=true breadcrumbs=true>
  <table class="govuk-table communication-summary">
    <tbody>
      <tr class="communication-summary__row">
        <th class="communication-summary__key">From</th>
        <td class="communication-summary__value">${service.serviceName}</td>
      </tr>
      <tr class="communication-summary__row">
        <th class="communication-summary__key">To</th>
        <td class="communication-summary__value">${recipientString}</td>
      </tr>
      <tr class="communication-summary__row">
        <th class="communication-summary__key">Subject</th>
        <td class="communication-summary__value">${communication.emailSubject}</td>
      </tr>
      <tr>
        <th class="govuk-visually-hidden">Body</th>
        <td class="communication-summary__value">
          <@multiLineText.multiLineText blockClass="govuk-body">
            <div>${greetingText} <span class="communication-summary__placeholder">[[Recipient name]]</span>,</div>
            <div>${communication.emailBody}</div>
            <div>${signOffText},</div>
            <div>${signOffIdentifier}</div>
          </@multiLineText.multiLineText>
        </td>
      </tr>
    </tbody>
  </table>
    <@fdsForm.htmlForm>
      <@fdsAction.submitButtons
        primaryButtonText="Send email"
        linkSecondaryAction=true
        secondaryLinkText="Previous"
        linkSecondaryActionUrl=springUrl(previousUrl)
      />
    </@fdsForm.htmlForm>
</@defaultPage>