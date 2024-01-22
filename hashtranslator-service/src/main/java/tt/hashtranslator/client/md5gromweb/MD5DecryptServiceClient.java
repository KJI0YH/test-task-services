package tt.hashtranslator.client.md5gromweb;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        value = "md5-decrypt-service",
        url = "${service.md5-decrypt.url}"
)
public interface MD5DecryptServiceClient {
    @GetMapping(value = "/", params = "md5")
    String decrypt(@RequestParam("md5") String hash);
}
