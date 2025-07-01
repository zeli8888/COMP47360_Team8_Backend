package team8.comp47360_team8_backend.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.authentication.AuthenticationProvider;

import java.util.Arrays;
import java.util.Collections;

/**
 * @Author : Ze Li
 * @Date : 02/06/2025 22:23
 * @Version : V1.0
 * @Description :
 */

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {
    public static final String[] excludedURLs = {"/v3/api-docs/**", "/swagger-ui/**"};

    @Value("${frontend.url}")
    private String frontendUrl;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.headers(headers -> headers.addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Origin", frontendUrl)));

        CorsConfiguration configuration = new CorsConfiguration();
        // configuration.setAllowedOrigins(Collections.singletonList(frontendUrl));
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        http.cors(cors -> cors.configurationSource(source));
        http.csrf(csrf -> csrf
                .csrfTokenRepository(new HttpSessionCsrfTokenRepository())
                // only generate tokens when needed.
                // Without this, spring security will generate a token for every request even if with HttpSessionCsrfTokenRepository
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .requireCsrfProtectionMatcher(request -> {
                    String requestUri = request.getRequestURI();
                    String contextPath = request.getContextPath();
                    // remove context path
                    requestUri = requestUri.substring(contextPath.length());
                    // protect get requests for endpoints starting with /user and /userplans
                    if (HttpMethod.GET.matches(request.getMethod())) {
                        // allow get requests for public user profile pictures
                        return !requestUri.startsWith("/user/picture/") &&
                                (requestUri.startsWith("/user") || requestUri.startsWith("/userplans"));
                    }
                    // allow post requests with /register, /login and /pois/recommendation endpoints
                    if (HttpMethod.POST.matches(request.getMethod())) {
                        return !("/register".equals(requestUri) || "/login".equals(requestUri) || "/pois/recommendation".equals(requestUri));
                    }
                    // protect other post/put/delete requests
                    return true;
                })
        );

        http.authorizeHttpRequests(
                authorizeRequest -> authorizeRequest.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(excludedURLs).permitAll()
                        .requestMatchers("/weather").permitAll()
                        .requestMatchers("/poitypes/**").permitAll()
                        .requestMatchers("/pois/**").permitAll()
                        .requestMatchers("/zones/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/picture/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/logout`").authenticated()
                        .requestMatchers(HttpMethod.GET, "/csrf-token").authenticated()
                        .requestMatchers("/user/**").authenticated()
                        .requestMatchers("/userplans/**").authenticated()
                        .anyRequest().permitAll()
        );

        http.formLogin(formLogin -> formLogin
//                .loginPage("/login")
                .successHandler((request, response, authentication) -> {response.setStatus(HttpServletResponse.SC_OK);})
                .failureHandler((request, response, exception) -> {response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);})
        );

        http.logout(logout -> logout
                // URL to trigger logout, Post request
                .logoutUrl("/logout")
                // Invalidate the session
                .invalidateHttpSession(true)
                // Delete the session cookie
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);})
        );

        http.authenticationProvider(authenticationProvider);

        return http.build();
    }
}
