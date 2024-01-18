package tt.hashtranslator.client.md5decrypt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tt.hashtranslator.entity.Application;
import tt.hashtranslator.entity.Hash;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class Md5DecryptTranslator implements ExternalTranslatorDelegate {

    private final RestTemplate restTemplate;
    @Value("${service.translator.md5decrypt.hash_type}")
    private String hashType;
    @Value("${service.translator.md5decrypt.email}")
    private String email;
    @Value("${service.translator.md5decrypt.code}")
    private String code;
    @Value("${service.translator.md5decrypt.uri}")
    private String uri;
    @Value("${service.translator.md5decrypt.hash_per_request}")
    private Integer hashPerRequest;

    @Autowired
    public Md5DecryptTranslator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Application translate(Application application) {
        List<Hash> unprocessedHash = application.getUnprocessedHashes();

        while (!unprocessedHash.isEmpty()) {
            List<Hash> requestHash = unprocessedHash.subList(0, Math.min(hashPerRequest, unprocessedHash.size()));
            String url = createUrl(requestHash);
            String response = null;
            try {
                response = restTemplate.getForObject(url, String.class);
            } catch (Exception ignored) {
            }
            String[] results = parseResults(response, requestHash.size());
            for (int i = 0; i < requestHash.size(); i++) {
                String result = results[i];
                if (result == null || result.startsWith("ERROR CODE : 00"))
                    result = "";
                requestHash.get(i).setValue(result);
            }
            unprocessedHash = application.getUnprocessedHashes();
        }
        return application;
    }

    private String createUrl(List<Hash> hashes) {
        return uri +
                '?' +
                "hash=" + hashes.stream().map(Hash::getHash).collect(Collectors.joining(";")) + "&" +
                "hash_type=" + hashType + "&" +
                "code=" + code + "&" +
                "email=" + email;
    }

    private String[] parseResults(String response, int length) {
        return response != null
                ? response.split("\\;", -1)
                : new String[length];
    }
}
