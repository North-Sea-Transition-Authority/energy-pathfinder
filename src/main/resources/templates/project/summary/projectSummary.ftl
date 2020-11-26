<#include '../../layoutPane.ftl'/>

<#macro summaryWithSubNavigation pageHeading projectSummaryView sidebarHeading errorMessage="">
  <@defaultPagePane htmlTitle=pageHeading phaseBanner=false>
    <@defaultPagePaneSubNav>
      <@fdsSubNavigation.subNavigation>
        <@fdsSubNavigation.subNavigationSection themeHeading=sidebarHeading>
          <#list projectSummaryView.sidebarSectionLinks as sidebarLink>
            <@sideBarSectionLink.renderSidebarLink sidebarLink=sidebarLink/>
          </#list>
        </@fdsSubNavigation.subNavigationSection>
      </@fdsSubNavigation.subNavigation>
    </@defaultPagePaneSubNav>

    <@defaultPagePaneContent pageHeading=pageHeading>
      <@summaryWithoutSubNavigation projectSummaryView=projectSummaryView errorMessage=errorMessage>
        <#nested>
      </@summaryWithoutSubNavigation>
    </@defaultPagePaneContent>
  </@defaultPagePane>
</#macro>

<#macro summaryWithoutSubNavigation projectSummaryView errorMessage="">
  <#if errorMessage?has_content>
    <@fdsError.singleErrorSummary errorMessage=errorMessage />
  </#if>
  ${projectSummaryView.summaryHtml?no_esc}
  <#nested>
</#macro>
