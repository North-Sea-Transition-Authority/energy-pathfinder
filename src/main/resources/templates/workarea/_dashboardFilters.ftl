<#include '../layout.ftl'/>

<#macro dashboardFilters includeOperatorFilter=false>
  <@fdsSearch.searchFilter>
    <@fdsSearch.searchFilterList filterButtonItemText="projects">
      <#if includeOperatorFilter>
        <@fdsSearch.searchFilterItem itemName="Project operator">
          <@fdsSearch.searchTextInput path="form.operatorName" labelText="Project operator"/>
        </@fdsSearch.searchFilterItem>
      </#if>
      <@fdsSearch.searchFilterItem itemName="Project title">
        <@fdsSearch.searchTextInput path="form.projectTitle" labelText="Project title"/>
      </@fdsSearch.searchFilterItem>
      <@fdsSearch.searchFilterItem itemName="Field stage">
        <@fdsSearch.searchCheckboxes path="form.fieldStages" checkboxes=fieldStages/>
      </@fdsSearch.searchFilterItem>
      <@fdsSearch.searchFilterItem itemName="Field">
        <@fdsSearch.searchTextInput path="form.field" labelText="Field"/>
      </@fdsSearch.searchFilterItem>
      <@fdsSearch.searchFilterItem itemName="UKCS Area">
        <@fdsSearch.searchCheckboxes path="form.ukcsAreas" checkboxes=ukcsAreas/>
      </@fdsSearch.searchFilterItem>
      <@fdsSearch.searchFilterItem itemName="Status">
        <@fdsSearch.searchCheckboxes path="form.projectStatusList" checkboxes=statuses/>
      </@fdsSearch.searchFilterItem>
    </@fdsSearch.searchFilterList>
  </@fdsSearch.searchFilter>
</#macro>