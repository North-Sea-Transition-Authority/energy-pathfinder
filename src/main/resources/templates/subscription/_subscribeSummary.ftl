<#macro _subscriptionSummaryText pronoun contentHasBottomMargin=true>
  <#assign serviceName = service.serviceName>
  <p class="govuk-body">
    ${pronoun} will receive an email once a month showing new or updated ${serviceName} projects.
  </p>
  <p class="govuk-body <#if !contentHasBottomMargin>govuk-!-margin-bottom-0</#if>">
    ${pronoun} may also receive emails from the ${service.customerName} with important information relating to ${serviceName}.
  </p>
</#macro>