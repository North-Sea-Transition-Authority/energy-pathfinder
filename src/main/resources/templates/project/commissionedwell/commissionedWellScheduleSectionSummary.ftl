<#include '../../layout.ftl'>
<#import '_commissionedWellScheduleSummary.ftl' as commissionedWellScheduleSummary>

<#-- @ftlvariable name="sectionId" type="String" -->
<#-- @ftlvariable name="sectionTitle" type="String" -->
<#-- @ftlvariable name="projectTypeDisplayNameLowercase" type="String" -->
<#-- @ftlvariable name="commissionedWellScheduleDiffModel" type="java.util.List<java.util.Map<String, Object>>" -->

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <#if commissionedWellScheduleDiffModel?has_content>
    <#list commissionedWellScheduleDiffModel as commissionedWellScheduleDiff>
      <@commissionedWellScheduleSummary.commissionedWellDiffSummary
        commissionedWellScheduleDiff=commissionedWellScheduleDiff
        showHeader=true
        showActions=false
        headingSize="h3"
        headingClass="govuk-heading-m"
      />
    </#list>
  <#else>
    <@emptySectionSummaryInset.emptySectionSummaryInset
      itemText="well commissioning schedules"
      projectTypeDisplayName=projectTypeDisplayNameLowercase
    />
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>
