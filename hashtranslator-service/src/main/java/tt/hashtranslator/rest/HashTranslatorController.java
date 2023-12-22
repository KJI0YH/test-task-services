package tt.hashtranslator.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tt.hashtranslator.data.Application;
import tt.hashtranslator.dto.ApplicationDto;
import tt.hashtranslator.dto.HashResponseDto;
import tt.hashtranslator.exception.ApplicationServiceException;
import tt.hashtranslator.exception.AuthServiceException;
import tt.hashtranslator.exception.ExternalTranslatorException;
import tt.hashtranslator.service.ApplicationService;
import tt.hashtranslator.service.AuthService;

@RestController
@RequestMapping("/api")
public class HashTranslatorController {

    private final AuthService authService;
    private final ApplicationService applicationService;

    @Autowired
    public HashTranslatorController(AuthService authService, ApplicationService applicationService) {
        this.authService = authService;
        this.applicationService = applicationService;
    }

    @PostMapping("/applications")
    public ResponseEntity<String> createApplication(@RequestBody ApplicationDto applicationDto,
                                                    @RequestHeader("Authorization") String authorization) throws AuthServiceException, ApplicationServiceException, ExternalTranslatorException {
        authService.authorization(authorization);
        Application application = applicationService.saveApplication(applicationDto);
        applicationService.processApplication(application);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(application.getId());
    }

    @GetMapping("/applications/{id}")
    ResponseEntity<HashResponseDto> getApplication(@PathVariable String id,
                                                   @RequestHeader("Authorization") String authorization) throws AuthServiceException, ApplicationServiceException {
        authService.authorization(authorization);
        Application application = applicationService.getApplication(id);
        return ResponseEntity.ok(new HashResponseDto(application.getProcessedHashes()));
    }
}
