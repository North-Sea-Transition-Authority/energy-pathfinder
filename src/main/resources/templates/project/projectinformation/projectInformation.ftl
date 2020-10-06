<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Project information" pageHeading="Project information" breadcrumbs=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsForm.htmlForm>

    <@fdsTextInput.textInput path="form.projectTitle" labelText="What is the project title?"/>
    <@fdsTextarea.textarea path="form.projectSummary" labelText="Provide a summary of the project"  />
    <@fdsDetails.summaryDetails summaryTitle="What should I provide for the summary of the project?">
      <p class="govuk-body">Provide a description of the project and future plans and include the following where appropriate:</p>
      <ul class="govuk-list govuk-list--bullet">
        <li>location in UKCS and type of structure, for example: steel platform, FPSO, Subsea manifold</li>
        <li>current stage of project, for example: Pre-Feed, COP</li>
        <li>timing / schedule for work, for example: dates for FID, FDP approval, COP, Decommissioning Programme approval</li>
      </ul>
    </@fdsDetails.summaryDetails>

    <@fdsRadio.radioGroup
      labelText="What is the field stage?"
      path="form.fieldStage"
      hiddenContent=true
    >
      <@fdsRadio.radioItem path="form.fieldStage" itemMap=discoveryFieldStage isFirstItem=true>
        <@firstProductionDate path="form.discoveryFirstProductionDate" nestingPath="form.fieldStage"/>
      </@fdsRadio.radioItem>
      <@fdsRadio.radioItem path="form.fieldStage" itemMap=developmentFieldStage>
        <@firstProductionDate path="form.developmentFirstProductionDate" nestingPath="form.fieldStage"/>
      </@fdsRadio.radioItem>
      <@fdsRadio.radioItem path="form.fieldStage" itemMap=operationsFieldStage/>
      <@fdsRadio.radioItem path="form.fieldStage" itemMap=decommissioningFieldStage>
        <@quarterYear.standardQuarterYearInput
          quarterYearInputPath="form.decomWorkStartDate"
          legendHeading="What is the decommissioning work start date?"
          quarterOptions=quarters
          formId="decomWorkStartDate"
          hintText="If you don’t know the exact date, provide an estimated date"
          nestingPath="form.fieldStage"
        />
        <@fdsDateInput.dateInput
          dayPath="form.productionCessationDate.day"
          monthPath="form.productionCessationDate.month"
          yearPath="form.productionCessationDate.year"
          labelText="What is the production cessation date?"
          formId="productionCessationDate-day-month-year"
          nestingPath="form.fieldStage"
          optionalLabel=true
        />
      </@fdsRadio.radioItem>
      <@fdsRadio.radioItem path="form.fieldStage" itemMap=energyTransitionFieldStage/>
    </@fdsRadio.radioGroup>

    <@contactDetails.standardContactDetails path="form.contactDetail"/>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>

<#macro firstProductionDate path nestingPath>
  <@quarterYear.standardQuarterYearInput
    quarterYearInputPath=path
    legendHeading="What is the first production date?"
    quarterOptions=quarters
    formId="firstProductionDate"
    hintText="If you do not know the exact date, provide an estimated date"
    nestingPath=nestingPath
  />
</#macro>