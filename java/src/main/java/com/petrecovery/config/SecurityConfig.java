package com.petrecovery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                // 允许访问的公共接口
                .antMatchers("/api/users/register", "/api/users/login", "/api/users/login-or-register", "/api/users/phone-login", "/api/users/send-code", "/api/users/test").permitAll()
                // 宠物相关接口 - 完全开放
                .antMatchers("/api/lost-pets/**").permitAll()
                .antMatchers("/api/lost-pets").permitAll()
                // 文件上传接口
                .antMatchers("/api/upload/**").permitAll()
                // 静态文件访问
                .antMatchers("/uploads/**").permitAll()
                // Swagger相关接口
                .antMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                // H2数据库控制台（如果使用）
                .antMatchers("/h2-console/**").permitAll()
                // 其他接口需要认证
                .anyRequest().authenticated()
            .and()
            .headers().frameOptions().disable(); // 允许H2控制台使用iframe
    }
}