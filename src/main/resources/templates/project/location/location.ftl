<#include '../../layout.ftl'>
<#import '../../macros/coordinateinput/coordinateInput.ftl' as coordinateInput/>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->
<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->

<@defaultPage htmlTitle="Location" pageHeading="Location" breadcrumbs=true errorItems=errorList>
  <@fdsForm.htmlForm>
    <@coordinateInput.latitudeInput
      degreesPath="form.centreOfInterestLatitude.degreesInput.inputValue"
      minutesPath="form.centreOfInterestLatitude.minutesInput.inputValue"
      secondsPath="form.centreOfInterestLatitude.secondsInput.inputValue"
      hemispherePath="form.centreOfInterestLatitude.hemisphereInput.inputValue"
      formId="latitude"
      labelText="Centre of interest latitude in WGS84"
    />

    <@coordinateInput.longitudeInput
      degreesPath="form.centreOfInterestLongitude.degreesInput.inputValue"
      minutesPath="form.centreOfInterestLongitude.minutesInput.inputValue"
      secondsPath="form.centreOfInterestLongitude.secondsInput.inputValue"
      hemispherePath="form.centreOfInterestLongitude.hemisphereInput.inputValue"
      hemisphereList=longitudeHemispheres
      formId="longitude"
      labelText="Centre of interest longitude in WGS84"
    />

    <#if isOilAndGasProject>
      <@fdsSearchSelector.searchSelectorRest path="form.field" selectorMinInputLength=3  labelText="Which field is the project related to?" restUrl=springUrl(fieldsRestUrl)  preselectedItems=preselectedField!{} />
      <@fdsSelect.select path="form.fieldType" labelText="Field type" options=fieldTypeMap/>
      <@fdsTextInput.textInput
        path="form.maximumWaterDepth"
        labelText="What is the maximum water depth?"
        suffix=waterDepthUnit.plural
        suffixScreenReaderPrompt=waterDepthUnit.screenReaderSuffix
        inputClass="govuk-input--width-4"
      />

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

      <@fdsRadio.radioGroup path="form.approvedDecomProgram" labelText="Do you have an approved Decommissioning Programme (DP)?" hiddenContent=true>
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
      <h2 class="govuk-heading-m">Licence blocks</h2>
      <@fdsAddToList.addToList
        pathForList="form.licenceBlocks"
        pathForSelector="form.licenceBlocksSelect"
        alreadyAdded=alreadyAddedBlocks
        itemName="Licence block"
        noItemText="No licence blocks added"
        invalidItemText="This licence block is invalid"
        addToListId="licence-block-table"
        selectorLabelText="Add a licence block (optional)"
        selectorHintText="For example 44/15. You can add more than one block. Leave this blank if your project is not within a licensed area"
        restUrl=springUrl(blocksRestUrl)
      />
      <@fdsDetails.summaryDetails summaryTitle="Why is a block not allowed on my project?">
        <p class="govuk-body">
          You can only include blocks on your project that exist on a production licence. If a block has been wholly
          or partially relinquished since it was added to your ${service.serviceName} project it will need to be removed
        </p>
      </@fdsDetails.summaryDetails>
    </#if>
    <@fdsAction.submitButtons
      primaryButtonText="Save and complete"
      secondaryButtonText="Save and complete later"
      disableOnSubmit=true
    />
  </@fdsForm.htmlForm>
</@defaultPage>
