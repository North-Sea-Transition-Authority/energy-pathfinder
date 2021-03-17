<#include '../../layout.ftl'>

<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true errorItems=errorList>
  <@fdsForm.htmlForm>
    <@fdsRadio.radioGroup
      labelText="When do you expect the decommissioning work to start?"
      path="form.decommissioningStartDateType"
      hiddenContent=true
    >
      <@fdsRadio.radioItem path="form.decommissioningStartDateType" itemMap=exactDecommissioningStartDateType isFirstItem=true>
        <@fdsDateInput.dateInput
          dayPath="form.exactDecommissioningStartDate.day"
          monthPath="form.exactDecommissioningStartDate.month"
          yearPath="form.exactDecommissioningStartDate.year"
          labelText="What is the decommissioning work start date?"
          formId="exactDecommissioningStartDate-day-month-year"
          nestingPath="form.decommissioningStartDateType"
        />
      </@fdsRadio.radioItem>
      <@fdsRadio.radioItem path="form.decommissioningStartDateType" itemMap=estimatedDecommissioningStartDateType>
        <@quarterYear.standardQuarterYearInput
          quarterYearInputPath="form.estimatedDecommissioningStartDate"
          legendHeading="What is the estimated decommissioning work start date?"
          quarterOptions=quarters
          formId="estimatedDecommissioningStartDate"
          hintText=""
          nestingPath="form.decommissioningStartDateType"
        />
      </@fdsRadio.radioItem>
      <@fdsRadio.radioItem path="form.decommissioningStartDateType" itemMap=unknownDecommissioningStartDateType>
        <@fdsTextarea.textarea
          path="form.decommissioningStartDateNotProvidedReason"
          labelText="Provide a reason why you are unable to provide the decommissioning work start date"
          nestingPath="form.decommissioningStartDateType"
        />
      </@fdsRadio.radioItem>
    </@fdsRadio.radioGroup>
    <@fdsRadio.radioGroup
      labelText="When do you expect Cessation of Production (CoP)?"
      path="form.cessationOfProductionDateType"
      hiddenContent=true
    >
      <@fdsRadio.radioItem path="form.cessationOfProductionDateType" itemMap=exactCessationOfProductionDateType isFirstItem=true>
        <@fdsDateInput.dateInput
          dayPath="form.exactCessationOfProductionDate.day"
          monthPath="form.exactCessationOfProductionDate.month"
          yearPath="form.exactCessationOfProductionDate.year"
          labelText="What is the CoP date?"
          formId="exactCessationOfProductionDate-day-month-year"
          nestingPath="form.cessationOfProductionDateType"
        />
      </@fdsRadio.radioItem>
      <@fdsRadio.radioItem path="form.cessationOfProductionDateType" itemMap=estimatedCessationOfProductionDateType>
        <@quarterYear.standardQuarterYearInput
          quarterYearInputPath="form.estimatedCessationOfProductionDate"
          legendHeading="What is the estimated CoP date?"
          quarterOptions=quarters
          formId="estimatedCessationOfProductionDate"
          hintText=""
          nestingPath="form.cessationOfProductionDateType"
        />
      </@fdsRadio.radioItem>
      <@fdsRadio.radioItem path="form.cessationOfProductionDateType" itemMap=unknownCessationOfProductionDateType>
        <@fdsTextarea.textarea
          path="form.cessationOfProductionDateNotProvidedReason"
          labelText="Provide a reason why you are unable to provide the CoP date"
          nestingPath="form.cessationOfProductionDateType"
        />
      </@fdsRadio.radioItem>
    </@fdsRadio.radioGroup>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>
