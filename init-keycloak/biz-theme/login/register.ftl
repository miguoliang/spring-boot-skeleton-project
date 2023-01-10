<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('firstName','lastName','email','username','password','password-confirm'); section>
    <#if section = "header">
      <div class="mb-8"><h3 class="mb-1">Sign Up</h3>
        <p>And lets get started with your free trial</p></div>
    <#elseif section = "form">
      <form action="${url.registrationAction}" method="post">
        <div class="form-container vertical">
          <div class="form-item vertical"><label class="form-label mb-2">Username</label>
            <div class=""><input
                      class="<#if messagesPerField.existsError('username')>input-invalid</#if> input input-md h-11 focus:ring-indigo-600 focus-within:ring-indigo-600 focus-within:border-indigo-600 focus:border-indigo-600"
                      type="text" name="username" autocomplete="off" placeholder="Username" value="admin1">
                <#if messagesPerField.existsError('username')>
                  <div class="form-explain" style="opacity: 1; margin-top: 3px;">
                      ${kcSanitize(messagesPerField.get('username'))?no_esc}
                  </div>
                </#if></div>
          </div>
          <div class="form-item vertical"><label class="form-label mb-2">Email</label>
            <div class=""><input
                      class="<#if messagesPerField.existsError('email')>input-invalid</#if> input input-md h-11 focus:ring-indigo-600 focus-within:ring-indigo-600 focus-within:border-indigo-600 focus:border-indigo-600"
                      type="email" name="email" autocomplete="off" placeholder="Email" value="test@testmail.com">
                <#if messagesPerField.existsError('email')>
                  <div class="form-explain" style="opacity: 1; margin-top: 3px;">
                      ${kcSanitize(messagesPerField.get('email'))?no_esc}
                  </div>
                </#if></div>
          </div>
          <div class="form-item vertical"><label class="form-label mb-2">Password</label>
            <div class=""><span class="input-wrapper "><input
                        class="<#if messagesPerField.existsError('password','password-confirm')>input-invalid</#if> input input-md h-11 focus:ring-indigo-600 focus-within:ring-indigo-600 focus-within:border-indigo-600 focus:border-indigo-600"
                        type="password" name="password" autocomplete="new-password" placeholder="Password"
                        value="123Qwe1"
                        style="padding-right: 2.25rem;"><div class="input-suffix-end"><span
                          class="cursor-pointer text-xl"><svg stroke="currentColor" fill="none" stroke-width="0"
                                                              viewBox="0 0 24 24" height="1em" width="1em"
                                                              xmlns="http://www.w3.org/2000/svg"><path
                              stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"></path></svg></span>
                                <#if messagesPerField.existsError('password','password-confirm')>
                                  <div class="form-explain" style="opacity: 1; margin-top: 3px;">
                                    ${kcSanitize(messagesPerField.get('password'))?no_esc}
                                  </div></#if>
                </div></span>
            </div>
          </div>
          <div class="form-item vertical"><label class="form-label mb-2">Confirm Password</label>
            <div class=""><span class="input-wrapper "><input
                        class="<#if messagesPerField.existsError('password-confirm')>input-invalid</#if>input input-md h-11 focus:ring-indigo-600 focus-within:ring-indigo-600 focus-within:border-indigo-600 focus:border-indigo-600"
                        type="password" name="password-confirm" autocomplete="off" placeholder="Confirm Password"
                        value="123Qwe1" style="padding-right: 2.25rem;"><div class="input-suffix-end"><span
                          class="cursor-pointer text-xl"><svg stroke="currentColor" fill="none" stroke-width="0"
                                                              viewBox="0 0 24 24" height="1em" width="1em"
                                                              xmlns="http://www.w3.org/2000/svg"><path
                              stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"></path></svg></span></div></span>
                <#if messagesPerField.existsError('password-confirm')>
                  <div class="form-explain" style="opacity: 1; margin-top: 3px;">
                    ${kcSanitize(messagesPerField.get('password-confirm'))?no_esc}
                  </div></#if>
            </div>
          </div>
          <button class="button bg-indigo-600 hover:bg-indigo-500 active:bg-indigo-700 text-white radius-round h-11 px-8 py-2 w-full"
                  type="submit">Sign Up
          </button>
          <div class="mt-4 text-center"><span>Already have an account? </span><a class="text-indigo-600 hover:underline"
                                                                                 href="${url.loginUrl}">Sign in</a>
          </div>
        </div>
      </form>
    </#if>
</@layout.registrationLayout>