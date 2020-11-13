<#include '../layout.ftl'/>

<#macro dashboardFilters>
  <@fdsSearch.searchFilter>
    <@fdsSearch.searchFilterList filterButtonItemText="projects">
      <@fdsSearch.searchFilterItem itemName="Project title"/>
      <@fdsSearch.searchFilterItem itemName="Field stage"/>
      <@fdsSearch.searchFilterItem itemName="Field"/>
      <@fdsSearch.searchFilterItem itemName="Status"/>
    </@fdsSearch.searchFilterList>
  </@fdsSearch.searchFilter>
</#macro>