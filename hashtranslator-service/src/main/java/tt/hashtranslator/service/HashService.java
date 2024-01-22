package tt.hashtranslator.service;

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

import java.time.LocalDateTime;

@Service
public class HashService {
    private final MongoTemplate mongoTemplate;
    private final MD5DecryptServiceClient md5DecryptService;

    @Autowired
    public HashService(MongoTemplate mongoTemplate, MD5DecryptServiceClient md5DecryptService) {
        this.mongoTemplate = mongoTemplate;
        this.md5DecryptService = md5DecryptService;
    }

    @Async
    public void processHash(String applicationId, Hash hash) {
        hash.setTime(LocalDateTime.now());
        hash.setStatus(HashStatus.PENDING);

        updateHash(applicationId, hash);

        Document doc = Jsoup.parse(md5DecryptService.decrypt(hash.getHash()));
        Element element = doc.selectFirst("p.word-break-all a.String");
        String result = element != null ? element.text() : "";

        hash.setResult(result);
        hash.setStatus(result.isEmpty() ? HashStatus.UNDECRYPTED : HashStatus.DECRYPTED);

        updateHash(applicationId, hash);
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
}
