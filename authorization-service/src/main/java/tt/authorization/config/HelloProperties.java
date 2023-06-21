package tt.authorization.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "authorization.hello")
@Data
public class HelloProperties {
    /**
     * Template for greeting message should have 2 %s placeholders: for name and hostname
     */
    private String template = "Hello %s from %s";
}
