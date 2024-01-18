package tt.hashtranslator.client.authorization;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import tt.hashtranslator.exception.AuthServiceException;

@FeignClient(
        value = "auth-service",
        url = "${service.authorization.url}",
        path = "/api"
)
public interface AuthServiceClient {
    @GetMapping("/login")
    Void login(@RequestHeader("Authorization") String authorization) throws AuthServiceException;
}
