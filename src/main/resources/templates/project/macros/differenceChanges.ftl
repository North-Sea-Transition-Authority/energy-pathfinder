<#include '../../layout.ftl'>

<#macro _diffChanges>
  <div class="diff-changes">
    <#nested>
  </div>
</#macro>

<#macro _diffChangesInsert>
  <span class="govuk-visually-hidden">start insertion,</span>
  <ins class="diff-changes__insert">
    <#nested>
  </ins>
  <span class="govuk-visually-hidden">end insertion,</span>
</#macro>

<#macro _diffChangesDelete>
  <span class="govuk-visually-hidden">start deletion,</span>
  <del class="diff-changes__delete">
    <#nested>
  </del>
  <span class="govuk-visually-hidden">end deletion,</span>
</#macro>

<#macro _diffLink diffedLinkText diffedLinkUrl>
 <@_diffChangesValueWrapper>
    <@fdsAction.link
      linkText=diffedLinkText
      linkClass="govuk-link"
      linkUrl=springUrl(diffedLinkUrl)
    />
 </@_diffChangesValueWrapper>
</#macro>

<#macro _rawValueWrapper>
  <span class="diff-raw-value">
    <#nested/>
  </span>
</#macro>

<#macro _diffLinkWithRawValueWrapper diffedLinkText diffedLinkUrl>
  <@_rawValueWrapper>
    <@_diffLink
      diffedLinkText=diffedLinkText
      diffedLinkUrl=diffedLinkUrl
    />
  </@_rawValueWrapper>
</#macro>

<#macro _diffChangesValueWrapper>
  <span class="diff-changes__value">
    <#nested />
  </span>
</#macro>

<#macro _diffValue noAutoEscapeFlagValue="false" value="" tag="" multiLineTextBlockClass="">
  <#if noAutoEscapeFlagValue=="true">
    <@_diffChangesValueWrapper>
      <#noautoesc>${value}</#noautoesc>
    </@_diffChangesValueWrapper>
  <#else>
    <@_diffChangesValueWrapper>
      <#--
        It is important that this macro call is on one line to avoid extra carriage returns being shown
        on the screen due to new lines being preserved within the macro
      -->
      <!--
        It is important that this macro call is on one line to avoid extra carriage returns being shown
        on the screen due to new lines being preserved within the macro
      -->
      <@multiLineText.multiLineText blockClass=multiLineTextBlockClass>${value}</@multiLineText.multiLineText>
    </@_diffChangesValueWrapper>
    <#if tag?has_content && tag.displayName?has_content>
      <strong class="govuk-tag">
        ${tag.displayName}
      </strong>
    </#if>
  </#if>
</#macro>

<#macro _diffValueWithRawValueWrapper noAutoEscapeFlagValue="false" value="" tag="" multiLineTextBlockClass="">
  <@_rawValueWrapper>
    <@_diffValue
      noAutoEscapeFlagValue=noAutoEscapeFlagValue
      value=value
      tag=tag
      multiLineTextBlockClass=multiLineTextBlockClass
    />
  </@_rawValueWrapper>
</#macro>

<#macro renderDifferenceLink differenceType diffedLinkText diffedLinkUrl>

  <#if differenceType == "ADDED">
    <@_diffChanges>
      <@_diffChangesInsert>
        <@_diffLink
          diffedLinkText=diffedLinkText.currentValue
          diffedLinkUrl=diffedLinkUrl.currentValue
        />
      </@_diffChangesInsert>
    </@_diffChanges>

    <@_diffLinkWithRawValueWrapper
      diffedLinkText=diffedLinkText.currentValue
      diffedLinkUrl=diffedLinkUrl.currentValue
    />
  </#if>

  <#if differenceType == "UPDATED">
    <@_diffChanges>
      <@_diffChangesDelete>
        <@_diffLink
          diffedLinkText=diffedLinkText.previousValue
          diffedLinkUrl=diffedLinkUrl.previousValue
        />
      </@_diffChangesDelete>

      <@_diffChangesInsert>
        <@_diffLink
          diffedLinkText=diffedLinkText.currentValue
          diffedLinkUrl=diffedLinkUrl.currentValue
        />
      </@_diffChangesInsert>
    </@_diffChanges>

    <@_diffLinkWithRawValueWrapper
      diffedLinkText=diffedLinkText.currentValue
      diffedLinkUrl=diffedLinkUrl.currentValue
    />
  </#if>

  <#if differenceType == "DELETED">
    <@_diffChanges>
      <@_diffChangesDelete>
        <@_diffValue value=diffedLinkText.previousValue />
      </@_diffChangesDelete>
    </@_diffChanges>
  </#if>

  <#if differenceType == "UNCHANGED">
    <@_diffLink
      diffedLinkText=diffedLinkText.currentValue
      diffedLinkUrl=diffedLinkUrl.currentValue
    />
  </#if>

</#macro>

<#macro renderDifference diffedField noAutoEscapeFlag="false" multiLineTextBlockClass="">

  <#if diffedField.differenceType == "ADDED">

    <@_diffChanges>
      <@_diffChangesInsert>
        <@_diffValue
          noAutoEscapeFlagValue=noAutoEscapeFlag
          value=diffedField.currentValue
          tag=diffedField.currentValueTag
          multiLineTextBlockClass=multiLineTextBlockClass
        />
      </@_diffChangesInsert>
    </@_diffChanges>

    <@_diffValueWithRawValueWrapper
      noAutoEscapeFlagValue=noAutoEscapeFlag
      value=diffedField.currentValue
      tag=diffedField.currentValueTag
      multiLineTextBlockClass=multiLineTextBlockClass
    />

  </#if>

  <#if diffedField.differenceType == "UPDATED">

    <@_diffChanges>
      <@_diffChangesDelete>
        <@_diffValue
          noAutoEscapeFlagValue=noAutoEscapeFlag
          value=diffedField.previousValue
          tag=diffedField.previousValueTag
          multiLineTextBlockClass=multiLineTextBlockClass
        />
      </@_diffChangesDelete>

      <@_diffChangesInsert>
        <@_diffValue
          noAutoEscapeFlagValue=noAutoEscapeFlag
          value=diffedField.currentValue
          tag=diffedField.currentValueTag
          multiLineTextBlockClass=multiLineTextBlockClass
        />
      </@_diffChangesInsert>
    </@_diffChanges>

    <@_diffValueWithRawValueWrapper
      noAutoEscapeFlagValue=noAutoEscapeFlag
      value=diffedField.currentValue
      tag=diffedField.currentValueTag
      multiLineTextBlockClass=multiLineTextBlockClass
    />

  </#if>

  <#if diffedField.differenceType == "DELETED">

    <@_diffChanges>
      <@_diffChangesDelete>
        <@_diffValue
          noAutoEscapeFlagValue=noAutoEscapeFlag
          value=diffedField.previousValue
          tag=diffedField.previousValueTag
          multiLineTextBlockClass=multiLineTextBlockClass
        />
      </@_diffChangesDelete>
    </@_diffChanges>

  </#if>

  <#if diffedField.differenceType == "UNCHANGED">
    <@_diffValue
      noAutoEscapeFlagValue=noAutoEscapeFlag
      value=diffedField.currentValue
      tag=diffedField.currentValueTag
      multiLineTextBlockClass=multiLineTextBlockClass
    />
  </#if>

</#macro>

<#macro toggler showDiffOnLoad=false togglerLabel="Show differences from previous version">
  <div class="govuk-form-group">
    <div class="govuk-checkboxes">
      <div class="govuk-checkboxes__item">
        <input class="govuk-checkboxes__input" id="toggle-diff" type="checkbox" ${showDiffOnLoad?then('checked','')}>
        <label class="govuk-label govuk-checkboxes__label" for="toggle-diff">
          ${togglerLabel}
        </label>
      </div>
    </div>
  </div>

  <script>
    $(document).ready(function() {

      function toggleDiff(diffTogglerIsChecked) {
        if (diffTogglerIsChecked) {
          $('.diff-raw-value').hide();
        }
        else {
          $('.diff-raw-value').show();
        }
      }

      const toggleDiffSelector = '#toggle-diff';

      $(toggleDiffSelector).click(function() {
        toggleDiff(this.checked);
        $('.diff-changes').toggleClass('diff-changes--flex');
      });

      toggleDiff($(toggleDiffSelector).prop("checked"));

    });
  </script>
</#macro>