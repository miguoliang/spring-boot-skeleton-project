<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
    <#if section = "header">
      <div class="mb-8">
          <#if messageHeader??>
            <h3 class="mb-1">${messageHeader}</h3>
          <#else>
            <p>${message.summary}</p>
          </#if>
      </div>
    <#elseif section = "form">
      <div>
        <p class="mb-8"><#if messageHeader?? >${message.summary}</#if><#if requiredActions??><#list requiredActions>:
            <b><#items as reqActionItem>${msg("requiredAction.${reqActionItem}")}<#sep>, </#items></b></#list><#else></#if>
        </p>
          <#if skipLink??>
          <#else>
              <#if pageRedirectUri?has_content>
                <p><a href="${pageRedirectUri}">${kcSanitize(msg("backToApplication"))?no_esc}</a></p>
              <#elseif actionUri?has_content>
                <p><a href="${actionUri}">${kcSanitize(msg("proceedWithAction"))?no_esc}</a></p>
              <#elseif (client.baseUrl)?has_content>
                <p><a href="${client.baseUrl}">${kcSanitize(msg("backToApplication"))?no_esc}</a></p>
              </#if>
          </#if>
      </div>
    </#if>
</@layout.registrationLayout>