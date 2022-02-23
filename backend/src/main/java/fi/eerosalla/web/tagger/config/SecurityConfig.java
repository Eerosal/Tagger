package fi.eerosalla.web.tagger.config;

import fi.eerosalla.web.tagger.security.JwtTokenFilter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

//https://www.toptal.com/spring/spring-security-tutorial

@Configuration
@ConfigurationProperties(prefix = "tagger.security")
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class SecurityConfig
    extends WebSecurityConfigurerAdapter {

    @Setter
    private Integer tokenLifetimeSeconds;

    public int getTokenLifetimeSeconds() {
        // default value 120 seconds / 2 minutes
        if (tokenLifetimeSeconds == null) {
            return 120;
        }

        return tokenLifetimeSeconds;
    }

    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(final JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // TODO: enable cors
        http.csrf().disable().cors().disable();

        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()

            .exceptionHandling()
            .authenticationEntryPoint(
                (request, response, ex) -> {
                    response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        ex.getMessage()
                    );
                }
            )
            .and()

            .authorizeRequests()
            .antMatchers("/authorize").permitAll()
            .anyRequest().authenticated();

        http.addFilterBefore(
            jwtTokenFilter,
            UsernamePasswordAuthenticationFilter.class
        );
    }
}
