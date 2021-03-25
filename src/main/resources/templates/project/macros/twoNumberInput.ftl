<#include '../../layout.ftl'/>

<#--
  This is a temporary template to solve an error binding issue in the numberInput.ftl template
  Ideally it's usages will be swapped with calls to the updated FDS template
-->
<#macro twoNumberInputs
  pathOne
  pathTwo
  formId
  labelText
  pathOneLabelText
  pathTwoLabelText
  numberInputLabelClass="govuk-input--width-4"
  hintText=""
  optionalLabel=false
  nestingPath=""
  fieldsetHeadingSize="h2"
  fieldsetHeadingClass="govuk-fieldset__legend--s"
  formGroupClass=""
  caption=""
  captionClass="govuk-caption-s"
  showLabelOnly=false
  noFieldsetHeadingSize="--s">

  <@spring.bind pathOne/>
  <#local hasErrorOne=(spring.status.errorMessages?size > 0)>
  <@spring.bind pathTwo/>
  <#local hasErrorTwo=(spring.status.errorMessages?size > 0)>
  <#local hasError=hasErrorOne || hasErrorTwo>

  <div class="govuk-form-group ${formGroupClass} <#if hasError>govuk-form-group--error</#if>">
    <@fdsFieldset.fieldset
      legendHeading=labelText
      legendHeadingSize=fieldsetHeadingSize
      legendHeadingClass=fieldsetHeadingClass
      caption=caption
      captionClass=captionClass
      optionalLabel=optionalLabel
      hintText=hintText
      showHeadingOnly=showLabelOnly
      noFieldsetHeadingSize=noFieldsetHeadingSize>

    <#if hasError>
      <@fdsError.inputError inputId="${formId}-1"/>
      <#-- Re-bind to our first path to set out any relevant errors -->
      <@spring.bind pathOne/>
      <@fdsError.inputError inputId="${formId}-2"/>
    </#if>
    <div class="govuk-date-input" id="${formId}-number-input">
        <@numberInputItem path=pathOne labelText=pathOneLabelText formId=formId inputClass=numberInputLabelClass/>
        <@numberInputItem path=pathTwo labelText=pathTwoLabelText formId=formId inputClass=numberInputLabelClass/>
    </div>
    </@fdsFieldset.fieldset>
  </div>
  <#--Rebind your form when a component is used inside show/hide radio groups-->
  <#if nestingPath?has_content>
      <@spring.bind nestingPath/>
  </#if>
</#macro>

<#macro numberInputItem
  path
  labelText
  formId
  suffix=""
  suffixScreenReaderPrompt=""
  inputClass="govuk-input--width-2">

  <@spring.bind path/>

  <#local id=fdsUtil.sanitiseId(spring.status.expression)>
  <#local name=spring.status.expression>
  <#local value=spring.stringStatusValue>
  <#local hasError=(spring.status.errorMessages?size > 0)>

  <div class="govuk-date-input__item">
    <div class="govuk-form-group">
      <label class="govuk-label govuk-date-input__label" for="${id}">
        ${labelText}
        <#if suffixScreenReaderPrompt?has_content>
          <span class="govuk-visually-hidden">${suffixScreenReaderPrompt}</span>
        </#if>
      </label>
      <input class="govuk-input <#if hasError>govuk-input--error</#if> govuk-date-input__input ${inputClass}" id="${id}" name="${name}" type="text" value="${value}">
        <#if suffix?has_content>
          <span id="${id}-suffix" class="govuk-input__suffix">${suffix}</span>
        </#if>
    </div>
  </div>
</#macro>