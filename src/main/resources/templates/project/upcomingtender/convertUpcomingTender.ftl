<#include '../../layout.ftl'>
<#import './_upcomingTenderSummary.ftl' as tenderSummary>

<#assign title = "Are you sure you want to convert upcoming tender ${displayOrder} to an awarded contract?"/>

<@defaultPage htmlTitle=title pageHeading=title breadcrumbs=true errorItems=errorList>

    <@fdsForm.htmlForm>

      <#if !view.valid>
          <@fdsError.singleErrorSummary errorMessage="You cannot convert this upcoming tender to an awarded contract until all the sections are completed"/>
      <#else>
          <@fdsWarning.warning>
            When an upcoming tender is converted to an awarded contract, the upcoming tender will be removed.
          </@fdsWarning.warning>
      </#if>

      <@tenderSummary.upcomingTenderSummary view=view showHeader=false showActions=false/>

      <#if view.valid>
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
      <#else>

          <@fdsAction.link
            linkText="Back to upcoming tenders"
            linkUrl=springUrl(cancelUrl)
            linkClass="govuk-link govuk-!-font-size-19"
          />

      </#if>

  </@fdsForm.htmlForm>

</@defaultPage>
