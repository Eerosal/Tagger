package fi.eerosalla.web.tagger.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "db")
public class DatabaseConfig {

    private String jdbcStr;

    @Bean
    @SneakyThrows
    @JsonIgnore
    public ConnectionSource connectionSource() {
        return new JdbcConnectionSource(this.getJdbcStr());
    }

}
