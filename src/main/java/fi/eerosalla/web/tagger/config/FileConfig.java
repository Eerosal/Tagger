package fi.eerosalla.web.tagger.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "tagger.file")
public class FileConfig {

    private DataSize maxFileSize;

}
