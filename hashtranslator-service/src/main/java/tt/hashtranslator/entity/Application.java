package tt.hashtranslator.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Document(collection = "application")
public class Application {
    @Id
    private String id;
    private List<Hash> hashes;

    public Map<String, String> getProcessedHashes() {
        return hashes
                .stream()
                .filter(hash -> hash.getValue() != null)
                .collect(Collectors.toMap(Hash::getHash, Hash::getValue));
    }

    public List<Hash> getUnprocessedHashes() {
        return hashes
                .stream()
                .filter(hash -> hash.getValue() == null)
                .collect(Collectors.toList());
    }

    public boolean isProcessed() {
        return getUnprocessedHashes().isEmpty();
    }
}
