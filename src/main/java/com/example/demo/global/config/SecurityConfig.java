package com.example.demo.global.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/js/**", "/img/**",
                                "/user/login", "/user/signup", "/post").permitAll()
                        .requestMatchers("/user", "/user/myHistory", "/user/delete", "/user/draft", "/post/write", "/post/*/edit", "/post/*/comment/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/post/*", "/notice/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/user/pwPage").hasAnyRole("USER", "ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/user/myPage","/user/pwPage").hasAnyRole("USER", "ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/user/delete").hasAnyRole("USER", "KAKAO_USER", "ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )

                .formLogin(login -> login
                        .loginPage("/user/login")
                        .loginProcessingUrl("/user/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", false)
                        .failureHandler((request, response, authException) ->{
                            log.warn("로그인 실패: [{}] {} 요청 by {}", request.getMethod(), request.getRequestURI(), request.getUserPrincipal(), authException);
                            response.sendRedirect("/user/login?error=1");
                        })
                        .permitAll()
                )

                .passwordManagement(management -> management
                        .changePasswordPage("/user/pwPage")
                )

                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("Authentication failed: [{}] {} 요청 by {}", request.getMethod(), request.getRequestURI(), request.getUserPrincipal(), authException);
                            response.sendRedirect("/user/login");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.warn("Access Denied: [{}] {} 요청 by {}", request.getMethod(), request.getRequestURI(), request.getUserPrincipal(), accessDeniedException);
                            response.sendError(HttpServletResponse.SC_FORBIDDEN);
                        })
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );
        return http.build();
    }
}
