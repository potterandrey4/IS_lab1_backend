package itmo.andrey.lab1_backend.config;

import itmo.andrey.lab1_backend.filters.JwtAuthenticationFilter;
import itmo.andrey.lab1_backend.utils.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

	private final JwtTokenUtil jwtTokenUtil;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtTokenUtil jwtTokenUtil, JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtTokenUtil = jwtTokenUtil;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.cors().and()  // Включаем CORS
				.csrf().disable()  // Отключаем CSRF
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/auth/signin", "/auth/signup", "/auth/verify-token").permitAll()
						.anyRequest().authenticated()
				)
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				);

		// Добавляем JWT-фильтр перед стандартным фильтром аутентификации
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:4200"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return new CorsFilter(source);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}
