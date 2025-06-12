<#include '../../layout.ftl'/>

<#-- @ftlvariable name="sectionId" type="String" -->
<#-- @ftlvariable name="sectionTitle" type="String" -->
<#-- @ftlvariable name="projectInformationDiffModel" type="java.util.Map<String, Object>" -->
<#-- @ftlvariable name="isDevelopmentFieldStageSubCategory" type="Boolean" -->

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <@fdsCheckAnswers.checkAnswers >
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Project title"
      diffedField=projectInformationDiffModel.ProjectInformationView_projectTitle
    />
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Project summary"
      diffedField=projectInformationDiffModel.ProjectInformationView_projectSummary
    />
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Energy project"
      diffedField=projectInformationDiffModel.ProjectInformationView_fieldStage
    />
    <#if isDevelopmentFieldStageSubCategory>
      <@checkAnswers.diffedCheckAnswersRowNoActions
        prompt="Development first production date"
        diffedField=projectInformationDiffModel.ProjectInformationView_developmentFirstProductionDate
      />
    </#if>
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Name"
      diffedField=projectInformationDiffModel.ProjectInformationView_contactName
    />
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Phone number"
      diffedField=projectInformationDiffModel.ProjectInformationView_contactPhoneNumber
    />
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Job title"
      diffedField=projectInformationDiffModel.ProjectInformationView_contactJobTitle
    />
    <@checkAnswers.checkAnswersRowEmailOrDiff
      prompt="Email address"
      fieldValue=projectInformationDiffModel.ProjectInformationView_contactEmailAddress
      isDiffedField=true
    />
  </@fdsCheckAnswers.checkAnswers>
</@sectionSummaryWrapper.sectionSummaryWrapper>
