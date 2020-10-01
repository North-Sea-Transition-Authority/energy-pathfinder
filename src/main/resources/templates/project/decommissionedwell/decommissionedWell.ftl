<#include '../../layout.ftl'>

<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>
  <@fdsForm.htmlForm>
    <@fdsSearchSelector.searchSelectorRest
      path="form.type"
      selectorMinInputLength=0
      labelText="What type of wells are being decommissioned?"
      restUrl=springUrl(typeRestUrl)
      preselectedItems=preSelectedType!{}
    />
    <@fdsTextInput.textInput
      path="form.numberToBeDecommissioned"
      labelText="How many wells of this type are being decommissioned?"
      inputClass="govuk-input--width-5"
      suffix="well(s)"
    />
    <@quarterYear.standardQuarterYearInput
      quarterYearInputPath="form.plugAbandonmentDate"
      legendHeading="Date of Plug & Abandonment (P&A)"
      quarterOptions=quarters
      formId="plugAbandonmentDate"
    />
    <@fdsRadio.radio
      path="form.plugAbandonmentDateType"
      labelText="Is the P&A date estimated or actual?"
      radioItems=plugAbandonmentDateTypes
    />
    <@fdsSearchSelector.searchSelectorRest
      path="form.operationalStatus"
      selectorMinInputLength=0
      labelText="What is the operational status of these wells?"
      restUrl=springUrl(operationalStatusRestUrl)
      preselectedItems=preSelectedOperationalStatus!{}
    />
    <@fdsSearchSelector.searchSelectorRest
      path="form.mechanicalStatus"
      selectorMinInputLength=0
      labelText="What is the mechanical status of these wells?"
      restUrl=springUrl(mechanicalStatusRestUrl)
      preselectedItems=preSelectedMechanicalStatus!{}
      />
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>