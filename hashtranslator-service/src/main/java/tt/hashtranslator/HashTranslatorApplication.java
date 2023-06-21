package tt.hashtranslator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("tt.hashtranslator.config")
public class HashTranslatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(HashTranslatorApplication.class, args);
    }
}
