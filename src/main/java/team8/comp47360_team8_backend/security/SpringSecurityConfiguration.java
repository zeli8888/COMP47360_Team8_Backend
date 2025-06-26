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
        configuration.setAllowedOrigins(Collections.singletonList(frontendUrl));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        http.cors(cors -> cors.configurationSource(source));
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(
                authorizeRequest -> authorizeRequest.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(excludedURLs).permitAll()
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/logout`").permitAll()
                        .requestMatchers("/poitypes/**").permitAll()
                        .requestMatchers("/pois/**").permitAll()
                        .requestMatchers("/zones/**").permitAll()
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
                .logoutUrl("/logout") // URL to trigger logout
                .invalidateHttpSession(true) // Invalidate the session
                .deleteCookies("JSESSIONID") // Delete the session cookie
        );

        http.authenticationProvider(authenticationProvider);

        return http.build();
    }
}
