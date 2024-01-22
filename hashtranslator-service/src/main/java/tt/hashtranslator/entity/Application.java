package tt.hashtranslator.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Document(collection = "application")
public class Application {
    @Id
    private String id;
    private List<Hash> hashes;

    @JsonIgnore
    public List<Hash> getRawHashes() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        return hashes.stream()
                .filter(hash -> hash.getStatus().equals(HashStatus.ACCEPTED) ||
                        (hash.getStatus().equals(HashStatus.PENDING) && hash.getTime().isBefore(fiveMinutesAgo)))
                .collect(Collectors.toList());
    }
}
