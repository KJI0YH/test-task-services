package tt.hashtranslator.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hash {
    private String hash;
    private String value;

    public Hash(String hash) {
        this.hash = hash;
    }
}
