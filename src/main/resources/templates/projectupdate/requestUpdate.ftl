<#include '../layout.ftl'>

<#assign title = "Request update" />

<@defaultPage htmlTitle=title pageHeading="" breadcrumbs=true fullWidthColumn=true errorItems=errorList>

  <@noEscapeHtml.noEscapeHtml html=projectHeaderHtml />

  <h2 class="govuk-heading-l">${title}</h2>

  <@fdsForm.htmlForm>
    <@fdsTextarea.textarea
      path="form.updateReason"
      labelText="What is the reason for the update?"
    />
    <@fdsDateInput.dateInput
      dayPath="form.deadlineDate.day"
      monthPath="form.deadlineDate.month"
      yearPath="form.deadlineDate.year"
      labelText="Deadline for update submission"
      formId="deadlineDate-day-month-year"
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
