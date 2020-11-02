<#macro stringWithTag stringWithTag>
  ${stringWithTag.value}
  <#if stringWithTag.tag.displayName?has_content>
    <strong class="govuk-tag">
      ${stringWithTag.tag.displayName}
    </strong>
  </#if>
</#macro>
