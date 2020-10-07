<#include '../../layout.ftl'/>

<#macro minMaxDateInput minFormPath maxFormPath nestedPath="" labelText="" altMinLabel="Minimum" altMaxLabel="Maximum">
  <@fdsNumberInput.twoNumberInputs pathOne=minFormPath pathTwo=maxFormPath formId="min-max-values" nestingPath=nestedPath labelText=labelText>
    <@fdsNumberInput.numberInputItem path=minFormPath labelText=altMinLabel inputClass="govuk-input--width-4"/>
    <@fdsNumberInput.numberInputItem path=maxFormPath labelText=altMaxLabel inputClass="govuk-input--width-4"/>
  </@fdsNumberInput.twoNumberInputs>
</#macro>