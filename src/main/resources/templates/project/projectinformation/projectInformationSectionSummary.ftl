<#include '../../layout.ftl'/>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <@fdsCheckAnswers.checkAnswers >
    <@checkAnswers.checkAnswersRowNoActions prompt="Project title" value=projectTitle!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Project summary" value=projectSummary!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Field stage" value=fieldStage!"" />
    <#if developmentRelated?has_content && developmentRelated>
      <@checkAnswers.checkAnswersRowNoActions prompt="Development first production date" value=developmentFirstProductionDate!"" />
    </#if>
    <#if discoveryRelated?has_content && discoveryRelated>
      <@checkAnswers.checkAnswersRowNoActions prompt="Discovery first production date" value=discoveryFirstProductionDate!"" />
    </#if>
    <#if decomRelated?has_content && decomRelated>
      <@checkAnswers.checkAnswersRowNoActions prompt="Decommissioning work start date" value=decomWorkStartDate!"" />
      <@checkAnswers.checkAnswersRowNoActions prompt="Decommissionoing production cessation date" value=decomProductionCessationDate!"" />
    </#if>
    <@checkAnswers.checkAnswersRowNoActions prompt="Name" value=name!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Phone number" value=phoneNumber!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Job title" value=jobTitle!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Email address" value=emailAddress!"" />
  </@fdsCheckAnswers.checkAnswers>
</@sectionSummaryWrapper.sectionSummaryWrapper>
