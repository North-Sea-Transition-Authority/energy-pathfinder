<#include '../../layout.ftl'/>

<#macro checkboxToggler
  checkboxContainerId
  prefixText=""
  selectAllLinkText="Select all"
  selectNoneLinkText="Select none"
  selectAllScreenReaderText=""
  selectNoneScreenReaderText=""
>
  <div class="checkbox-selection-toggler">
    <div class="checkbox-selection-toggler__toggler" data-checkbox-selection-toggler-container-id="${checkboxContainerId}">
      <div class="checkbox-selection-toggler__link-wrapper govuk-body">
        <#if prefixText?has_content>
          <span aria-hidden="true">${prefixText}</span>
        </#if>

        <#local defaultLinkClass="govuk-link govuk-link--no-visited-state checkbox-selection-toggler__link" />
        <@_link
          linkText=selectAllLinkText
          linkUrl="#"
          linkClass="${defaultLinkClass} checkbox-selection-toggler__select-all-link"
          ariaLabel=selectAllScreenReaderText
          linkScreenReaderText=""
        />
        <span>/</span>
        <@_link
          linkText=selectNoneLinkText
          linkUrl="#"
          linkClass="${defaultLinkClass} checkbox-selection-toggler__select-none-link"
          ariaLabel=selectNoneScreenReaderText
          linkScreenReaderText=""
        />
      </div>
    </div>
  </div>
</#macro>

<#macro _link
  linkText
  linkUrl
  linkClass="govuk-link"
  linkScreenReaderText=""
  role=false
  ariaDescribeBy=""
  ariaLabel=""
>
  <a href="${linkUrl}" class="${linkClass}" <#if ariaDescribeBy?has_content> aria-describedby="${ariaDescribeBy}"</#if> <#if ariaLabel?has_content> aria-label="${ariaLabel}"</#if>>
    ${linkText}
    <#if linkScreenReaderText?has_content>
      <span class="govuk-visually-hidden">${linkScreenReaderText}</span>
    </#if>
  </a>
</#macro>