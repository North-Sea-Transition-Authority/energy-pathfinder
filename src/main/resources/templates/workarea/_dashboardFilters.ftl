<#include '../layout.ftl'/>

<#macro dashboardFilters clearFilterUrl filterType="">
  <@fdsSearch.searchFilter>
    <@fdsSearch.searchFilterList
      filterButtonItemText="projects"
      filterButtonClass="govuk-button govuk-button--blue"
      clearFilterUrl=springUrl(clearFilterUrl)
    >
      <#if filterType == "REGULATOR">
        <@regulatorFilters/>
      <#else>
        <@operatorFilters/>
      </#if>
    </@fdsSearch.searchFilterList>
  </@fdsSearch.searchFilter>
</#macro>

<#macro operatorFilters>
  <@defaultFilters/>
</#macro>

<#macro regulatorFilters>
  <@fdsSearch.searchFilterItem itemName="Project operator/developer">
    <@fdsSearch.searchTextInput path="form.operatorName" labelText="Project operator/developer" labelClass="govuk-visually-hidden"/>
  </@fdsSearch.searchFilterItem>
  <@defaultFilters/>
</#macro>

<#macro defaultFilters>
  <@fdsSearch.searchFilterItem itemName="Project title">
    <@fdsSearch.searchTextInput path="form.projectTitle" labelText="Project title" labelClass="govuk-visually-hidden"/>
  </@fdsSearch.searchFilterItem>
  <@fdsSearch.searchFilterItem itemName="Field stage">
    <@fdsSearch.searchCheckboxes path="form.fieldStages" checkboxes=fieldStages/>
  </@fdsSearch.searchFilterItem>
  <@fdsSearch.searchFilterItem itemName="Field">
    <@fdsSearch.searchTextInput path="form.field" labelText="Field" labelClass="govuk-visually-hidden"/>
  </@fdsSearch.searchFilterItem>
  <@fdsSearch.searchFilterItem itemName="UKCS area">
    <@fdsSearch.searchCheckboxes path="form.ukcsAreas" checkboxes=ukcsAreas/>
  </@fdsSearch.searchFilterItem>
  <@fdsSearch.searchFilterItem itemName="Status">
    <@fdsSearch.searchCheckboxes path="form.projectStatusList" checkboxes=statuses/>
  </@fdsSearch.searchFilterItem>
</#macro>
