package tt.hashtranslator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tt.hashtranslator.data.ApiResponse;
import tt.hashtranslator.exception.AuthServiceException;

import java.util.Optional;

@Service
public class AuthService {

    private final RestTemplate restTemplate;
    @Value("${service.authorization.url}")
    private String serviceUrl;

    @Autowired
    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void authorization(String authorization) throws AuthServiceException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorization);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<ApiResponse> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    serviceUrl,
                    HttpMethod.GET,
                    requestEntity,
                    ApiResponse.class);
        } catch (Exception e) {
            throw new AuthServiceException("Authorize service unavailable");
        }

        HttpStatus status = responseEntity.getStatusCode();
        ApiResponse apiResponse = Optional.ofNullable(responseEntity.getBody())
                .orElse(new ApiResponse(false, "Authorization server error"));
        if (status != HttpStatus.OK)
            throw new AuthServiceException("Authorization attempt failed: " + apiResponse.getMessage());
    }
}
