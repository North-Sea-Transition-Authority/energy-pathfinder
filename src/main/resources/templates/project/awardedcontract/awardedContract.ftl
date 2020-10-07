<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Awarded contract" pageHeading="Awarded contract" breadcrumbs=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsForm.htmlForm>
    <@fdsTextInput.textInput path="form.contractorName" labelText="Contractor name"/>
    <@fdsSearchSelector.searchSelectorRest
      path="form.contractFunction"
      selectorMinInputLength=0
      labelText="What function is the awarded contract for?"
      restUrl=springUrl(contractFunctionRestUrl)
      preselectedItems=preSelectedContractFunctionMap!{}
    />
    <@fdsTextarea.textarea path="form.descriptionOfWork" labelText="Provide a detailed description of the work"/>
    <@fdsDateInput.dateInput
      dayPath="form.dateAwarded.day"
      monthPath="form.dateAwarded.month"
      yearPath="form.dateAwarded.year"
      labelText="Date contract awarded"
      formId="dateAwarded-day-month-year"
    />
    <@fdsRadio.radio
      labelText="Contract band"
      path="form.contractBand"
      radioItems=contractBands
    />
    <@contactDetails.standardContactDetails path="form.contactDetail" legendHeading="Contract contact details"/>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>