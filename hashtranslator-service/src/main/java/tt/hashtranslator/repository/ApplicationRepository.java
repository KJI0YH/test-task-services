package tt.hashtranslator.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tt.hashtranslator.entity.Application;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {
    Application findApplicationById(String id);

    @Query("{ '$or': [ {'hashes.status': 'ACCEPTED'}, { '$and': [ {'hashes.status': 'PENDING'}, {'hashes.time': { '$lt': ?0 } } ] } ] }")
    List<Application> findApplicationsWithRawHashes(LocalDateTime thresholdTime);
}
