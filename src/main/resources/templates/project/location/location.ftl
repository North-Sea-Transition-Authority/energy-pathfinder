<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Location" pageHeading="Location" breadcrumbs=true >
    <#if errorList?has_content>
        <@fdsError.errorSummary errorItems=errorList />
    </#if>

    <@fdsForm.htmlForm>
      <@fdsSearchSelector.searchSelectorRest path="form.field" selectorMinInputLength=3  labelText="Which field is the project related to?" restUrl=springUrl(fieldsRestUrl)  preselectedItems=preselectedField!{} />
      <@fdsSelect.select path="form.fieldType" labelText="Field type" options=fieldTypeMap/>
      <@fdsTextInput.textInput path="form.waterDepth" labelText="What is the water depth? " suffix=waterDepthUnit.plural suffixScreenReaderPrompt=waterDepthUnit.screenReaderSuffix inputClass="govuk-input--width-4" />
      <@fdsRadio.radioGroup path="form.approvedFieldDevelopmentPlan" labelText="Do you have an approved Field Development Plan (FDP)?" fieldsetHeadingClass="govuk-fieldset__legend" hiddenContent=true>
        <@fdsRadio.radioYes path="form.approvedFieldDevelopmentPlan">
          <@fdsNumberInput.twoNumberInputs pathOne="form.approvedFdpDate.month" pathTwo="form.approvedFdpDate.year" nestingPath="form.approvedFieldDevelopmentPlan" fieldsetHeadingClass="govuk-fieldset__legend" labelText="What is the FDP approval date?" formId="approvedFdpDate-month-year">
            <@fdsNumberInput.numberInputItem path="form.approvedFdpDate.month" labelText="Month" inputClass="govuk-input--width-2"/>
            <@fdsNumberInput.numberInputItem path="form.approvedFdpDate.year" labelText="Year" inputClass="govuk-input--width-4"/>
          </@fdsNumberInput.twoNumberInputs>
        </@fdsRadio.radioYes>
        <@fdsRadio.radioNo path="form.approvedFieldDevelopmentPlan"/>
      </@fdsRadio.radioGroup>

      <@fdsRadio.radioGroup path="form.approvedDecomProgram" labelText="Do you have an approved Decomissioning Program (DP)?" fieldsetHeadingClass="govuk-fieldset__legend" hiddenContent=true>
        <@fdsRadio.radioYes path="form.approvedDecomProgram">
          <@fdsNumberInput.twoNumberInputs pathOne="form.approvedDecomProgramDate.month" pathTwo="form.approvedDecomProgramDate.year" nestingPath="form.approvedDecomProgram" fieldsetHeadingClass="govuk-fieldset__legend" labelText="What is the DP approval date?" formId="approvedDecomProgramDate-month-year">
            <@fdsNumberInput.numberInputItem path="form.approvedDecomProgramDate.month" labelText="Month" inputClass="govuk-input--width-2"/>
            <@fdsNumberInput.numberInputItem path="form.approvedDecomProgramDate.year" labelText="Year" inputClass="govuk-input--width-4"/>
          </@fdsNumberInput.twoNumberInputs>
        </@fdsRadio.radioYes>
        <@fdsRadio.radioNo path="form.approvedDecomProgram"/>
      </@fdsRadio.radioGroup>

      <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
    </@fdsForm.htmlForm>
</@defaultPage>