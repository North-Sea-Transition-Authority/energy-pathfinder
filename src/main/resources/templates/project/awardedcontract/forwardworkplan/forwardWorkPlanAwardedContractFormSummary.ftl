<#include '../../../layout.ftl'>
<#import '../_awardedContractSummary.ftl' as awardedContractSummary>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->
<#-- @ftlvariable name="projectTypeDisplayNameLowercase" type="String" -->

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true errorItems=errorList>

    <#list awardedContractViews as awardedContractView>
        <@awardedContractSummary.awardedContractSummary
          awardedContractView=awardedContractView
          showActions=true
          showHeader=true
        />
    </#list>

    <@fdsAction.link
      linkText="Add awarded contract"
      linkUrl=springUrl(addAwardedContractUrl)
      role=true
      linkClass="govuk-button govuk-button--blue"
    />
    <@fdsForm.htmlForm>

        <#assign hasAddedAllContractsFormBind = "form.hasOtherContractsToAdd"/>

        <@fdsRadio.radioGroup
          path=hasAddedAllContractsFormBind
          labelText="Do you have any other awarded contracts to add to this ${projectTypeDisplayNameLowercase}?"
        >
            <@fdsRadio.radioYes path=hasAddedAllContractsFormBind/>
            <@fdsRadio.radioNo path=hasAddedAllContractsFormBind/>
        </@fdsRadio.radioGroup>


        <@fdsAction.submitButtons
          primaryButtonText="Continue"
          secondaryLinkText="Back to task list"
          linkSecondaryAction=true
          linkSecondaryActionUrl=springUrl(backToTaskListUrl)
        />
    </@fdsForm.htmlForm>
</@defaultPage>
