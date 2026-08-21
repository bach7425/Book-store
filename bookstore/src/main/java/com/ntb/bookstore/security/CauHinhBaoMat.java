package com.ntb.bookstore.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class CauHinhBaoMat {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cauHinh = new CorsConfiguration();
        cauHinh.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://127.0.0.1:5173",
                "https://fe-production-90c2.up.railway.app"));
        cauHinh.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cauHinh.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        cauHinh.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cauHinh);
        return source;
    }

    @Bean
    public SecurityFilterChain chuoiBoLoc(HttpSecurity http, JwtBoLocXacThuc jwtBoLocXacThuc) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new DiemVaoXacThucJwt(new ObjectMapper()))
                        .accessDeniedHandler(new JwtXuLyTuchoiTruyCap()))
                .authorizeHttpRequests(xacThuc -> xacThuc
                        .requestMatchers("/api/xac-thuc/**", "/api/sach/**", "/api/the-loai/**", "/api/tac-gia/**",
                                "/uploads/**", "/api/ai/**", "/api/test/**")
                        .permitAll()
                        .requestMatchers("/api/nguoi-dung/**", "/api/quan-tri/**", "/api/gio-hang/**",
                                "/api/don-hang/**", "/api/danh-gia/**", "/api/sach-yeu-thich/**",
                                "/api/thong-bao/**", "/api/ma-giam-gia/**")
                        .authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtBoLocXacThuc, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtBoLocXacThuc jwtBoLocXacThuc(JwtService jwtService,
            org.springframework.security.core.userdetails.UserDetailsService userDetailsService) {
        return new JwtBoLocXacThuc(jwtService, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
