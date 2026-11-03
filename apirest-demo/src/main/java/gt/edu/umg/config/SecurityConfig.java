package gt.edu.umg.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${rsa.public-key}")
    private Resource publicKeyResource;

    @Value("${rsa.public-key-base64:}")
    private String publicKeyBase64;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public RSAPublicKey rsaPublicKey() throws Exception {
        String keyStr;
        if (publicKeyBase64 != null && !publicKeyBase64.isBlank()) {
            keyStr = new String(Base64.getDecoder().decode(publicKeyBase64), StandardCharsets.UTF_8);
        } else {
            try (InputStream is = publicKeyResource.getInputStream()) {
                keyStr = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        keyStr = keyStr
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
        byte[] decode = Base64.getDecoder().decode(keyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decode);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(spec);
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey) {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, RSAPublicKey rsaPublicKey) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                            .map(String::trim)
                            .filter(origin -> !origin.isEmpty())
                            .toList());
                    corsConfig.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                    corsConfig.setAllowedHeaders(java.util.List.of("*"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/pacientes/dashboard").hasAnyAuthority("DASHBOARD:READ", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/pacientes/**").hasAnyAuthority("PACIENTES:READ", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/pacientes/**").hasAnyAuthority("PACIENTES:CREATE", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/v1/pacientes/**").hasAnyAuthority("PACIENTES:UPDATE", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/v1/pacientes/**").hasAnyAuthority("PACIENTES:DELETE", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/medicos/**").hasAnyAuthority("MEDICOS:READ", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/medicos/**").hasAnyAuthority("MEDICOS:CREATE", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/v1/medicos/**").hasAnyAuthority("MEDICOS:UPDATE", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/v1/medicos/**").hasAnyAuthority("MEDICOS:DELETE", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/especialidades/**").hasAnyAuthority("ESPECIALIDADES:READ", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/especialidades/**").hasAnyAuthority("ESPECIALIDADES:CREATE", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/v1/especialidades/**").hasAnyAuthority("ESPECIALIDADES:UPDATE", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/v1/especialidades/**").hasAnyAuthority("ESPECIALIDADES:DELETE", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/citas/**").hasAnyAuthority("CITAS:READ", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/citas/**").hasAnyAuthority("CITAS:CREATE", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/v1/citas/**").hasAnyAuthority("CITAS:UPDATE", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/v1/citas/**").hasAnyAuthority("CITAS:DELETE", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/enfermedades/**").hasAnyAuthority("ENFERMEDADES:READ", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/enfermedades/**").hasAnyAuthority("ENFERMEDADES:CREATE", "SUPER_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/v1/enfermedades/**").hasAnyAuthority("ENFERMEDADES:DELETE", "SUPER_ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .decoder(jwtDecoder(rsaPublicKey))
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                ))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public org.springframework.core.convert.converter.Converter<org.springframework.security.oauth2.jwt.Jwt, org.springframework.security.authentication.AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return jwt -> {
            java.util.Set<org.springframework.security.core.GrantedAuthority> authorities = new java.util.HashSet<>();
            java.util.List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles != null) {
                roles.forEach(r -> {
                    authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(r));
                    if (!r.startsWith("ROLE_")) {
                        authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + r));
                    }
                });
            }
            java.util.List<String> permisos = jwt.getClaimAsStringList("permisos");
            if (permisos != null) {
                permisos.forEach(p -> authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(p)));
            }
            java.util.List<String> scopedAuthorities = jwt.getClaimAsStringList("authorities");
            if (scopedAuthorities != null) {
                scopedAuthorities.forEach(a -> authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(a)));
            }
            if (Boolean.TRUE.equals(jwt.getClaim("superAdmin"))) {
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("SUPER_ADMIN"));
            }
            return new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
        };
    }
}
