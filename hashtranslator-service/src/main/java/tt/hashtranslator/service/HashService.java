package tt.hashtranslator.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tt.hashtranslator.client.md5gromweb.MD5DecryptServiceClient;
import tt.hashtranslator.entity.Application;
import tt.hashtranslator.entity.Hash;
import tt.hashtranslator.entity.HashStatus;
import tt.hashtranslator.exception.MD5DecoderServiceException;

import java.time.LocalDateTime;

@Service
@Slf4j
public class HashService {
    private final MongoTemplate mongoTemplate;
    private final MD5DecryptServiceClient md5DecryptService;

    @Autowired
    public HashService(MongoTemplate mongoTemplate, MD5DecryptServiceClient md5DecryptService) {
        this.mongoTemplate = mongoTemplate;
        this.md5DecryptService = md5DecryptService;
    }

    @Async("applicationExecutor")
    public void processHash(String applicationId, Hash hash) {
        if (isProcessedHash(hash)) return;
        hash.setTime(LocalDateTime.now());
        hash.setStatus(HashStatus.PENDING);
        log.info("Start processing hash: " + hash.getHash() + " from application with id: " + applicationId);

        updateHash(applicationId, hash);

        try {
            Document doc = Jsoup.parse(md5DecryptService.decrypt(hash.getHash()));
            Element element = doc.selectFirst("p.word-break-all a.String");
            String result = element != null ? element.text() : "";

            hash.setResult(result);
            hash.setStatus(result.isEmpty() ? HashStatus.UNDECRYPTED : HashStatus.DECRYPTED);
            log.info("Finish processing hash: " + hash.getHash() + " from application with id: " + applicationId);

            updateHash(applicationId, hash);
        } catch (MD5DecoderServiceException ignore) {
        }
    }

    public void updateHash(String applicationId, Hash hash) {
        mongoTemplate.updateFirst(
                Query.query(Criteria
                        .where("id").is(applicationId)
                        .and("hashes.hash").is(hash.getHash())),
                new Update()
                        .set("hashes.$.time", hash.getTime())
                        .set("hashes.$.status", hash.getStatus())
                        .set("hashes.$.result", hash.getResult()),
                Application.class
        );
    }

    private boolean isProcessedHash(Hash hash) {
        return hash.getStatus() == HashStatus.DECRYPTED ||
                hash.getStatus() == HashStatus.UNDECRYPTED;
    }
}
