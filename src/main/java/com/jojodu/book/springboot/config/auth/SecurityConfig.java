package com.jojodu.book.springboot.config.auth;

import com.jojodu.book.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.userinfo.CustomUserTypesOAuth2UserService;

/**
 * Spring Security + OAuth2 로그인 설정
 */
@RequiredArgsConstructor
@EnableWebSecurity //Spring Security 설정등을 활성화시켜줍니다.
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    protected void configure(HttpSecurity http) throws Exception{
        http.csrf().disable().headers().frameOptions().disable()
                .and().authorizeRequests()
                .antMatchers("/","/css/**","/images/**","/js/**","/h2-console/**").permitAll()
                .antMatchers("/api/v1/**").hasAnyRole(Role.GUEST.name(),Role.USER.name())
                .anyRequest().authenticated()
                .and().logout().logoutSuccessUrl("/")
                .and().oauth2Login().userInfoEndpoint().userService(customOAuth2UserService);
    }
    /**
     * .headers().frameOptions().disable() = h2-console 화면을 사용하기 위해 해당옵션들을 disable합니다.
     * .authorizeRequests() = URL별 권한 관리를 설정하는 옵션의 시작점입니다. 이후 antMatchers() 옵션을 사용가능합니다.
     * .antMatchers() = 권한 관리 대상을 지정하는 옵션입니다.URL,HTTP 메소드별로 관리가 가능합니다.
     * .anyRequest().authenticated() = 설정값 이외의 url에 대한 설정입니다. 이외의 url은 모두 인증된 사용자에게만 허용합니다.
     * .and().logout().logoutSuccessUrl("/") = 로그아웃 성공시 "/"로 이동
     * .oauth2Login() = oauth2 로그인 설정의 시작점
     * .userInfoEndpoint() = oauth2 로그인 성공 후 사용자정보를 가져올때 설정담당
     * .userService() = 소셜로그인성공후 후속조치를 진행할 userService 인터페이스의 구현제등록
     * 소셜서비스에서 사용자정보를 가져온 상태에서 추가로 진행하고자하는 기능을 명시할 수 있음
     */


}
