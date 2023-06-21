package tt.hashtranslator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tt.hashtranslator.config.HelloProperties;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Service
public class HelloService {
    private final HelloProperties properties;

    @Autowired
    public HelloService(HelloProperties properties) {
        this.properties = properties;
    }

    public String makeGreetingMessageFor(String name) {
        String instanceId = "unknown";
        try {
            instanceId = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.warn("can't get hostname", e);
        }
        return String.format(properties.getTemplate(), name, instanceId);
    }
}
