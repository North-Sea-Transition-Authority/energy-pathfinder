<#include '../../layout.ftl'>
<#include '../macros/minMaxDateInput.ftl'>

<@defaultPage htmlTitle="Platform or FPSO" pageHeading="Platform or FPSO" breadcrumbs=true >
    <#if errorList?has_content>
        <@fdsError.errorSummary errorItems=errorList />
    </#if>

    <@fdsForm.htmlForm>
      <@fdsSearchSelector.searchSelectorRest path="form.structure" selectorMinInputLength=3  labelText="Structure" restUrl=springUrl(facilitiesUrl)  preselectedItems=preselectedStructure!{} />
      <@fdsTextInput.textInput path="form.topsideFpsoMass" labelText="Topside / FPSO mass  " suffix=mtUnit.plural suffixScreenReaderPrompt=mtUnit.screenReaderSuffix inputClass="govuk-input--width-4" />
      <@minMaxDateInput
        minFormPath="form.topsideRemovalYears.minYear"
        maxFormPath="form.topsideRemovalYears.maxYear"
        labelText="FPSO / topsides removal"
        altMinLabel="Earliest"
        altMaxLabel="Latest"
        formId="topsides-fpso-removal"
      />

      <@fdsRadio.radioGroup path="form.substructureExpectedToBeRemoved" labelText="Are substructures expected to be removed?" hiddenContent=true>
        <@fdsRadio.radioYes path="form.substructureExpectedToBeRemoved">
          <@fdsRadio.radio path="form.substructureRemovalPremise" nestingPath="form.substructureExpectedToBeRemoved" labelText="Substructure removal premise" radioItems=substructureRemovalPremiseMap/>
          <@fdsTextInput.textInput path="form.substructureRemovalMass" nestingPath="form.substructureExpectedToBeRemoved" labelText="Substructure removal mass  " suffix=mtUnit.plural suffixScreenReaderPrompt=mtUnit.screenReaderSuffix inputClass="govuk-input--width-4" />
          <@minMaxDateInput
            minFormPath="form.substructureRemovalYears.minYear"
            maxFormPath="form.substructureRemovalYears.maxYear"
            labelText="Substructure removal"
            altMinLabel="Earliest"
            altMaxLabel="Latest"
            formId="substructure-removal"
          />
        </@fdsRadio.radioYes>
        <@fdsRadio.radioNo path="form.substructureExpectedToBeRemoved"/>
      </@fdsRadio.radioGroup>
      <@fdsTextInput.textInput path="form.fpsoType" labelText="FPSO type"/>
      <@fdsTextarea.textarea path="form.fpsoDimensions" labelText="FPSO dimensions"  />
      <@fdsRadio.radio path="form.futurePlans" labelText="Future plans" radioItems=futurePlansMap/>
      <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
    </@fdsForm.htmlForm>
</@defaultPage>