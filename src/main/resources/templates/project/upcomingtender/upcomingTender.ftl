<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Upcoming tender" pageHeading="Upcoming tender" breadcrumbs=true>
  <#if errorList?has_content>
      <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsForm.htmlForm>
    <@fdsSearchSelector.searchSelectorRest path="form.tenderFunction" selectorMinInputLength=0 labelText="What function is the tender for?" restUrl=springUrl(tenderRestUrl)  preselectedItems=preselectedTender!{} />
    <@fdsTextarea.textarea path="form.descriptionOfWork" labelText="Provide a detailed description of the work"/>
    <@fdsNumberInput.twoNumberInputs pathOne="form.estimatedTenderDate.month" pathTwo="form.estimatedTenderDate.year" fieldsetHeadingClass="govuk-fieldset__legend--s" labelText="Estimated tender date" formId="estimatedTenderDate-month-year">
        <@fdsNumberInput.numberInputItem path="form.estimatedTenderDate.month" labelText="Month" inputClass="govuk-input--width-2"/>
        <@fdsNumberInput.numberInputItem path="form.estimatedTenderDate.year" labelText="Year" inputClass="govuk-input--width-4"/>
    </@fdsNumberInput.twoNumberInputs>

    <@fdsRadio.radioGroup
      labelText="Contract band"
      path="form.contractBand"
      fieldsetHeadingClass="govuk-fieldset__legend--s"
      optionalLabel=true
    >
      <#list contractBands as band>
        <@fdsRadio.radioItem path="form.contractBand" itemMap={band.name() : band.getDisplayName()} isFirstItem=band.getDisplayOrder()=1 />
      </#list>
    </@fdsRadio.radioGroup>

    <@contactDetails.contactDetails legendHeading="Tender contact details"/>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>