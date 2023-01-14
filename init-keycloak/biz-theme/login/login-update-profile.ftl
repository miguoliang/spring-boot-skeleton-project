<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','email','firstName','lastName'); section>
    <#if section = "header">
      <div class="mb-8"></div>
      <div class="mb-8"><h3 class="mb-1">${msg("loginProfileTitle")}</h3></div>
    <#elseif section = "form">
      <form id="kc-update-profile-form" action="${url.loginAction}" method="post">
        <div class="form-container vertical">
            <#if user.editUsernameAllowed>
              <div class="form-item vertical"><label class="form-label mb-2">Username</label>
                <div class=""><input
                          class="<#if messagesPerField.existsError('username')>input-invalid</#if> input input-md h-11 focus:ring-indigo-600 focus-within:ring-indigo-600 focus-within:border-indigo-600 focus:border-indigo-600"
                          type="text" name="username" autocomplete="off" placeholder="Username"
                          value="${(user.username!'')}">
                    <#if messagesPerField.existsError('username')>
                      <div class="form-explain" style="opacity: 1; margin-top: 3px;">
                          ${kcSanitize(messagesPerField.getFirstError('username'))?no_esc}
                      </div>
                    </#if>
                </div>
              </div>
            </#if>
            <#if user.editEmailAllowed>
              <div class="form-item vertical"><label class="form-label mb-2">Email</label>
                <div class=""><input
                          class="<#if messagesPerField.existsError('email')>input-invalid</#if> input input-md h-11 focus:ring-indigo-600 focus-within:ring-indigo-600 focus-within:border-indigo-600 focus:border-indigo-600"
                          type="email" name="email" autocomplete="off" placeholder="Email" value="${(user.email!'')}">
                    <#if messagesPerField.existsError('email')>
                      <div class="form-explain" style="opacity: 1; margin-top: 3px;">
                          ${kcSanitize(messagesPerField.get('email'))?no_esc}
                      </div>
                    </#if></div>
              </div>
            </#if>
          <div class="form-item vertical"><label class="form-label mb-2">Username</label>
            <div class=""><input
                      class="<#if messagesPerField.existsError('firstName')>input-invalid</#if> input input-md h-11 focus:ring-indigo-600 focus-within:ring-indigo-600 focus-within:border-indigo-600 focus:border-indigo-600"
                      type="text" name="firstName" autocomplete="off" placeholder="First Name"
                      value="${(user.firstName!'')}">
                <#if messagesPerField.existsError('firstName')>
                  <div class="form-explain" style="opacity: 1; margin-top: 3px;">
                      ${kcSanitize(messagesPerField.getFirstError('firstName'))?no_esc}
                  </div>
                </#if>
            </div>
          </div>
          <div class="form-item vertical"><label class="form-label mb-2">Username</label>
            <div class=""><input
                      class="<#if messagesPerField.existsError('lastName')>input-invalid</#if> input input-md h-11 focus:ring-indigo-600 focus-within:ring-indigo-600 focus-within:border-indigo-600 focus:border-indigo-600"
                      type="text" name="lastName" autocomplete="off" placeholder="Last Name"
                      value="${(user.lastName!'')}">
                <#if messagesPerField.existsError('lastName')>
                  <div class="form-explain" style="opacity: 1; margin-top: 3px;">
                      ${kcSanitize(messagesPerField.getFirstError('lastName'))?no_esc}
                  </div>
                </#if>
            </div>
          </div>
            <#if isAppInitiatedAction??>
              <button class="mb-4 button bg-indigo-600 hover:bg-indigo-500 active:bg-indigo-700 text-white radius-round h-11 px-8 py-2 w-full"
                      type="submit">${msg("doSubmit")}
              </button>
              <button class="button bg-white border border-1 border-indigo-600 hover:border-indigo-500 active:border-indigo-700 text-indigo-600 hover:text-indigo-500 active:bg-indigo-700 radius-round h-11 px-8 py-2 w-full"
                      type="submit" name="cancel-aia" value="true">${msg("doCancel")}
              </button>
            <#else>
              <button class="button bg-indigo-600 hover:bg-indigo-500 active:bg-indigo-700 text-white radius-round h-11 px-8 py-2 w-full"
                      type="submit">${msg("doSubmit")}
              </button>
            </#if>
        </div>
      </form>
    </#if>
</@layout.registrationLayout>
