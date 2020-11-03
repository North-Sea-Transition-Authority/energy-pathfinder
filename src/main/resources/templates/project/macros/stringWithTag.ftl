<#macro stringWithTag stringWithTag>
  <#if stringWithTag.value?has_content>
    ${stringWithTag.value}
    <#if stringWithTag.tag.displayName?has_content>
      <strong class="govuk-tag">
        ${stringWithTag.tag.displayName}
      </strong>
    </#if>
  </#if>
</#macro>
