package tt.hashtranslator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@ConfigurationPropertiesScan("tt.hashtranslator.config")
@EnableFeignClients
@EnableAspectJAutoProxy
public class HashTranslatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(HashTranslatorApplication.class, args);
    }
}
