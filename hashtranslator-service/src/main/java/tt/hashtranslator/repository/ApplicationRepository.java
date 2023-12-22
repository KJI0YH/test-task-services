package tt.hashtranslator.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tt.hashtranslator.data.Application;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {
    Application findApplicationById(String id);
}
