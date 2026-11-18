package az.iktex.organization.structure.testders.security;

import az.iktex.organization.structure.testders.security.jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final RestAuthenticationEntryPoint entryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    private static final String[] PUBLIC_URLS = {
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/error"
    };

    private static final String[] EDUCATION_URLS = {
            "/api/students/**",
            "/api/courses/**",
            "/api/authors/**",
            "/api/books/**"
    };

    private static final String[] ORGANIZATION_URLS = {
            "/api/departments/**",
            "/api/employees/**"
    };

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // JWT Authorization header-de gonderilir.
                // Brauzer tokeni avtomatik gondermediyi ucun
                // cookie esasli CSRF qorumasina ehtiyac yoxdur.
                .csrf(AbstractHttpConfigurer::disable)

                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .authorizeHttpRequests(auth -> auth

                        // Public endpointler
                        .requestMatchers(PUBLIC_URLS)
                        .permitAll()

                        // Admin endpointleri
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // Student, Course, Author, Book
                        .requestMatchers(
                                HttpMethod.GET,
                                EDUCATION_URLS
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.POST,
                                EDUCATION_URLS
                        )
                        .hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                EDUCATION_URLS
                        )
                        .hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                EDUCATION_URLS
                        )
                        .hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                EDUCATION_URLS
                        )
                        .hasRole("ADMIN")

                        // Department ve Employee
                        .requestMatchers(
                                HttpMethod.GET,
                                ORGANIZATION_URLS
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.POST,
                                ORGANIZATION_URLS
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                ORGANIZATION_URLS
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                ORGANIZATION_URLS
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                ORGANIZATION_URLS
                        )
                        .hasRole("ADMIN")

                        // User endpointleri
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users/**"
                        )
                        .hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers("/api/users/**")
                        .hasRole("ADMIN")

                        // Qalan endpointler
                        .anyRequest()
                        .authenticated()
                )

                // JWT filter inteqrasiyasi
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // JWT filter-in iki defe islemesinin qarsisini alir
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter>
    jwtFilterRegistration(JwtAuthenticationFilter filter) {

        FilterRegistrationBean<JwtAuthenticationFilter>
                registration = new FilterRegistrationBean<>(filter);

        registration.setEnabled(false);

        return registration;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }
}