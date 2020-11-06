<#include '../../layout.ftl'>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsForm.htmlForm>
    <@fdsSearchSelector.searchSelectorRest
      path="form.structure"
      selectorMinInputLength=1
      labelText="What host structure does this subsea infrastructure relate to?"
      restUrl=springUrl(facilitiesRestUrl)
      preselectedItems=preSelectedFacilityMap!{}
    />
    <@fdsTextarea.textarea path="form.description" labelText="Provide a description of the structure"/>
    <@fdsSelect.select path="form.status" labelText="Structure status" options=infrastructureStatuses/>
    <@fdsRadio.radioGroup
      labelText="Type of subsea infrastructure?"
      path="form.infrastructureType"
      hiddenContent=true
    >
      <@fdsRadio.radioItem path="form.infrastructureType" itemMap=concreteMattressInfrastructureType isFirstItem=true>
        <@_concreteMattressesForm path="form.concreteMattressForm" nestingPath="form.infrastructureType"/>
      </@fdsRadio.radioItem>
      <@fdsRadio.radioItem path="form.infrastructureType" itemMap=subseaInfrastructureType isFirstItem=false>
        <@_subseaStructureForm path="form.subseaStructureForm" nestingPath="form.infrastructureType"/>
      </@fdsRadio.radioItem>
      <@fdsRadio.radioItem path="form.infrastructureType" itemMap=otherInfrastructureType isFirstItem=false>
        <@_otherStructureForm path="form.otherSubseaStructureForm" nestingPath="form.infrastructureType"/>
      </@fdsRadio.radioItem>
    </@fdsRadio.radioGroup>
    <@minMaxDate.minMaxDateInput
      minFormPath="form.decommissioningDate.minYear"
      maxFormPath="form.decommissioningDate.maxYear"
      labelText="Provide the period in which the decommissioning is expected to take place"
      altMinLabel="Earliest start year"
      altMaxLabel="Latest completion year"
      formId="decommissioning-date-min-max"
    />
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>

<#macro _concreteMattressesForm path nestingPath>
  <@fdsTextInput.textInput
    path="${path}.numberOfMattresses"
    labelText="Number of mattresses to decommission"
    nestingPath=nestingPath
    inputClass="govuk-input--width-4"
  />
  <@_totalEstimatedMassInput
    path="${path}.totalEstimatedMattressMass"
    nestingPath=nestingPath
    unit=metricTonneUnit
  />
</#macro>

<#macro _subseaStructureForm path nestingPath>
  <@fdsRadio.radio
    path="${path}.totalEstimatedSubseaMass"
    labelText="Total estimated mass"
    radioItems=subseaStructureMasses
    nestingPath=nestingPath
  />
</#macro>

<#macro _otherStructureForm path nestingPath>
  <@fdsTextInput.textInput
    path="${path}.typeOfStructure"
    labelText="Type of subsea structure being decommissioned"
    nestingPath=nestingPath
  />
  <@_totalEstimatedMassInput
    path="${path}.totalEstimatedMass"
    nestingPath=nestingPath
    unit=metricTonneUnit
  />
</#macro>

<#macro _totalEstimatedMassInput path nestingPath unit labelText="Total estimated mass" inputClass="govuk-input--width-4">
  <@fdsTextInput.textInput
    path=path
    labelText=labelText
    nestingPath=nestingPath
    suffix=unit.plural
    suffixScreenReaderPrompt=unit.screenReaderSuffix
    inputClass=inputClass
  />
</#macro>