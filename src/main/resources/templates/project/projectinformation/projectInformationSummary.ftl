<#include '../../layout.ftl'/>

<h2 class="govuk-heading-l summary-list__heading" id=${sectionId} >${sectionTitle}</h2>
<@fdsCheckAnswers.checkAnswers >
    <@checkAnswers.checkAnswersRowNoActions prompt="Project title" value=projectTitle!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Project summary" value=projectSummary!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Field stage" value=fieldStage!"" />
    <#if developmentRelated>
      <@checkAnswers.checkAnswersRowNoActions prompt="Development first production date" value=developmentFirstProductionDate!"" />
    </#if>
    <#if discoveryRelated>
      <@checkAnswers.checkAnswersRowNoActions prompt="Discovery first production date" value=discoveryFirstProductionDate!"" />
    </#if>
    <#if decomRelated>
      <@checkAnswers.checkAnswersRowNoActions prompt="Decommissioning work start date" value=decomWorkStartDate!"" />
      <@checkAnswers.checkAnswersRowNoActions prompt="Decommissionoing production cessation date" value=decomProductionCessationDate!"" />
    </#if>
    <@checkAnswers.checkAnswersRowNoActions prompt="Name" value=name!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Phone number" value=phoneNumber!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Job title" value=jobTitle!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Email address" value=emailAddress!"" />
</@fdsCheckAnswers.checkAnswers>