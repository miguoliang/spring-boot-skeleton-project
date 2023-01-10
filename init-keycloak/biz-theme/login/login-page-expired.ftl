<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "header">
      <div class="mb-8"><h3 class="mb-1">${msg("pageExpiredTitle")}</h3></div>
    <#elseif section = "form">
      <p class="mb-8">${msg("pageExpiredMsg1")} <a class="text-blue-500" id="loginRestartLink" href="${url.loginRestartFlowUrl}">${msg("doClickHere")}</a>
        .</p>
      <p>${msg("pageExpiredMsg2")} <a class="text-blue-500" id="loginContinueLink" href="${url.loginAction}">${msg("doClickHere")}</a> .</p>
    </#if>
</@layout.registrationLayout>
