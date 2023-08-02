<#include '../../layout.ftl'>
<#import './_upcomingTenderSummary.ftl' as tenderSummary>

<#assign title = "Are you sure you want to convert upcoming tender ${displayOrder} to an awarded contract?"/>

<@defaultPage htmlTitle=title pageHeading=title breadcrumbs=true errorItems=errorList>

    <@fdsForm.htmlForm>
      <p class="govuk-body">
        Once an upcoming tender is converted to an awarded contract, the upcoming tender will be removed.
      </p>

      <@tenderSummary.upcomingTenderSummary view=view showHeader=false showActions=false/>

      <h3 class="govuk-heading-m">Awarded contract details</h3>

      <@fdsTextInput.textInput path="form.contractorName" labelText="Contractor name"/>

      <@fdsDateInput.dateInput
        dayPath="form.dateAwarded.day"
        monthPath="form.dateAwarded.month"
        yearPath="form.dateAwarded.year"
        labelText="Date contract awarded"
        formId="dateAwarded-day-month-year"
      />

      <@fdsAction.submitButtons
        primaryButtonText="Convert to awarded contract"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl=springUrl(cancelUrl)
      />

  </@fdsForm.htmlForm>

</@defaultPage>
