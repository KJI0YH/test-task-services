package tt.hashtranslator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tt.hashtranslator.data.Application;
import tt.hashtranslator.data.Hash;
import tt.hashtranslator.dto.ApplicationDto;
import tt.hashtranslator.exception.ApplicationServiceException;
import tt.hashtranslator.repository.ApplicationRepository;
import tt.hashtranslator.service.external.ExternalTranslatorDelegate;

import java.util.stream.Collectors;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ExternalTranslatorDelegate externalTranslator;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository, ExternalTranslatorDelegate externalTranslator) {
        this.applicationRepository = applicationRepository;
        this.externalTranslator = externalTranslator;
    }

    public Application getApplication(String id) throws ApplicationServiceException {
        Application application = applicationRepository.findApplicationById(id);
        if (application == null)
            throw new ApplicationServiceException("Application with id " + id + " not found");
        return application;
    }

    public Application saveApplication(ApplicationDto applicationDto) throws ApplicationServiceException {
        Application application = new Application();

        if (!isValidApplication(applicationDto))
            throw new ApplicationServiceException("Invalid application format");

        application.setHashes(applicationDto.getHashes()
                .stream()
                .map(Hash::new)
                .collect(Collectors.toList()));

        try {
            applicationRepository.save(application);
            return application;
        } catch (Exception e) {
            throw new ApplicationServiceException("Can not accept the application");
        }
    }

    @Async
    public void processApplication(Application application) {
        externalTranslator.translate(application);
    }

    public boolean isValidApplication(ApplicationDto applicationDto) {
        return applicationDto.getHashes() != null && !applicationDto.getHashes().isEmpty();
    }
}
