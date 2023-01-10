<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password') displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <#if section = "header">
      <div class="mb-8"></div>
      <div class="mb-8"><h3 class="mb-1">Welcome back!</h3>
        <p>Please enter your credentials to sign in!</p></div>
    <#elseif section = "form">
      <form action="${url.loginAction}" method="post">
        <div class="form-container vertical">
          <div class="form-item vertical"><label class="form-label mb-2">Username</label>
            <div class=""><input
                      class="input input-md h-11 focus:ring-indigo-600 focus-within:ring-indigo-600 focus-within:border-indigo-600 focus:border-indigo-600"
                      type="text" name="username" autocomplete="off" placeholder="Username" value="admin">
            </div>
          </div>
          <div class="form-item vertical"><label class="form-label mb-2">Password</label>
            <div class=""><span class="input-wrapper "><input
                        class="input input-md h-11 focus:ring-indigo-600 focus-within:ring-indigo-600 focus-within:border-indigo-600 focus:border-indigo-600"
                        type="password" name="password" autocomplete="off" placeholder="Password" value="123Qwe"
                        style="padding-right: 2.25rem;"><div class="input-suffix-end"><span
                          class="cursor-pointer text-xl"><svg stroke="currentColor" fill="none" stroke-width="0"
                                                              viewBox="0 0 24 24" height="1em" width="1em"
                                                              xmlns="http://www.w3.org/2000/svg"><path
                              stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"></path></svg></span></div></span>
            </div>
          </div>
          <div class="flex justify-between mb-6"><label class="checkbox-label mb-0"><input
                      class="checkbox text-indigo-600" type="checkbox" name="rememberMe" form="[object Object]"
                      value="true" checked=""><span class="ltr:ml-2 rtl:mr-2">Remember Me</span></label><a
                    class="text-indigo-600 hover:underline" href="${url.loginResetCredentialsUrl}">Forgot
              Password?</a></div>
          <button class="button bg-indigo-600 hover:bg-indigo-500 active:bg-indigo-700 text-white radius-round h-11 px-8 py-2 w-full"
                  type="submit">Sign In
          </button>
        </div>
      </form>
    <#--    <div id="kc-form">-->
    <#--      <div id="kc-form-wrapper">-->
    <#--        <#if realm.password>-->
    <#--            <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">-->
    <#--                <#if !usernameHidden??>-->
    <#--                    <div class="${properties.kcFormGroupClass!}">-->
    <#--                        <label for="username" class="${properties.kcLabelClass!}"><#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>${msg("email")}</#if></label>-->

    <#--                        <input tabindex="1" id="username" class="${properties.kcInputClass!}" name="username" value="${(login.username!'')}"  type="text" autofocus autocomplete="off"-->
    <#--                               aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"-->
    <#--                        />-->

    <#--                        <#if messagesPerField.existsError('username','password')>-->
    <#--                            <span id="input-error" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">-->
    <#--                                    ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}-->
    <#--                            </span>-->
    <#--                        </#if>-->

    <#--                    </div>-->
    <#--                </#if>-->

    <#--                <div class="${properties.kcFormGroupClass!}">-->
    <#--                    <label for="password" class="${properties.kcLabelClass!}">${msg("password")}</label>-->

    <#--                    <input tabindex="2" id="password" class="${properties.kcInputClass!}" name="password" type="password" autocomplete="off"-->
    <#--                           aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"-->
    <#--                    />-->

    <#--                    <#if usernameHidden?? && messagesPerField.existsError('username','password')>-->
    <#--                        <span id="input-error" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">-->
    <#--                                ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}-->
    <#--                        </span>-->
    <#--                    </#if>-->

    <#--                </div>-->

    <#--                <div class="${properties.kcFormGroupClass!} ${properties.kcFormSettingClass!}">-->
    <#--                    <div id="kc-form-options">-->
    <#--                        <#if realm.rememberMe && !usernameHidden??>-->
    <#--                            <div class="checkbox">-->
    <#--                                <label>-->
    <#--                                    <#if login.rememberMe??>-->
    <#--                                        <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox" checked> ${msg("rememberMe")}-->
    <#--                                    <#else>-->
    <#--                                        <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox"> ${msg("rememberMe")}-->
    <#--                                    </#if>-->
    <#--                                </label>-->
    <#--                            </div>-->
    <#--                        </#if>-->
    <#--                        </div>-->
    <#--                        <div class="${properties.kcFormOptionsWrapperClass!}">-->
    <#--                            <#if realm.resetPasswordAllowed>-->
    <#--                                <span><a tabindex="5" href="${url.loginResetCredentialsUrl}">${msg("doForgotPassword")}</a></span>-->
    <#--                            </#if>-->
    <#--                        </div>-->

    <#--                  </div>-->

    <#--                  <div id="kc-form-buttons" class="${properties.kcFormGroupClass!}">-->
    <#--                      <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>-->
    <#--                      <input tabindex="4" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" name="login" id="kc-login" type="submit" value="${msg("doLogIn")}"/>-->
    <#--                  </div>-->
    <#--            </form>-->
    <#--        </#if>-->
    <#--        </div>-->

    <#--    </div>-->
    <#elseif section = "info" >
      <div class="mt-4 text-center"><span>Don't have an account yet? </span><a
                class="text-indigo-600 hover:underline" href="${url.registrationUrl}">Sign up</a></div>
    <#elseif section = "socialProviders" >
        <#if realm.password && social.providers??>
          <div id="kc-social-providers" class="${properties.kcFormSocialAccountSectionClass!}">
            <hr/>
            <h4>${msg("identity-provider-login-label")}</h4>

            <ul class="${properties.kcFormSocialAccountListClass!} <#if social.providers?size gt 3>${properties.kcFormSocialAccountListGridClass!}</#if>">
                <#list social.providers as p>
                  <a id="social-${p.alias}"
                     class="${properties.kcFormSocialAccountListButtonClass!} <#if social.providers?size gt 3>${properties.kcFormSocialAccountGridItem!}</#if>"
                     type="button" href="${p.loginUrl}">
                      <#if p.iconClasses?has_content>
                        <i class="${properties.kcCommonLogoIdP!} ${p.iconClasses!}" aria-hidden="true"></i>
                        <span class="${properties.kcFormSocialAccountNameClass!} kc-social-icon-text">${p.displayName!}</span>
                      <#else>
                        <span class="${properties.kcFormSocialAccountNameClass!}">${p.displayName!}</span>
                      </#if>
                  </a>
                </#list>
            </ul>
          </div>
        </#if>
    </#if>

</@layout.registrationLayout>
