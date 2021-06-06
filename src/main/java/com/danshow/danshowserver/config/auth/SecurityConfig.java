package com.danshow.danshowserver.config.auth;

import com.danshow.danshowserver.config.auth.custom.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //Session 비활성화
                .and()
                    .authorizeRequests()
                    .antMatchers("/","/css/**",
                            "/images/**","/js/**","/h2-console/**",
                            "/api/v1/file", //TODO : 테스트 끝나면 file 엔드포인트 제거
                            "/jwt", //TODO 임시 엔드포인트
                            "/swagger-ui.html/**", "/webjars/**","/v2/api-docs","/swagger-resources/**") //swagger 자꾸 안되서 넣었음
                .permitAll()
                    .antMatchers("/need-membership").hasRole("MEMBER")
                    .antMatchers("/auth/success").hasRole("GUEST")
                    .anyRequest().authenticated() //TODO 허용 url 수정하기
                .and()
                    .logout()
                        .logoutSuccessUrl("/");
                /*.and()
                    .oauth2Login()
                        .successHandler(customAuthenticationSuccessHandler)
                        .userInfoEndpoint()
                            .userService(customOAuth2UserService);*/
        //JWT 검증하는 필터
        http.addFilterBefore(new JwtAuthenticationFilter(tokenProvider),
                UsernamePasswordAuthenticationFilter.class);
    }
}
