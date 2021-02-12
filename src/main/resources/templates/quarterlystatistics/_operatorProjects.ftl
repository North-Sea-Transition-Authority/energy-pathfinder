<#include '../layout.ftl'>

<#macro _operators operatorProjectMap>
  <@_updateRequiredToggle/>
  <@fdsAccordion.accordion accordionId="operators">
    <#list operatorProjectMap as operatorName, reportableProjects>
      <@fdsAccordion.accordionSection sectionHeading=operatorName>
        <@_operatorProjects operatorReportableProjects=reportableProjects/>
      </@fdsAccordion.accordionSection>
    </#list>
  </@fdsAccordion.accordion>
</#macro>

<#macro _updateRequiredToggle>
  <div class="govuk-form-group">
    <div class="govuk-checkboxes">
      <div class="govuk-checkboxes__item">
        <input class="govuk-checkboxes__input" id="show-update-required-toggle" type="checkbox"/>
        <label class="govuk-label govuk-checkboxes__label" for="show-update-required-toggle">
          Show only projects that require an update this quarter
        </label>
      </div>
    </div>
  </div>

  <script type="text/javascript">
    $(document).ready(function() {

      const toggleSelector = '#show-update-required-toggle';

      $(toggleSelector).click(function() {

        // toggle single rows which don't require an update
        $('.operator-projects__no-update-required-row').toggleClass('operator-projects__no-update-required-row--hide');

        // toggle tables which don't have any require update rows
        const $operatorsWithNoProjectsRequiringUpdate = $(".operator-projects__table:not(:has(.operator-projects__update-required-row))");
        $operatorsWithNoProjectsRequiringUpdate.each(function() {
          $(this).toggleClass("operator-projects__table--hide");
          $(this).parent().find('.operator-projects__inset').toggleClass("operator-projects__inset--hide");
        });
      });
    });
  </script>
</#macro>

<#macro _operatorProjects operatorReportableProjects>
  <div class="operator-projects">
    <table class="govuk-table operator-projects__table">
      <thead>
        <tr class="govuk-table__row">
          <th scope="col" class="govuk-table__header govuk-table__header--no-border govuk-!-width-one-third">
            Project
          </th>
          <th scope="col" class="govuk-table__header govuk-table__header--no-border govuk-!-width-two-thirds">
            Last updated date
          </th>
        </tr>
      </thead>
      <tbody>
        <#list operatorReportableProjects as operatorReportableProject>

          <#assign hasUpdateInQuarter = operatorReportableProject.hasUpdateInQuarter() />

          <tr class="govuk-table__row <#if hasUpdateInQuarter>operator-projects__no-update-required-row<#else>operator-projects__update-required-row</#if>">
            <td class="govuk-table__cell govuk-table__cell--no-border">
              <#assign linkScreenReaderText>
                <span class="govuk-visually-hidden">View project </span>
                ${operatorReportableProject.projectTitle}
              </#assign>
              <@fdsAction.link
                linkText=linkScreenReaderText
                linkUrl=springUrl(operatorReportableProject.viewProjectUrl)
                openInNewTab=true
              />
            </td>
            <td class="govuk-table__cell govuk-table__cell--no-border">
              <div class="operator-projects__updated-date">
                ${operatorReportableProject.lastUpdatedDatetimeFormatted}
                <#if !hasUpdateInQuarter>
                  <@tag.tag tagClasses="operator-projects__tag">UPDATE REQUIRED</@tag.tag>
                </#if>
              </div>
            </td>
          </tr>
        </#list>
      </tbody>
    </table>
    <@fdsInsetText.insetText insetTextClass="operator-projects__inset operator-projects__inset--hide">
      <p class="govuk-body">All published projects for this operators have been updated in the current quarter</p>
    </@fdsInsetText.insetText>
  </div>
</#macro>
