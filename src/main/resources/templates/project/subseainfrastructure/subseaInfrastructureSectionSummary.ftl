<#include '../../layout.ftl'>
<#import '_subseaInfrastructureSummary.ftl' as subseaInfrastructureSummary>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <#if subseaInfrastructureDiffModel?has_content>
    <#list subseaInfrastructureDiffModel as subseaInfrastructureDiff>
      <@subseaInfrastructureSummary.subseaInfrastructureDiffSummary
        diffModel=subseaInfrastructureDiff.subseaInfrastructureDiff
        concreteMattress=subseaInfrastructureDiff.concreteMattress
        subseaStructure=subseaInfrastructureDiff.subseaStructure
        otherInfrastructure=subseaInfrastructureDiff.otherInfrastructure
        showHeader=true
        showActions=false
        headingSize="h3"
        headingClass="govuk-heading-m"
      />
    </#list>
  <#else>
    <@emptySectionSummaryInset.emptySectionSummaryInset
      itemText="subsea infrastructures"
      projectTypeDisplayName=projectTypeDisplayNameLowercase
    />
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>
