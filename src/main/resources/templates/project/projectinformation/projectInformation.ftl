<#include '../../layout.ftl'>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->

<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true errorItems=errorList>
  <@fdsForm.htmlForm>
    <@fdsTextInput.textInput path="form.projectTitle" labelText="What is the project title?"/>
    <@fdsTextarea.textarea path="form.projectSummary" labelText="Provide a summary of the project"  />
    <@fdsDetails.summaryDetails summaryTitle="What should I provide for the summary of the project?">
      <p class="govuk-body">Provide a description of the project and future plans and include the following where appropriate:</p>
      <ul class="govuk-list govuk-list--bullet">
        <li>location in UKCS and high level scope/infrastructure summary</li>
        <li>current stage of the project, for example: Pre-Feed, decommissioning</li>
        <li>
          current view of the timing/schedule for upcoming work, including key constraints, for example:
          <ul class="govuk-list govuk-list--bullet">
            <li>estimated year when the platform will go cold following well decommissioning</li>
            <li>market engagement plan, for example when will tenders come to the market</li>
          </ul>
        </li>
        <li>receptiveness to campaign/aggregation proposals</li>
      </ul>
    </@fdsDetails.summaryDetails>

    <@fdsRadio.radioGroup
      labelText="What is the field stage?"
      path="form.fieldStage"
      hiddenContent=true
    >
      <@fdsRadio.radioItem path="form.fieldStage" itemMap=discoveryFieldStage itemHintText=discoveryFieldStageDescription isFirstItem=true/>
      <@fdsRadio.radioItem path="form.fieldStage" itemMap=developmentFieldStage itemHintText=developmentFieldStageDescription>
        <@firstProductionDate path="form.developmentFirstProductionDate" nestingPath="form.fieldStage"/>
      </@fdsRadio.radioItem>
      <@fdsRadio.radioItem path="form.fieldStage" itemMap=decommissioningFieldStage itemHintText=decommissioningFieldStageDescription/>
      <@fdsRadio.radioItem path="form.fieldStage" itemMap=energyTransitionFieldStage itemHintText=energyTransitionFieldStageDescription>
        <@fdsRadio.radio
          path="form.energyTransitionCategory"
          labelText="Energy transition category"
          radioItems=energyTransitionCategories
          nestingPath="form.fieldStage"
        />
      </@fdsRadio.radioItem>
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