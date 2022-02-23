package fi.eerosalla.web.tagger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("fi.eerosalla.web.tagger.*")
@SpringBootApplication
public class TaggerApplication {

    public static void main(final String[] args) {
        SpringApplication.run(TaggerApplication.class, args);
    }

}
