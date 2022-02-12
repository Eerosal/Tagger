package fi.eerosalla.web.tagger.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "tagger.password-encoder")
public class PasswordEncoderConfig {

    @JsonIgnore
    private static final int DEFAULT_STRENGTH = 7;

    private Integer bCryptStrength;

    @Bean
    @JsonIgnore
    public PasswordEncoder passwordEncoder() {
        Integer strength = this.getBCryptStrength();

        return new BCryptPasswordEncoder(
            strength == null ? DEFAULT_STRENGTH : strength
        );
    }

}
