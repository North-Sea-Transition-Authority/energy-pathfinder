<#include '../../layout.ftl'>

<@defaultPage htmlTitle=pageNameSingular pageHeading=pageNameSingular breadcrumbs=true errorItems=errorList>
  <@fdsForm.htmlForm>
    <@fdsSearchSelector.searchSelectorRest
      path="form.departmentType"
      selectorMinInputLength=0
      labelText="What department is the tender for?"
      restUrl=springUrl(departmentTenderRestUrl)
      preselectedItems=preSelectedFunction!{}
    />
    <@fdsTextarea.textarea path="form.descriptionOfWork" labelText="Provide a detailed description of the work"/>
    <@fdsDateInput.dateInput
      dayPath="form.estimatedTenderDate.day"
      monthPath="form.estimatedTenderDate.month"
      yearPath="form.estimatedTenderDate.year"
      labelText="Estimated tender date"
      formId="estimatedTenderDate-day-month-year"
    />
    <@fdsRadio.radio
      labelText="Contract band"
      path="form.contractBand"
      radioItems=contractBands
    />
    <@contactDetails.standardContactDetails path="form.contactDetail" legendHeading="Tender contact details"/>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>