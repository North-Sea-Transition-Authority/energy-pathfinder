<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Location" pageHeading="Location" breadcrumbs=true >
    <#if errorList?has_content>
        <@fdsError.errorSummary errorItems=errorList />
    </#if>

    <@fdsForm.htmlForm>
      <@fdsSearchSelector.searchSelectorRest path="form.field" selectorMinInputLength=3  labelText="Which field is the project related to?" restUrl=springUrl(fieldsRestUrl)  preselectedItems=preselectedField!{} />
      <@fdsSelect.select path="form.fieldType" labelText="Field type" options=fieldTypeMap/>
      <@fdsTextInput.textInput path="form.waterDepth" labelText="What is the water depth? " suffix=waterDepthUnit.plural suffixScreenReaderPrompt=waterDepthUnit.screenReaderSuffix inputClass="govuk-input--width-4" />

        <@fdsRadio.radioGroup path="form.approvedFieldDevelopmentPlan" labelText="Do you have an approved Field Development Plan (FDP)?" hiddenContent=true>
        <@fdsRadio.radioYes path="form.approvedFieldDevelopmentPlan">
          <@fdsDateInput.dateInput
              dayPath="form.approvedFdpDate.day"
              monthPath="form.approvedFdpDate.month"
              yearPath="form.approvedFdpDate.year"
              labelText="What is the FDP approval date?"
              formId="approvedFdpDate-day-month-year"
              nestingPath="form.approvedFieldDevelopmentPlan"
          />
        </@fdsRadio.radioYes>
        <@fdsRadio.radioNo path="form.approvedFieldDevelopmentPlan"/>
      </@fdsRadio.radioGroup>

      <@fdsRadio.radioGroup path="form.approvedDecomProgram" labelText="Do you have an approved Decommissioning Program (DP)?" hiddenContent=true>
        <@fdsRadio.radioYes path="form.approvedDecomProgram">
          <@fdsDateInput.dateInput
            dayPath="form.approvedDecomProgramDate.day"
            monthPath="form.approvedDecomProgramDate.month"
            yearPath="form.approvedDecomProgramDate.year"
            labelText="What is the DP approval date?"
            formId="approvedDecomProgramDate-day-month-year"
            nestingPath="form.approvedDecomProgram"
          />
        </@fdsRadio.radioYes>
        <@fdsRadio.radioNo path="form.approvedDecomProgram"/>
      </@fdsRadio.radioGroup>
      <@fdsSelect.select path="form.ukcsArea" labelText="What UKCS area is this project located in?" options=ukcsAreaMap/>
      <@fdsAddToList.addToList
        path="form.licenceBlocks"
        alreadyAdded=alreadyAddedBlocks
        title="Licence blocks"
        itemName="Licence block"
        noItemText="No licence blocks added"
        addToListId="licence-block-table"
        selectorLabelText="Which licence blocks is this project located in?"
        restUrl=springUrl(blocksRestUrl)
      />
      <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
    </@fdsForm.htmlForm>
</@defaultPage>