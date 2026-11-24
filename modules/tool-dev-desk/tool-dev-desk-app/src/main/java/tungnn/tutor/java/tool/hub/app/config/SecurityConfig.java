package tungnn.tutor.java.tool.hub.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, HubProperties props) throws Exception {
        if (props.security().enabled()) {
            http.authorizeHttpRequests(a -> a.requestMatchers("/css/**").permitAll().anyRequest().authenticated())
                    .httpBasic(Customizer.withDefaults());
        } else {
            http.authorizeHttpRequests(a -> a.anyRequest().permitAll());
        }
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService(HubProperties props) {
        HubProperties.Security s = props.security();
        String password = s.enabled() ? s.password() : java.util.UUID.randomUUID().toString();
        return new InMemoryUserDetailsManager(
                User.withUsername(s.username()).password("{noop}" + password).roles("USER").build());
    }
}
