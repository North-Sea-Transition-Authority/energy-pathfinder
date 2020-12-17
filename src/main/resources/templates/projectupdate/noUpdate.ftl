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
      path="form.reasonNoUpdateRequired"
      labelText="What is the reason no changes are required?"
    />
      <@fdsAction.submitButtons
        primaryButtonText="Save and complete"
        linkSecondaryAction=true
        secondaryLinkText="Cancel"
        linkSecondaryActionUrl=springUrl(cancelUrl)
      />
  </@fdsForm.htmlForm>
</@defaultPage>
