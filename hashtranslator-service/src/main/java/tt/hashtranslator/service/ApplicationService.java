package tt.hashtranslator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tt.hashtranslator.dto.ApplicationRequestDto;
import tt.hashtranslator.entity.Application;
import tt.hashtranslator.entity.Hash;
import tt.hashtranslator.exception.ApplicationServiceException;
import tt.hashtranslator.exception.MapperException;
import tt.hashtranslator.repository.ApplicationRepository;
import tt.hashtranslator.service.mapper.ApplicationMapperService;

import java.util.List;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ApplicationMapperService mapperService;
    private final HashService hashService;

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
        return application;
    }

    public Application saveApplication(ApplicationRequestDto applicationRequestDto) throws ApplicationServiceException, MapperException {
        Application application = mapperService.dtoToEntity(applicationRequestDto);
        try {
            return applicationRepository.save(application);
        } catch (Exception e) {
            throw new ApplicationServiceException("Can not save the application");
        }
    }

    public void processApplication(Application application) {
        List<Hash> hashes = application.getRawHashes();

        hashes.parallelStream()
                .forEach(hash -> hashService.processHash(application.getId(), hash));
    }
}
