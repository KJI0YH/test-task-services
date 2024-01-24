package tt.hashtranslator.service.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tt.hashtranslator.dto.ApplicationRequestDto;
import tt.hashtranslator.entity.Application;
import tt.hashtranslator.entity.Hash;
import tt.hashtranslator.entity.HashStatus;
import tt.hashtranslator.exception.MapperException;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApplicationMapperService implements MapperService<Application, ApplicationRequestDto> {

    private final Pattern md5Pattern = Pattern.compile("^[a-fA-F0-9]{32}$");
    @Value("${service.hash-translator.hashes.max}")
    private int maxHashes;

    @Override
    public Application dtoToEntity(ApplicationRequestDto applicationRequestDto) throws MapperException {
        validate(applicationRequestDto);
        Application application = new Application();
        List<Hash> hashes = applicationRequestDto.getHashes().stream()
                .map(h -> {
                    Hash hash = new Hash(h);
                    hash.setStatus(HashStatus.ACCEPTED);
                    return hash;
                })
                .collect(Collectors.toList());
        application.setHashes(hashes);
        log.info("Convert application DTO: " + applicationRequestDto + " to entity: " + application);
        return application;
    }

    private void validate(ApplicationRequestDto applicationRequestDto) throws MapperException {
        List<String> hashes = applicationRequestDto.getHashes();
        if (hashes == null || hashes.isEmpty())
            throw new MapperException("Hash list is empty");
        if (hashes.size() > maxHashes)
            throw new MapperException("Too many hashes. Max number of hashes: " + maxHashes);
        StringBuilder builder = new StringBuilder();
        boolean error = false;
        for (String hash : hashes) {
            if (!isValidMd5Hash(hash)) {
                error = true;
                builder.append(hash).append(": is not a valid MD5 hash\n");
            }
        }
        if (error)
            throw new MapperException(builder.toString());
    }

    private boolean isValidMd5Hash(String hash) {
        return md5Pattern.matcher(hash).matches();
    }
}
