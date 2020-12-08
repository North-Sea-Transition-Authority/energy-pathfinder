<#include '../layout.ftl'>

<#assign title = "Request update" />

<@defaultPage htmlTitle=title pageHeading=title breadcrumbs=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsForm.htmlForm>
    <@fdsTextarea.textarea
      path="form.updateReason"
      labelText="What is the reason for the update?"
    />
    <@fdsDateInput.dateInput
      dayPath="form.deadlineDate.day"
      monthPath="form.deadlineDate.month"
      yearPath="form.deadlineDate.year"
      labelText="Deadline"
      formId="deadlineDate-day-month-year"
    />
    <@fdsAction.submitButtons
      primaryButtonText="Save and complete"
      linkSecondaryAction=true
      secondaryLinkText="Cancel"
      linkSecondaryActionUrl=springUrl(cancelUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>
