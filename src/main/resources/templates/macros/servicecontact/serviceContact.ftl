<#include '../../layout.ftl'>

<#macro serviceContact serviceContact includeHeader=true>
  <#if includeHeader>
    <h2 class="govuk-heading-m">${serviceContact.displayName}</h2>
    <#if serviceContact.description?has_content>
      <span class="govuk-hint">${serviceContact.description}</span>
    </#if>
  </#if>
  <ul class="govuk-list">
    <li>${serviceContact.serviceName}</li>
    <#if serviceContact.phoneNumber?has_content>
      <li>${serviceContact.phoneNumber}</li>
    </#if>
    <#if serviceContact.emailAddress?has_content>
      <li>
        <@fdsAction.link
          linkText=serviceContact.emailAddress
          linkUrl="mailto:${serviceContact.emailAddress}"
        />
      </li>
    </#if>
  </ul>
</#macro>