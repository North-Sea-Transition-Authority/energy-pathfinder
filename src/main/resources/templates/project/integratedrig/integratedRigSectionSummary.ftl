<#include '../../layout.ftl'>
<#import '_integratedRigSummary.ftl' as integratedRigSummary>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <#if integratedRigDiffModel?has_content>
    <#list integratedRigDiffModel as integratedRigDiff>
      <@integratedRigSummary.integratedRigDiffSummary
        integratedRigDiff=integratedRigDiff
        showHeader=true
        showActions=false
        headingSize="h3"
        headingClass="govuk-heading-m"
      />
    </#list>
  <#else>
    <@emptySectionSummaryInset.emptySectionSummaryInset
      itemText="integrated rigs"
      projectTypeDisplayName=projectTypeDisplayNameLowercase
    />
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>
