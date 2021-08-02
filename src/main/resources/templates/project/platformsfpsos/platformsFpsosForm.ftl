<#include '../../layout.ftl'>
<#include '../macros/minMaxDateInput.ftl'>
<#import './_terminology.ftl' as terminology>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->
<#-- @ftlvariable name="platformInfrastructureType" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="fpsoInfrastructureType" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="facilitiesUrl" type="String" -->
<#-- @ftlvariable name="preselectedPlatformStructure" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="preselectedFpsoStructure" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="mtUnit" type="uk.co.ogauthority.pathfinder.model.enums.MeasurementUnits" -->
<#-- @ftlvariable name="substructureRemovalPremiseMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="futurePlansMap" type="java.util.Map<String, String>" -->

<#assign platformInitCapped = terminology.terminology['platformInitCapped'] />
<#assign platformLowerCase = terminology.terminology['platformLowerCase'] />
<#assign floatingUnitLowerCase = terminology.terminology['floatingUnitLowerCase'] />
<#assign floatingUnitInitCapped = terminology.terminology['floatingUnitInitCapped'] />

<#assign pageTitle = "${platformInitCapped} or ${floatingUnitLowerCase}" />

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true errorItems=errorList>
  <@fdsForm.htmlForm>
    <@fdsRadio.radioGroup
      labelText="Are you decommissioning a ${platformLowerCase} or ${floatingUnitLowerCase}?"
      path="form.infrastructureType"
      hiddenContent=true
    >
      <@fdsRadio.radioItem path="form.infrastructureType" itemMap=platformInfrastructureType isFirstItem=true>
        <@fdsSearchSelector.searchSelectorRest
          path="form.platformStructure"
          selectorMinInputLength=3
          labelText="Which ${platformLowerCase} is being decommissioned?"
          restUrl=springUrl(facilitiesUrl)
          preselectedItems=preselectedPlatformStructure!{}
          nestingPath="form.infrastructureType"
        />
      </@fdsRadio.radioItem>
      <@fdsRadio.radioItem path="form.infrastructureType" itemMap=fpsoInfrastructureType isFirstItem=false>
        <@fdsSearchSelector.searchSelectorRest
          path="form.fpsoStructure"
          selectorMinInputLength=3
          labelText="Which ${floatingUnitLowerCase} is being decommissioned?"
          restUrl=springUrl(facilitiesUrl)
          preselectedItems=preselectedFpsoStructure!{}
          nestingPath="form.infrastructureType"
        />
        <@fdsTextInput.textInput
          path="form.fpsoType"
          labelText="${floatingUnitInitCapped} type"
          hintText="For example, Tanker or Sevan"
          nestingPath="form.infrastructureType"
        />
        <@fdsTextarea.textarea
          path="form.fpsoDimensions"
          labelText="${floatingUnitInitCapped} dimensions"
          hintText="Provide the approximate maximum overall length, breadth and height, and the minimum/maximum draught in metres. This information is useful in identifying suitable decommissioning yards"
          nestingPath="form.infrastructureType"
        />
        <@fdsRadio.radioGroup
          path="form.substructureExpectedToBeRemoved"
          labelText="Is substructure removal expected to be within scope?"
          hintText="Substructure refers to anchor chains and anchor suction cans"
          hiddenContent=true
          nestingPath="form.infrastructureType"
        >
          <@fdsRadio.radioYes path="form.substructureExpectedToBeRemoved">
            <@fdsRadio.radio
              path="form.substructureRemovalPremise"
              nestingPath="form.substructureExpectedToBeRemoved"
              labelText="Substructure removal premise"
              radioItems=substructureRemovalPremiseMap
            />
            <@fdsTextInput.textInput
              path="form.substructureRemovalMass"
              nestingPath="form.substructureExpectedToBeRemoved"
              labelText="Estimated substructure removal mass"
              suffix=mtUnit.plural
              suffixScreenReaderPrompt=mtUnit.screenReaderSuffix
              inputClass="govuk-input--width-4"
            />
            <@minMaxDateInput
              minFormPath="form.substructureRemovalYears.minYear"
              maxFormPath="form.substructureRemovalYears.maxYear"
              labelText="Provide the period over which the substructure removal takes place"
              altMinLabel="Earliest start year"
              altMaxLabel="Latest completion year"
              formId="substructure-removal"
            />
          </@fdsRadio.radioYes>
          <@fdsRadio.radioNo path="form.substructureExpectedToBeRemoved"/>
        </@fdsRadio.radioGroup>
      </@fdsRadio.radioItem>
    </@fdsRadio.radioGroup>

    <@fdsTextInput.textInput
      path="form.topsideFpsoMass"
      labelText="Topsides/${floatingUnitLowerCase} mass"
      suffix=mtUnit.plural
      suffixScreenReaderPrompt=mtUnit.screenReaderSuffix
      inputClass="govuk-input--width-4"
    />
    <@minMaxDateInput
      minFormPath="form.topsideRemovalYears.minYear"
      maxFormPath="form.topsideRemovalYears.maxYear"
      labelText="Provide the period in which the topsides/${floatingUnitLowerCase} removal is expected to take place"
      altMinLabel="Earliest start year"
      altMaxLabel="Latest completion year"
      formId="topsides-fpso-removal"
    />
    <@fdsRadio.radio path="form.futurePlans" labelText="Future plans" radioItems=futurePlansMap/>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>