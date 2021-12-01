<#include '../../../layout.ftl'>
<#import '../../macros/forwardworkplan/forwardWorkPlanGuidance.ftl' as forwardWorkPlanGuidance>

<#-- @ftlvariable name="infrastructureProjectTypeLowercaseDisplayName" type="String" -->
<#-- @ftlvariable name="forwardWorkPlanProjectTypeLowercaseDisplayName" type="String" -->
<#-- @ftlvariable name="startInfrastructureProjectUrl" type="String" -->
<#-- @ftlvariable name="taskListUrl" type="String" -->

<#assign pageHeading = "Manage ${forwardWorkPlanProjectTypeLowercaseDisplayName}">

<@defaultPage htmlTitle=pageHeading pageHeading=pageHeading backLink=true twoThirdsColumn=true>
  <@fdsStartPage.startPage
    startActionText=pageHeading
    startActionUrl=springUrl(taskListUrl)
    startActionButton=false
  >
    <@forwardWorkPlanGuidance.introductionText
      forwardWorkPlanProjectTypeLowercaseDisplayName=forwardWorkPlanProjectTypeLowercaseDisplayName
      infrastructureProjectTypeLowercaseDisplayName=infrastructureProjectTypeLowercaseDisplayName
      startInfrastructureProjectUrl=startInfrastructureProjectUrl
    />
  </@fdsStartPage.startPage>
</@defaultPage>