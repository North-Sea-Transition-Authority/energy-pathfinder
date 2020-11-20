<#include '../../layoutPane.ftl'/>

<#macro summaryWithSubNavigation pageHeading projectSummaryView sidebarHeading>
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
      <@summaryWithoutSubNavigation projectSummaryView=projectSummaryView>
        <#nested>
      </@summaryWithoutSubNavigation>
    </@defaultPagePaneContent>
  </@defaultPagePane>
</#macro>

<#macro summaryWithoutSubNavigation projectSummaryView>
  ${projectSummaryView.summaryHtml?no_esc}
  <#nested>
</#macro>
