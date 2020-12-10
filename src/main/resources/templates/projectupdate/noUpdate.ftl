<#include '../layout.ftl'>

<@defaultPage htmlTitle="Confirm no changes required" breadcrumbs=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsForm.htmlForm>
    <@fdsTextarea.textarea
      path="form.reasonNoUpdateRequired"
      labelText="What is the reason no changes are required?"
      labelHeadingClass="govuk-label--l"
      pageHeading=true
    />
      <@fdsAction.submitButtons
        primaryButtonText="Save and complete"
        linkSecondaryAction=true
        secondaryLinkText="Cancel"
        linkSecondaryActionUrl=springUrl(cancelUrl)
      />
  </@fdsForm.htmlForm>
</@defaultPage>
