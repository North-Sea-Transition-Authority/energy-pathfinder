<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Upcoming tender" pageHeading="Upcoming tender" breadcrumbs=true>
  <#if errorList?has_content>
      <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsForm.htmlForm>
    <@fdsSearchSelector.searchSelectorRest path="form.tenderFunction" selectorMinInputLength=0 labelText="What function is the tender for?" restUrl=springUrl(tenderRestUrl)  preselectedItems=preselectedTender!{} />
    <@fdsTextarea.textarea path="form.descriptionOfWork" labelText="Provide a detailed description of the work"/>
    <@fdsDateInput.dateInput
      dayPath="form.estimatedTenderDate.day"
      monthPath="form.estimatedTenderDate.month"
      yearPath="form.estimatedTenderDate.year"
      labelText="Estimated tender date"
      formId="estimatedTenderDate-day-month-year"
      fieldsetHeadingClass="govuk-fieldset__legend--s"
    />
    <@fdsRadio.radio
      labelText="Contract band"
      path="form.contractBand"
      radioItems=contractBands
      fieldsetHeadingClass="govuk-fieldset__legend--s"
      optionalLabel=true
    />
    <@contactDetails.standardContactDetails path="form.contactDetail" legendHeading="Tender contact details"/>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>