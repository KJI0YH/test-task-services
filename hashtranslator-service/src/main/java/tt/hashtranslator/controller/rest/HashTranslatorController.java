package tt.hashtranslator.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tt.hashtranslator.client.authorization.AuthServiceClient;
import tt.hashtranslator.dto.ApplicationRequestDto;
import tt.hashtranslator.entity.Application;
import tt.hashtranslator.exception.ApplicationServiceException;
import tt.hashtranslator.exception.AuthServiceException;
import tt.hashtranslator.exception.MapperException;
import tt.hashtranslator.service.ApplicationService;

@RestController
@RequestMapping("/api")
public class HashTranslatorController {

    private final AuthServiceClient authServiceClient;
    private final ApplicationService applicationService;

    @Autowired
    public HashTranslatorController(AuthServiceClient authServiceClient, ApplicationService applicationService) {
        this.authServiceClient = authServiceClient;
        this.applicationService = applicationService;
    }

    @PostMapping("/applications")
    public ResponseEntity<String> createApplication(@RequestBody ApplicationRequestDto applicationRequestDto,
                                                    @RequestHeader("Authorization") String authorization) throws ApplicationServiceException, AuthServiceException, MapperException {
        authServiceClient.login(authorization);
        Application application = applicationService.saveApplication(applicationRequestDto);
        applicationService.processApplication(application);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(application.getId());
    }

    @GetMapping("/applications/{id}")
    ResponseEntity<Application> getApplication(@PathVariable String id,
                                               @RequestHeader("Authorization") String authorization) throws ApplicationServiceException, AuthServiceException {
        authServiceClient.login(authorization);
        Application application = applicationService.getApplication(id);
        return ResponseEntity.ok(application);
    }
}
