<#include '../../layout.ftl'>
<#import '_integratedRigSummary.ftl' as integratedRigSummary>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <#if integratedRigViews?has_content>
    <#list integratedRigViews as integratedRigView>
      <@integratedRigSummary.integratedRigSummary
        integratedRigView=integratedRigView
        showHeader=true
        showActions=false
        showTag=true
        headingSize="h3"
        headingClass="govuk-heading-m"
      />
    </#list>
  <#else>
    <@emptySectionSummaryInset.emptySectionSummaryInset itemText="integrated rigs"/>
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>
