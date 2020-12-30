<#include '../layout.ftl'>

<#assign title = "Archive project">

<@defaultPage htmlTitle=title breadcrumbs=true fullWidthColumn=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@noEscapeHtml.noEscapeHtml html=projectHeaderHtml />

  <h2 class="govuk-heading-l">${title}</h2>

  <@fdsForm.htmlForm>
    <@fdsTextarea.textarea
      path="form.archiveReason"
      labelText="What is the reason you are archiving the project?"
    />
    <@fdsAction.submitButtons
      primaryButtonText="Save and complete"
      linkSecondaryAction=true
      secondaryLinkText="Cancel"
      linkSecondaryActionUrl=springUrl(cancelUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>
