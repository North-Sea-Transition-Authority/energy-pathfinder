<#include '../../layout.ftl'>

<#macro
  standardQuarterYearInput
  quarterYearInputPath
  legendHeading
  quarterOptions
  formId
  hintText=""
  fieldsetHeadingClass="govuk-fieldset__legend--s"
  nestingPath=""
  optionalLabel=false
  optionalInputDefault=""
>
  <@customQuarterYearInput
    quarterPath="${quarterYearInputPath + '.quarter'}"
    yearPath="${quarterYearInputPath + '.year'}"
    legendHeading=legendHeading
    quarterOptions=quarterOptions
    formId=formId
    hintText=hintText
    fieldsetHeadingClass=fieldsetHeadingClass
    nestingPath=nestingPath
    optionalLabel=optionalLabel
    optionalInputDefault=optionalInputDefault
  />
</#macro>

<#macro
  customQuarterYearInput
  quarterPath
  yearPath
  legendHeading
  quarterOptions
  formId
  legendHeadingSize="h2"
  quarterLabelText="Quarter"
  yearLabelText="Year"
  fieldsetHeadingClass="govuk-fieldset__legend--s"
  hintText=""
  formGroupClass=""
  nestingPath=""
  optionalLabel=false
  optionalInputDefault=""
>
  <@spring.bind quarterPath/>
  <#local quarterPathHasError=(spring.status.errorMessages?size > 0)>
  <@spring.bind yearPath/>
  <#local yearPathHasError=(spring.status.errorMessages?size > 0)>
  <#local hasError=quarterPathHasError || yearPathHasError>

  <div class="govuk-form-group ${formGroupClass}<#if hasError> govuk-form-group--error</#if>">
    <@fdsFieldset.fieldset
      legendHeading=legendHeading
      legendHeadingSize=legendHeadingSize
      hintText=hintText
      legendHeadingClass=fieldsetHeadingClass
      nestingPath=nestingPath
    >
      <@spring.bind quarterPath/>
      <#local quarterId=spring.status.expression?replace('[','')?replace(']','')>
      <#local quarterName=spring.status.expression>
      <#local quarterValue=spring.stringStatusValue>

      <#if hasError>
        <@fdsError.inputError inputId="${formId}"/>
      </#if>

      <div class="govuk-date-input">

        <div class="govuk-date-input__item">
          <div class="govuk-form-group">
            <label class="govuk-label" for="${quarterId}">
              ${quarterLabelText} <#if optionalLabel>(optional)</#if>
            </label>
            <select class="govuk-select <#if hasError> govuk-select--error</#if>" id="${quarterId}" name="${quarterName}">
              <#if optionalInputDefault?has_content>
                <option value="" selected>${optionalInputDefault}</option>
              <#else>
                <option value="" selected disabled>Select one...</option>
              </#if>
              <#list quarterOptions?keys as option>
                  <#assign isSelected = quarterValue == option>
                <option value="${option}" <#if isSelected>selected</#if>>${quarterOptions[option]}</option>
              </#list>
            </select>
          </div>
        </div>

        <@spring.bind yearPath/>
        <#local yearId=spring.status.expression?replace('[','')?replace(']','')>
        <#local yearName=spring.status.expression>
        <#local yearValue=spring.stringStatusValue>
        <div class="govuk-date-input__item">
          <div class="govuk-form-group">
            <label class="govuk-label govuk-date-input__label" for="${yearId}">Year</label>
            <input class="govuk-input <#if hasError> govuk-input--error</#if> govuk-date-input__input govuk-input--width-4" id="${yearId}" name="${yearName}" type="text" value="${yearValue}">
          </div>
        </div>
      </div>
    </@fdsFieldset.fieldset>
  </div>
</#macro>