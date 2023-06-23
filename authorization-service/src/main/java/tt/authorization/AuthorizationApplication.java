package tt.authorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("tt.authorization.config")
public class AuthorizationApplication {
    public static void main(String[] args) {
        SpringApplication.run(tt.authorization.AuthorizationApplication.class, args);
    }
}
