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
                      class="<#if messagesPerField.existsError('username','password')>input-invalid</#if> input input-md h-11 focus:ring-indigo-600 focus-within:ring-indigo-600 focus-within:border-indigo-600 focus:border-indigo-600"
                      type="text" name="username" autocomplete="off" placeholder="Username" value="admin">
                <#if messagesPerField.existsError('username','password')>
                  <div class="form-explain" style="opacity: 1; margin-top: 3px;">
                      ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}
                  </div>
                </#if>
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

    <#elseif section = "info" >
      <div class="mt-4 text-center"><span>Don't have an account yet? </span><a
                class="text-indigo-600 hover:underline" href="${url.registrationUrl}">Sign up</a></div>
    <#elseif section = "socialProviders" >
        <#if realm.password && social.providers??>
          <div class="mt-4 mb-8">
            <p class="mb-4 text-center">${msg("identity-provider-login-label")}</p>
            <ul class="flex justify-around">
                <#list social.providers as p>
                  <a type="button" href="${p.loginUrl}">
                    <img class="w-8 h-8" src="${url.resourcesPath}/img/social/social-${p.alias}.png" alt="${p.displayName}"/>
                  </a>
                </#list>
            </ul>
          </div>
        </#if>
    </#if>

</@layout.registrationLayout>
v