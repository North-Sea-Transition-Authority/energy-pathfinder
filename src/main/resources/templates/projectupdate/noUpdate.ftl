<#include '../layout.ftl'>

<#assign title = "Confirm no changes required">

<@defaultPage htmlTitle=title breadcrumbs=true fullWidthColumn=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@noEscapeHtml.noEscapeHtml html=projectHeaderHtml />

  <h2 class="govuk-heading-l">${title}</h2>

  <@fdsForm.htmlForm>
    <@fdsTextarea.textarea
      path="form.supplyChainReason"
      labelText="Provide a reason for the supply chain as to why no changes are required"
      hintText="This reason will be visible to the supply chain"
    />
    <@fdsTextarea.textarea
      path="form.regulatorReason"
      labelText="Provide a reason for the ${service.customerMnemonic} as to why no changes are required"
      hintText="This reason will only be visible to the ${service.customerMnemonic}"
      optionalLabel=true
    />
      <@fdsAction.submitButtons
        primaryButtonText="Save and complete"
        linkSecondaryAction=true
        secondaryLinkText="Cancel"
        linkSecondaryActionUrl=springUrl(cancelUrl)
      />
  </@fdsForm.htmlForm>
</@defaultPage>
