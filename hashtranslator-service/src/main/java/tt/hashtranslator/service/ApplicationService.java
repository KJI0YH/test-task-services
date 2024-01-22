package tt.hashtranslator.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tt.hashtranslator.dto.ApplicationRequestDto;
import tt.hashtranslator.entity.Application;
import tt.hashtranslator.entity.Hash;
import tt.hashtranslator.entity.HashStatus;
import tt.hashtranslator.exception.ApplicationServiceException;
import tt.hashtranslator.exception.MapperException;
import tt.hashtranslator.repository.ApplicationRepository;
import tt.hashtranslator.service.mapper.ApplicationMapperService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ApplicationMapperService mapperService;
    private final HashService hashService;
    @Value("${service.hash-translator.hashes.timeout}")
    @JsonIgnore
    private int timeout;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository, ApplicationMapperService mapperService, HashService hashService) {
        this.applicationRepository = applicationRepository;
        this.mapperService = mapperService;
        this.hashService = hashService;
    }

    public Application getApplication(String id) throws ApplicationServiceException {
        Application application = applicationRepository.findApplicationById(id);
        if (application == null)
            throw new ApplicationServiceException("Application with id " + id + " not found");
        log.info("Find an application with id: " + application.getId());
        return application;
    }

    public Application saveApplication(ApplicationRequestDto applicationRequestDto) throws ApplicationServiceException, MapperException {
        Application application = mapperService.dtoToEntity(applicationRequestDto);
        try {
            application = applicationRepository.save(application);
            log.info("Save an application: " + application);
            return application;
        } catch (Exception e) {
            throw new ApplicationServiceException("Can not save the application");
        }
    }

    public List<Hash> getRawHashes(Application application) {
        LocalDateTime thresholdTime = LocalDateTime.now().minus(timeout, ChronoUnit.MILLIS);
        return application.getHashes().stream()
                .filter(hash -> hash.getStatus().equals(HashStatus.ACCEPTED) ||
                        (hash.getStatus().equals(HashStatus.PENDING) && hash.getTime().isBefore(thresholdTime))
                )
                .collect(Collectors.toList());
    }

    public List<Application> getUnprocessedApplication() {
        LocalDateTime thresholdTime = LocalDateTime.now().minus(timeout, ChronoUnit.MILLIS);
        return applicationRepository.findApplicationsWithRawHashes(thresholdTime);
    }

    public void processApplication(Application application) {
        List<Hash> hashes = getRawHashes(application);
        log.info("Start processing application with id: " + application.getId() + " with hashes number: " + hashes.size());

        hashes.parallelStream()
                .forEach(hash -> hashService.processHash(application.getId(), hash));
    }

    @Async("resupplyScheduler")
    @Scheduled(fixedRateString = "${service.hash-translator.resupply.fixed-rate}")
    public void resupplyingApplications() {
        log.info("Start resupplying");
        List<Application> applications = getUnprocessedApplication();
        if (applications.isEmpty()) {
            log.info("There are no applications for resupplying");
            return;
        }
        log.info("Resupplying " + applications.size() + " applications");
        applications.parallelStream()
                .forEach(this::processApplication);
    }
}
