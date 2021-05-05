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
    <@quarterYear.standardQuarterYearInput
      quarterYearInputPath="form.estimatedTenderStartDate"
      legendHeading="What is the estimated tender date?"
      quarterOptions=quarters
      formId="estimatedTenderStartDate"
      hintText=""
    />
    <@fdsRadio.radio
      labelText="Contract band"
      path="form.contractBand"
      radioItems=contractBands
    />
    <@_contractTerm
      contractTermMeasurementPeriodPath="form.contractTermDurationPeriod"
      contractTermPeriodDaysMapOption=contractTermPeriodDays
      contractTermDayDurationPath="form.contractTermDayDuration"
      contractTermPeriodWeeksMapOption=contractTermPeriodWeeks
      contractTermWeekDurationPath="form.contractTermWeekDuration"
      contractTermPeriodMonthsMapOption=contractTermPeriodMonths
      contractTermMonthDurationPath="form.contractTermMonthDuration"
      contractTermPeriodYearsMapOption=contractTermPeriodYears
      contractTermYearDurationPath="form.contractTermYearDuration"
    />
    <@contactDetails.standardContactDetails path="form.contactDetail" legendHeading="Tender contact details"/>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>

<#macro _contractTerm
  contractTermMeasurementPeriodPath
  contractTermPeriodDaysMapOption
  contractTermDayDurationPath
  contractTermPeriodWeeksMapOption
  contractTermWeekDurationPath
  contractTermPeriodMonthsMapOption
  contractTermMonthDurationPath
  contractTermPeriodYearsMapOption
  contractTermYearDurationPath
>
  <#assign contractTermDurationPrefix="What is the length of the contract in"/>
  <@fdsRadio.radioGroup
    labelText="Is the length of the contract measured in days, weeks, months or years?"
    path=contractTermMeasurementPeriodPath
    hiddenContent=true
  >
    <@_contractTermRadioItem
      radioItemPath=contractTermMeasurementPeriodPath
      radioItemOptionMap=contractTermPeriodDaysMapOption
      durationTextInputPath=contractTermDayDurationPath
      durationTextInputLabel="${contractTermDurationPrefix} days?"
      durationTextInputSuffix="days"
      isFirstRadioItem=true
    />
    <@_contractTermRadioItem
      radioItemPath=contractTermMeasurementPeriodPath
      radioItemOptionMap=contractTermPeriodWeeksMapOption
      durationTextInputPath=contractTermWeekDurationPath
      durationTextInputLabel="${contractTermDurationPrefix} weeks?"
      durationTextInputSuffix="weeks"
    />
    <@_contractTermRadioItem
      radioItemPath=contractTermMeasurementPeriodPath
      radioItemOptionMap=contractTermPeriodMonthsMapOption
      durationTextInputPath=contractTermMonthDurationPath
      durationTextInputLabel="${contractTermDurationPrefix} months?"
      durationTextInputSuffix="months"
    />
    <@_contractTermRadioItem
      radioItemPath=contractTermMeasurementPeriodPath
      radioItemOptionMap=contractTermPeriodYearsMapOption
      durationTextInputPath=contractTermYearDurationPath
      durationTextInputLabel="${contractTermDurationPrefix} years?"
      durationTextInputSuffix="years"
    />
  </@fdsRadio.radioGroup>
</#macro>

<#macro _contractTermRadioItem
  radioItemPath
  radioItemOptionMap
  durationTextInputPath
  durationTextInputLabel
  durationTextInputSuffix
  isFirstRadioItem=false
>
  <@fdsRadio.radioItem path=radioItemPath itemMap=radioItemOptionMap isFirstItem=isFirstRadioItem>
    <@fdsTextInput.textInput
      path=durationTextInputPath
      labelText=durationTextInputLabel
      suffix=durationTextInputSuffix
      nestingPath=radioItemPath
      inputClass="govuk-input--width-4"
    />
  </@fdsRadio.radioItem>
</#macro>