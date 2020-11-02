<#include '../../layout.ftl'/>

<h2 class="govuk-heading-l summary-list__heading" id=${sectionId} >${sectionTitle}</h2>
<@fdsCheckAnswers.checkAnswers >
  <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Field">
    <@stringWithTag.stringWithTag stringWithTag=projectLocationView.field />
  </@checkAnswers.checkAnswersRowNoActionsWithNested>
  <@checkAnswers.checkAnswersRowNoActions prompt="Field type" value=projectLocationView.fieldType!"" />
  <@checkAnswers.checkAnswersRowNoActions prompt="Water depth" value=projectLocationView.waterDepth!"" />
  <@checkAnswers.checkAnswersRowNoActions prompt="Approved Field Development Plan" value=projectLocationView.approvedFieldDevelopmentPlan?has_content?then(projectLocationView.approvedFieldDevelopmentPlan?string("Yes", "No"), "") />
  <#if projectLocationView.approvedFieldDevelopmentPlan?has_content && projectLocationView.approvedFieldDevelopmentPlan>
    <@checkAnswers.checkAnswersRowNoActions prompt="FDP approval date" value=projectLocationView.approvedFdpDate!"" />
  </#if>
  <@checkAnswers.checkAnswersRowNoActions prompt="Approved Decommissioning Program" value=projectLocationView.approvedDecomProgram?has_content?then(projectLocationView.approvedDecomProgram?string("Yes", "No"), "") />
  <#if projectLocationView.approvedDecomProgram?has_content && projectLocationView.approvedDecomProgram>
    <@checkAnswers.checkAnswersRowNoActions prompt="DP approval date" value=projectLocationView.approvedDecomProgramDate!"" />
  </#if>
  <@checkAnswers.checkAnswersRowNoActions prompt="UKCS area" value=projectLocationView.ukcsArea!"" />
  <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Licence blocks">
    <#list projectLocationView.licenceBlocks as licenceBlock>
      ${licenceBlock}
      <br>
    </#list>
  </@checkAnswers.checkAnswersRowNoActionsWithNested>
</@fdsCheckAnswers.checkAnswers>
