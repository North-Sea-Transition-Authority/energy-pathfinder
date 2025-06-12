<#include '../../layout.ftl'>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->

<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true errorItems=errorList>
  <@fdsForm.htmlForm>
    <@fdsTextInput.textInput path="form.projectTitle" labelText="What is the project title?"/>
    <@fdsTextarea.textarea path="form.projectSummary" labelText="Provide a summary of the project"  />
    <@fdsDetails.summaryDetails summaryTitle="What should I provide for the summary of the project?">
      <p class="govuk-body">Provide a description of the project and future plans. Things you may want to include:</p>
      <ul class="govuk-list govuk-list--bullet">
        <li>high level scope/infrastructure summary/construction phase</li>
        <li>specific details of contract opportunities (to avoid unnecessary enquiries from unqualified suppliers)</li>
        <li>current stage of the project</li>
        <li>estimated timing/schedule for upcoming work</li>
        <li>number of turbines & install capacity in megawatts</li>
        <li>estimated year when the platform will go cold following well decommissioning</li>
      </ul>
    </@fdsDetails.summaryDetails>

    <@fdsRadio.radioGroup
      labelText="What is the energy project?"
      path="form.fieldStage"
      hiddenContent=true
    >
      <@fdsRadio.radioItem path="form.fieldStage" itemMap=carbonCaptureAndStorageFieldStage>
        <@fdsRadio.radioGroup
          labelText="Carbon Capture and Storage (CCS) category"
          path="form.carbonCaptureSubCategory"
          hiddenContent=false
          nestingPath="form.fieldStage">
            <@fdsRadio.radioItem
              path="form.carbonCaptureSubCategory"
              itemMap=carbonAndOnshoreCategory
              itemHintText=carbonAndOnshoreDescription
              isFirstItem=true
            />
            <@fdsRadio.radioItem
              path="form.carbonCaptureSubCategory"
              itemMap=transportationAndStorageCategory
              itemHintText=transportationAndStorageDescription
            />
        </@fdsRadio.radioGroup>
      </@fdsRadio.radioItem>
      <@fdsRadio.radioItem path="form.fieldStage" itemMap=electrificationFieldStage>
        <@fdsRadio.radioGroup
          labelText="Electrification category"
          path="form.electrificationSubCategory"
          hiddenContent=false
          nestingPath="form.fieldStage"
        >
          <@fdsRadio.radioItem
            path="form.electrificationSubCategory"
            itemMap=offshoreElectrificationCategory
            isFirstItem=true
          />
          <@fdsRadio.radioItem
            path="form.electrificationSubCategory"
            itemMap=onshoreElectrificationCategory
          />
        </@fdsRadio.radioGroup>
      </@fdsRadio.radioItem>
      <@fdsRadio.radioItem path="form.fieldStage" itemMap=hydrogenFieldStage>
        <@fdsRadio.radioGroup
          labelText="Hydrogen category"
          path="form.hydrogenSubCategory"
          hiddenContent=false
          nestingPath="form.fieldStage"
        >
          <@fdsRadio.radioItem
            path="form.hydrogenSubCategory"
            itemMap=offshoreHydrogenCategory
            isFirstItem=true
          />
          <@fdsRadio.radioItem
            path="form.hydrogenSubCategory"
            itemMap=onshoreHydrogenCategory
          />
        </@fdsRadio.radioGroup>
      </@fdsRadio.radioItem>
      <@fdsRadio.radioItem path="form.fieldStage" itemMap=oilAndGasFieldStage>
        <@fdsRadio.radioGroup
          labelText="Oil and Gas category"
          path="form.oilAndGasSubCategory"
          hiddenContent=true
          nestingPath="form.fieldStage">
          <@fdsRadio.radioItem
            path="form.oilAndGasSubCategory"
            itemMap=discoveryCategory
            itemHintText=discoveryCategoryDescription
            isFirstItem=true/>
          <@fdsRadio.radioItem
            path="form.oilAndGasSubCategory"
            itemMap=developmentCategory
            itemHintText=developmentCategoryDescription>
            <@firstProductionDate path="form.developmentFirstProductionDate" nestingPath="form.oilAndGasSubCategory"/>
          </@fdsRadio.radioItem>
          <@fdsRadio.radioItem
            path="form.oilAndGasSubCategory"
            itemMap=decommissioningCategory
            itemHintText=decommissioningCategoryDescription/>
        </@fdsRadio.radioGroup>
      </@fdsRadio.radioItem>
      <@fdsRadio.radioItem path="form.fieldStage" itemMap=windEnergyFieldStage>
        <@fdsRadio.radioGroup
          labelText="Wind energy category"
          path="form.windEnergySubCategory"
          hiddenContent=false
          nestingPath="form.fieldStage">
            <@fdsRadio.radioItem
              path="form.windEnergySubCategory"
              itemMap=fixedBottomOffshoreWindCategory
              isFirstItem=true
            />
            <@fdsRadio.radioItem
              path="form.windEnergySubCategory"
              itemMap=floatingOffshoreWindCategory
            />
            <@fdsRadio.radioItem
              path="form.windEnergySubCategory"
              itemMap=onshoreWindCategory
            />
        </@fdsRadio.radioGroup>
      </@fdsRadio.radioItem>

    </@fdsRadio.radioGroup>

    <@contactDetails.standardContactDetails path="form.contactDetail"
      hintText="For example, project manager, procurement manager etc"/>
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
