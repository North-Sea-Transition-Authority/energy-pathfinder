<#include '../../layout.ftl'/>

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
      prompt="Field stage"
      diffedField=projectInformationDiffModel.ProjectInformationView_fieldStage
    />
    <#if isDevelopmentFieldStage>
      <@checkAnswers.diffedCheckAnswersRowNoActions
        prompt="Development first production date"
        diffedField=projectInformationDiffModel.ProjectInformationView_developmentFirstProductionDate
      />
    </#if>
    <#if isDiscoveryFieldStage>
      <@checkAnswers.diffedCheckAnswersRowNoActions
        prompt="Discovery first production date"
        diffedField=projectInformationDiffModel.ProjectInformationView_discoveryFirstProductionDate
      />
    </#if>
    <#if isDecommissioningFieldStage>
      <@checkAnswers.diffedCheckAnswersRowNoActions
        prompt="Decommissioning work start date"
        diffedField=projectInformationDiffModel.ProjectInformationView_decomWorkStartDate
      />
      <@checkAnswers.diffedCheckAnswersRowNoActions
        prompt="Decommissioning production cessation date"
        diffedField=projectInformationDiffModel.ProjectInformationView_decomProductionCessationDate
      />
    </#if>
    <#if isEnergyTransitionFieldStage>
      <@checkAnswers.diffedCheckAnswersRowNoActions
        prompt="Energy transition category"
        diffedField=projectInformationDiffModel.ProjectInformationView_energyTransitionCategory
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
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Email address"
      diffedField=projectInformationDiffModel.ProjectInformationView_contactEmailAddress
    />
  </@fdsCheckAnswers.checkAnswers>
</@sectionSummaryWrapper.sectionSummaryWrapper>
