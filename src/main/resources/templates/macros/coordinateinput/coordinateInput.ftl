<#import '/spring.ftl' as spring>
<#include '../../layout.ftl'>
<#import '../../fds/utilities/utilities.ftl' as fdsUtil>

<#macro latitudeInput
  degreesPath
  minutesPath
  secondsPath
  hemispherePath
  formId
  labelText=""
  hintText="From 45 to 64 for degrees, 0 to 59 for minutes, and 0 to 59.999 for seconds"
>
  <@_coordinateInput
    type="LATITUDE"
    degreesPath=degreesPath
    minutesPath=minutesPath
    secondsPath=secondsPath
    hemispherePath=hemispherePath
    formId=formId
    labelText=labelText
    hintText=hintText
  />
</#macro>

<#macro longitudeInput
  degreesPath
  minutesPath
  secondsPath
  hemispherePath
  hemisphereList
  formId
  labelText=""
  hintText="From 0 to 30 for degrees, 0 to 59 for minutes, and 0 to 59.999 for seconds"
>
  <@_coordinateInput
    type="LONGITUDE"
    degreesPath=degreesPath
    minutesPath=minutesPath
    secondsPath=secondsPath
    hemispherePath=hemispherePath
    formId=formId
    hemisphereList=hemisphereList
    labelText=labelText
    hintText=hintText
  />
</#macro>

<#macro _coordinateInput
  type
  degreesPath
  minutesPath
  secondsPath
  hemispherePath
  formId
  hemisphereList=[]
  labelText=""
  hintText=""
>
  <@spring.bind degreesPath/>
  <#local hasErrorDegrees=fdsUtil.hasSpringStatusErrors() && spring.status.errorMessages[0]?has_content>
  <#assign degreesError>
    <@fdsError.inputError inputId="${formId}-degrees"/>
  </#assign>

  <@spring.bind minutesPath/>
  <#local hasErrorMinutes=fdsUtil.hasSpringStatusErrors() && spring.status.errorMessages[0]?has_content>
  <#assign minutesError>
    <@fdsError.inputError inputId="${formId}-minutes"/>
  </#assign>

  <@spring.bind secondsPath/>
  <#local hasErrorSeconds=fdsUtil.hasSpringStatusErrors() && spring.status.errorMessages[0]?has_content>
  <#assign secondsError>
    <@fdsError.inputError inputId="${formId}-seconds"/>
  </#assign>

  <@spring.bind hemispherePath/>
  <#local hemisphereInputName=fdsUtil.getSpringStatusExpression()>
  <#local hasErrorHemisphere=fdsUtil.hasSpringStatusErrors()>
  <#assign hemisphereError>
    <@fdsError.inputError inputId="${formId}-hemisphere"/>
  </#assign>

  <#local hasError=hasErrorDegrees || hasErrorMinutes || hasErrorSeconds || hasErrorHemisphere>

  <div class="govuk-form-group <#if hasError>govuk-form-group--error</#if>">
    <@fdsFieldset.fieldset legendHeading="${labelText}" legendHeadingSize="h3" legendHeadingClass="govuk-fieldset__legend--s" hintText=hintText>
      <#if hasError>
        <#if hasErrorDegrees>
          ${degreesError}
        <#elseif hasErrorMinutes>
          ${minutesError}
        <#elseif hasErrorSeconds>
          ${secondsError}
        <#elseif hasErrorHemisphere>
          ${hemisphereError}
        </#if>
      </#if>
      <div class="govuk-date-input">
        <@fdsNumberInput.numberInputItem path=degreesPath labelText="Degrees" inputClass="govuk-input--width-2"/>
        <@fdsNumberInput.numberInputItem path=minutesPath labelText="Minutes" inputClass="govuk-input--width-2"/>
        <@fdsNumberInput.numberInputItem path=secondsPath labelText="Seconds" inputClass="govuk-input--width-4"/>
        <div class="govuk-date-input__item">
          <div class="govuk-form-group">
            <#if type == "LATITUDE">
              <div class="govuk-date-input__item">
                <div class="govuk-form-group">
                  <label class="govuk-label govuk-date-input__label" for="${formId}-hemisphere-north">
                    Hemisphere
                  </label>
                  <input
                    class="govuk-input <#if hasError>govuk-input--error</#if> govuk-date-input__input govuk-input--width-3 govuk-input--read-only"
                    style="opacity: 1;"
                    id="${formId}-hemisphere-north"
                    name="hemisphere-north"
                    type="text"
                    value="North"
                    disabled
                  >
                </div>
              </div>
            <#else>
              <@_coordinateSelect path=hemispherePath options=hemisphereList labelText="Hemisphere"/>
            </#if>
          </div>
        </div>
      </div>
    </@fdsFieldset.fieldset>
  </div>
</#macro>

<#--Custom select component, removes error messages bound to the form group wrapping the select.-->
<#macro _coordinateSelect
  path
  options
  labelText
>
  <@spring.bind path/>

  <#local id=fdsUtil.sanitiseId(spring.status.expression)>
  <#local name=fdsUtil.getSpringStatusExpression()>
  <#local value=fdsUtil.getSpringStatusValue()>
  <#local hasError=fdsUtil.hasSpringStatusErrors()>
  <#local ariaDescribedByValue=fdsUtil.getAriaDescribedByAttr(false, hasError, id)>

  <div class="govuk-form-group">
    <label class="govuk-label" for="${id}">
      ${labelText}
    </label>
    <select
      class="govuk-select<#if hasError> govuk-select--error</#if>"
      id="${id}"
      name="${name}"
      <#if ariaDescribedByValue?has_content>aria-describedby="${ariaDescribedByValue}"</#if>
    >
      <option value="" selected disabled>Select one...</option>
      <#list options?keys as option>
        <#assign isSelected = value == option>
        <option value="${option}" <#if isSelected>selected</#if>>${options[option]}</option>
      </#list>
    </select>
  </div>
</#macro>
